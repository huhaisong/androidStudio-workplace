package com.example.texturetest.model;

import static com.example.texturetest.util.ShaderUtil.createProgram;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.example.texturetest.util.MatrixState;
import com.example.texturetest.util.MemUtil;
import com.example.texturetest.util.ShaderUtil;


public class MyModel {
    int mProgram;//自定义渲染管线程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id
    int maTexCoorHandle; //顶点纹理坐标属性引用id
    String mVertexShader;//顶点着色器
    String mFragmentShader;//片元着色器
    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer mTexCoorBuffer;//顶点纹理坐标数据缓冲
    public MyModel(MySurfaceView mv) {
        initVertexData();
        initShader(mv);
    }

    public void initVertexData() {
        float vertices[] = new float[]
                {
                        -1.0f,1.0f, 0,
                        -1.0f, -1.0f, 0,
                        1.0f, -1.0f, 0,

                        1.0f, -1.0f, 0,
                        -1.0f, 1.0f, 0  ,
                        1.0f, 1.0f, 0
                };
        float texCoor[] = new float[]
                {
                        0, 0, 0, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 0, 0, 1.0f, 0
                };
        mTexCoorBuffer = MemUtil.makeFloatBuffer(texCoor);
        mVertexBuffer = MemUtil.makeFloatBuffer(vertices);
    }

    public void initShader(MySurfaceView mv) {
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf(int texId) {
        GLES20.glUseProgram(mProgram);

        MatrixState.setInitStack();

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mTexCoorBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }
}