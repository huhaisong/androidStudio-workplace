package com.example.aaaa.model;

import android.opengl.GLES20;

import com.example.aaaa.MySurfaceView;
import com.example.aaaa.util.MemUtil;
import com.example.aaaa.util.ShaderUtil;

import java.nio.FloatBuffer;

/**
 * Created by 111 on 2016/7/6.
 */
public class Rect {


    public FloatBuffer vertexBuffer, texCoorBuffer;
    private int mProgram;
    private int maPositionHandle, maTexCoorHandle;

    public final float vertices[] = new float[]{

            -1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,

            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f
    };

    public final float texCoors[] = new float[]{

            0, 0, 0, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 0, 0, 0
    };


    public Rect(MySurfaceView mv) {

        initData();
        initShader(mv);
    }

    private void initData() {

        vertexBuffer = MemUtil.makeFloatBuffer(vertices);
        texCoorBuffer = MemUtil.makeFloatBuffer(texCoors);
    }

    private void initShader(MySurfaceView mv) {

        String vertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        String fragShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(vertexShader, fragShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");

    }

    public void drawSelf(int textureId) {

        GLES20.glUseProgram(mProgram);
        // MatrixState.setInitStack();

        //GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, texCoorBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

       /* GLES20.glUseProgram(mProgram);

        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);

        GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, texCoorBuffer);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);*//*
        int sampleID = GLES20.glGetUniformLocation(mProgram, "sTexture");
        GLES20.glUniform1i(sampleID , 0);*//*
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);*/
    }
}
