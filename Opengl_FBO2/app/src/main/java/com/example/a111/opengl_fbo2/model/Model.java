package com.example.a111.opengl_fbo2.model;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.example.a111.opengl_fbo2.util.MatrixState;
import com.example.a111.opengl_fbo2.util.MemUtil;
import com.example.a111.opengl_fbo2.util.ShaderUtil;

import java.nio.FloatBuffer;

/**
 * Created by 111 on 2016/7/4.
 */
public class Model {

    private static final String TAG = "Model";
    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /**
     * Store the projection matrix. This is used to project the scene onto a 2D viewport.
     */
    private float[] mProjectionMatrix = new float[16];

    /**
     * Allocate storage for the final combined matrix. This will be passed into the shader program.
     */
    private float[] mMVPMatrix = new float[16];

    private final int mPositionDataSize = 3;
    private final int mColorDataSize = 4;
    private final int mTextureCoordinateDataSize = 2;

    private float[] mModelMatrix = new float[16];

    private FloatBuffer mCubePositions;
    private FloatBuffer mCubeTextureCoordinates;
    private Context mContext;
    private int mProgramHandle;
    private int mPositionHandle, mTextureCoordinateHandle;

    public Model(Context context) {
        this.mContext = context;
        initData();
        initShader();
    }

    private void initData() {
        final float[] cubePositionData =
                {
                        // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
                        // if the points are counter-clockwise we are looking at the "front". If not we are looking at
                        // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
                        // usually represent the backside of an object and aren't visible anyways.

                        // Front face
                        -1.0f, 1.0f, 0.0f,
                        -1.0f, -1.0f, 0.0f,
                        1.0f, 1.0f, 0.0f,

                        -1.0f, -1.0f, 0.0f,
                        1.0f, -1.0f, 0.0f,
                        1.0f, 1.0f, 0.0f,
                };

        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
        // What's more is that the texture coordinates are the same for every face.
        final float[] cubeTextureCoordinateData =
                {
                        // Front face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                };

        // Initialize the buffers.
        mCubePositions = MemUtil.makeFloatBuffer(cubePositionData);
        mCubeTextureCoordinates = MemUtil.makeFloatBuffer(cubeTextureCoordinateData);
    }

    private void initShader() {

        String vertexShader = ShaderUtil.loadFromAssetsFile("vertex_rect.sh", mContext.getResources());
        String fragmentShader = ShaderUtil.loadFromAssetsFile("frag_rect.sh", mContext.getResources());
        mProgramHandle = ShaderUtil.createProgram(vertexShader, fragmentShader);
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
    }

    public void drawSelf(int texture) {

        GLES20.glUseProgram(mProgramHandle);

        Matrix.setIdentityM(mModelMatrix, 0);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        //GLES20.glUniform1i(mTextureUniformHandle, 0);

        // Pass in the position information
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize,
                GLES20.GL_FLOAT, false, 0, mCubePositions);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the texture coordinate information
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize,
                GLES20.GL_FLOAT, false, 0, mCubeTextureCoordinates);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }
}
