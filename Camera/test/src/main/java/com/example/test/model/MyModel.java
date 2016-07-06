package com.example.test.model;

import android.opengl.GLES20;

import com.example.test.util.MatrixState;
import com.example.test.util.MemUtil;
import com.example.test.util.ShaderUtil;

import java.nio.FloatBuffer;

/**
 * Created by 111 on 2016/6/6.
 */
public class MyModel {


    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private String mVertexShader;
    private String mFragmentShader;
    private int mProgram;
    private int maPositionHandle;
    private int matexCoorHandle;
    private int muMVPMatrixHandle;


    public MyModel(MyGLSurfaceView mv) {

        initVertexData();
        initShaderData(mv);
    }

    private void initVertexData() {
        final float UNIT_SIZE = 0.3f;
        float vertexCoords[] = { // 按逆时针方向顺序:

                -8 * UNIT_SIZE, 8 * UNIT_SIZE, 0,
                -8 * UNIT_SIZE, -8 * UNIT_SIZE, 0,
                8 * UNIT_SIZE, -8 * UNIT_SIZE, 0,

                8 * UNIT_SIZE, -8 * UNIT_SIZE, 0,
                8 * UNIT_SIZE, 8 * UNIT_SIZE, 0,
                -8 * UNIT_SIZE, 8 * UNIT_SIZE, 0

        };

        float textureCoords[] = {
                0, 0, 0, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 0, 0, 0

        };

        textureBuffer = MemUtil.makeFloatBuffer(textureCoords);
        vertexBuffer = MemUtil.makeFloatBuffer(vertexCoords);
    }

    private void initShaderData(MyGLSurfaceView mv) {
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());

        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);

        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        matexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
       // muMVPMatrixHandle = GLES20.glGetAttribLocation(mProgram, "uMVPMatrix");  找了很久的错误

        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void draw(int texId) {

        GLES20.glUseProgram(mProgram);

        MatrixState.setInitStack();

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(matexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(matexCoorHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }
}
