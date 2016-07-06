package com.example.a3d_load.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.example.a3d_load.MySurfaceView;
import com.example.a3d_load.util.MatrixState;
import com.example.a3d_load.util.ShaderUtil;

public class LoadedObjectVertexOnly {
    int mProgram;
    int muMVPMatrixHandle;
    int maPositionHandle;
    String mVertexShader;
    String mFragmentShader;

    FloatBuffer mVertexBuffer;
    int vCount = 0;

    public LoadedObjectVertexOnly(MySurfaceView mv, float[] vertices) {
        initVertexData(vertices);
        initShader(mv);
    }

    public void initVertexData(float[] vertices) {
        vCount = vertices.length / 3;

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }

    public void initShader(MySurfaceView mv) {
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_color.sh", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_color.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
        //GLES20.glDrawElements(GLES20.GL_TRIANGLES, count, GLES20.GL_UNSIGNED_SHORT, IndicesBuffer);
    }
}
