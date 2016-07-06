package com.example.test.model2;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 111 on 2016/6/6.
 */
public class MyGLSurfaceView2 extends GLSurfaceView implements GLSurfaceView.Renderer {


    final String vertexShader =
            "uniform mat4 u_MVPMatrix;                \n"        // A constant representing the combined model/view/projection matrix.
                    + "attribute vec4 a_Position;     \n"        // Per-vertex position information we will pass in.
                    + "attribute vec4 a_Color;        \n"        // Per-vertex color information we will pass in.
                    + "varying vec4 v_Color;          \n"        // This will be passed into the fragment shader.
                    + "void main()                    \n"        // The entry point for our vertex shader.
                    + "{                              \n"
                    + "   v_Color = a_Color;          \n"        // Pass the color through to the fragment shader.
                    // It will be interpolated across the triangle.
                    + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
                    + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                    + "}                              \n";    // normalized screen coordinates.

    final String fragmentShader =
            "precision mediump float;                 \n"        // Set the default precision to medium. We don't need as high of a
                    // precision in the fragment shader.
                    + "varying vec4 v_Color;          \n"        // This is the color from the vertex shader interpolated across the
                    // triangle per fragment.
                    + "void main()                    \n"        // The entry point for our fragment shader.
                    + "{                              \n"
                    + "   gl_FragColor = v_Color;     \n"        // Pass the color directly through the pipeline.
                    + "}                              \n";

    private MyModel2 myModel2;

    private float[] mMVPMatrix = new float[16];

    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private int mMVPMatrixHandle;

    private int mPositionHandle;
    private int mColorHandle;
    private int mProgramHandle;

    private static final int BYTES_PER_FLOAT = 4;
    private final int POSITION_DATA_SIZE = 3;
    private final int COLOR_DATA_SIZE = 4;

    public MyGLSurfaceView2(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // TODO Auto-generated method stub
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //long time = SystemClock.uptimeMillis() % 10000L;
        //float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        GLES20.glUseProgram(mProgramHandle);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        //从着色器源程序中的顶点着色器中获取Position属性
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        //从着色器源程序中的顶点着色器中获取Color属性
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");

        Matrix.setIdentityM(mModelMatrix, 0);    //初始化mModelMatrix
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5.0f);   //移动
        Matrix.rotateM(mModelMatrix, 0, 45f, 1.0f, 1.0f, 1.0f);   //旋转
        drawCube(myModel2.getmCubePositions(), myModel2.getmCubeColors());
    }

    private void drawCube(final FloatBuffer cubePositionsBuffer, final FloatBuffer cubeColorsBuffer) {
        cubePositionsBuffer.position(0);
        cubeColorsBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, 0, cubePositionsBuffer);
        GLES20.glVertexAttribPointer(mColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, 0, cubeColorsBuffer);
        //开启顶点属性数组
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub

        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub

        myModel2 = new MyModel2();

        //GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = -0.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 0.5f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        int vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        int fragmentShaderHandle = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        mProgramHandle = GLES20.glCreateProgram();
        if (mProgramHandle != 0) {
            GLES20.glAttachShader(mProgramHandle, vertexShaderHandle);
            GLES20.glAttachShader(mProgramHandle, fragmentShaderHandle);
            GLES20.glBindAttribLocation(mProgramHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(mProgramHandle, 1, "a_Color");
            GLES20.glLinkProgram(mProgramHandle);
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(mProgramHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(mProgramHandle);
                mProgramHandle = 0;
            }
        }
        if (mProgramHandle == 0) {
            throw new RuntimeException("failed to create program");
        }
    }

    public int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        if (shader != 0) {
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        if (shader == 0) {
            throw new RuntimeException("failed to creating vertex shader");
        }
        return shader;
    }
}
