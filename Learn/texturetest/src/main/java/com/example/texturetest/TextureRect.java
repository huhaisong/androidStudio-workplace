package com.example.texturetest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.texturetest.util.MemUtil;

public class TextureRect {
    int mProgram;
    int muMVPMatrixHandle;
    int maPositionHandle;
    int maTexCoorHandle;
    String mVertexShader;
    String mFragmentShader;
    static float[] mMMatrix = new float[16];

    FloatBuffer mVertexBuffer;
    FloatBuffer mTexCoorBuffer;
    ShortBuffer mIndexBuffer;
    int vCount = 0;
    float xAngle = 0;
    float yAngle = 0;
    float zAngle = 0;

    float sRange;
    float tRange;

    public TextureRect(MySurfaceView mv, float sRange, float tRange) {
        this.sRange = sRange;
        this.tRange = tRange;
        initVertexData();
        initShader(mv);
    }

    public void initVertexData() {
        vCount = 6;
        final float UNIT_SIZE = 0.3f;
        float vertices[] = new float[]
                {
                        -4 * UNIT_SIZE, 4 * UNIT_SIZE, 0,
                        -4 * UNIT_SIZE, -4 * UNIT_SIZE, 0,
                        4 * UNIT_SIZE, -4 * UNIT_SIZE, 0,
                        4 * UNIT_SIZE, 4 * UNIT_SIZE, 0
                };
        mVertexBuffer = MemUtil.makeFloatBuffer(vertices);
        float texCoor[] = new float[]
                {
                    /*0,0, 0,tRange, sRange,tRange,
  	      		sRange,tRange, sRange,0, 0,0*/
                        0, 0, 0.0f, 4f, 4.0f, 4.0f,
                        0, 0
                };
        mTexCoorBuffer = MemUtil.makeFloatBuffer(texCoor);

        short index[] = new short[]{
                0,1,2,
                0,2,3
        };
        mIndexBuffer = MemUtil.makeShortBuffer(index);
    }

    public void initShader(MySurfaceView mv) {
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf(int texId) {
        GLES20.glUseProgram(mProgram);
        Matrix.setRotateM(mMMatrix, 0, 0, 0, 1, 0);
        Matrix.translateM(mMMatrix, 0, 0, 0, 1);
        Matrix.rotateM(mMMatrix, 0, yAngle, 0, 1, 0);
        Matrix.rotateM(mMMatrix, 0, zAngle, 0, 0, 1);
        Matrix.rotateM(mMMatrix, 0, xAngle, 1, 0, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(mMMatrix), 0);
        GLES20.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mVertexBuffer
                );
        GLES20.glVertexAttribPointer
                (
                        maTexCoorHandle,
                        2,
                        GLES20.GL_FLOAT,
                        false,
                        2 * 4,
                        mTexCoorBuffer
                );
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES,6,GLES20.GL_UNSIGNED_SHORT,mIndexBuffer);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }
}