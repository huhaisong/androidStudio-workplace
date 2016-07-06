package com.example.a111.opengl_fbo2;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;


import com.example.a111.opengl_fbo2.util.MemUtil;
import com.example.a111.opengl_fbo2.util.ShaderUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MySurfaceView extends GLSurfaceView {

    private MyRenderer myRenderer;

    public MySurfaceView(Context context) {
        super(context);

        this.setEGLContextClientVersion(2);
        myRenderer = new MyRenderer();
        setRenderer(myRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    class MyRenderer implements Renderer {

        Triangle triangle;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            triangle = new Triangle();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            triangle.drawSelf();
        }
    }

    class Triangle {

        float[] vertics = new float[]{
                -1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f
        };
        float[] colors = new float[]{
                1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f
        };

        private FloatBuffer verticsBuffer, colorsBuffer;
        int mProgram;
        int maPositionHandle;
        int maColorHandle;

        String fragShader;
        String vertShader;

        public Triangle() {
            initData();
            initShader();
        }

        private void initData() {

            verticsBuffer = MemUtil.makeFloatBuffer(vertics);
            colorsBuffer = MemUtil.makeFloatBuffer(colors);
        }

        private void initShader() {

            fragShader = ShaderUtil.loadFromAssetsFile("frag_rect.sh", getResources());
            vertShader = ShaderUtil.loadFromAssetsFile("vertex_rect.sh", getResources());
            mProgram = ShaderUtil.createProgram(vertShader, fragShader);
            maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
           // maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        }

        public void drawSelf() {
            GLES20.glUseProgram(mProgram);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, verticsBuffer);
            /*GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, colorsBuffer);
            GLES20.glEnableVertexAttribArray(maColorHandle);*/
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        }
    }
}
