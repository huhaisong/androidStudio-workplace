package com.example.test;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by 111 on 2016/7/1.
 */
public class CircleFace {

    int mProgram;
    int muMVPMatrixHandle;
    int maPositionHandle;
    int maTexCoorHandle;
    int maNormalHandle;


    String mVertexShader;
    String mFragmentShader;

    FloatBuffer mVertexBuffer;
    FloatBuffer mTexCoorBuffer;
    FloatBuffer mNormalBuffer;

    int vCount;

    public CircleFace(MySurfaceView mv) {
        initData();
        initShader(mv);

    }

    private void initData() {
        vCount = 36*3;
        float angdegSpan = 360.0f / 36;
        float r = 20.0f;
        float[] vertices = new float[vCount * 3];
        float[] textures = new float[vCount * 2+6];

        int count = 0;
        int stCount = 0;
        for (float angdeg = 0; Math.ceil(angdeg) <360; angdeg += angdegSpan) {
            double angrad = Math.toRadians(angdeg);
            double angradNext = Math.toRadians(angdeg + angdegSpan);

            vertices[count++] = 0;
            vertices[count++] = 0;
            vertices[count++] = 0;

            vertices[count++] = (float) (-r * Math.sin(angrad));
            vertices[count++] = (float) (r * Math.cos(angrad));
            vertices[count++] = 0;
            vertices[count++] = (float) (-r * Math.sin(angradNext));
            vertices[count++] = (float) (r * Math.cos(angradNext));
            vertices[count++] = 0;

            textures[stCount++] = 0.5f;
            textures[stCount++] = 0.5f;
            textures[stCount++] = (float) (0.5f - 0.5f * Math.sin(angrad));
            textures[stCount++] = (float) (0.5f - 0.5f * Math.cos(angrad));
            textures[stCount++] = (float) (0.5f - 0.5f * Math.sin(angradNext));
            textures[stCount++] = (float) (0.5f - 0.5f * Math.cos(angradNext));
        }

        mVertexBuffer = MemUtil.makeFloatBuffer(vertices);
        float[] normals = new float[vertices.length];
        for (int i = 0; i < normals.length; i += 3) {
            normals[i] = 0;
            normals[i + 1] = 0;
            normals[i + 2] = 1;
        }
        mNormalBuffer = MemUtil.makeFloatBuffer(normals);
        mTexCoorBuffer = MemUtil.makeFloatBuffer(textures);
    }

    private void initShader(MySurfaceView mv) {

        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
    }

    public void drawSelf(int textureId){

        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle,1,false,MatrixState.getFinalMatrix(),0);
        GLES20.glVertexAttribPointer(maPositionHandle,3,GLES20.GL_FLOAT,false,3*4,mVertexBuffer);
        GLES20.glVertexAttribPointer(maTexCoorHandle,2,GLES20.GL_FLOAT,false,2*4,mTexCoorBuffer);
        GLES20.glVertexAttribPointer(maNormalHandle,3,GLES20.GL_FLOAT,false,3*4,mNormalBuffer);
        GLES20.glEnableVertexAttribArray(maNormalHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);
        GLES20.glEnableVertexAttribArray(maPositionHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);

        GLES20.glDrawArrays(GLES20.GL_LINES,0,vCount);
    }
}
