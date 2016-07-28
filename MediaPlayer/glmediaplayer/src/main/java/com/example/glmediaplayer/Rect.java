package com.example.glmediaplayer;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

/**
 * Created by 111 on 2016/7/6.
 */
public class Rect {


    public FloatBuffer vertexBuffer, texCoorBuffer;
    private int mProgram;
    private int maPositionHandle, maTexCoorHandle;

    private final String mVertexShader =
                    "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = aPosition;\n" +
                    "  vTextureCoord = aTextureCoord;\n" +
                    "}\n";

    private final String mFragmentShader =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";

    public final float vertices[] = new float[]{

            -1.0f, 1.0f, 0,
            -1.0f, -1.0f, 0,
            1.0f, -1.0f, 0,

            1.0f, -1.0f, 0,
            1.0f, 1.0f, 0,
            -1.0f, 1.0f, 0
    };

    public final float texCoors[] = new float[]{

            0, 0, 0, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 0, 0, 0
    };


    public Rect(MyGLSurfaceView mv) {

        initData();
        initShader(mv);
    }

    private void initData() {

        vertexBuffer = MemUtil.makeFloatBuffer(vertices);
        texCoorBuffer = MemUtil.makeFloatBuffer(texCoors);
    }

    private void initShader(MyGLSurfaceView mv) {

        String vertexShader = ShaderUtil.loadFromAssetsFile("vertex_rect.sh", mv.getResources());
        String fragShader = ShaderUtil.loadFromAssetsFile("frag_rect.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(vertexShader, fragShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");

    }

    public void drawSelf(int textureId) {

        GLES20.glUseProgram(mProgram);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, texCoorBuffer);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }
}