package com.example.a111.opengl_fbo2.model;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.a111.opengl_fbo2.util.MemUtil;
import com.example.a111.opengl_fbo2.util.ShaderUtil;

import java.nio.FloatBuffer;

/**
 * Created by 111 on 2016/7/4.
 */
public class Rect {

    FloatBuffer mVertexBuffer;
    FloatBuffer mTexCoorBuffer;

    String mVertexShader;
    String mFragmentShader;

    int mProgram;
    int maPositionHandle;
    int maTexCoorHandle;


    public Rect(Context context){

        initVertexData();
        initShader(context);
    }

    private void initVertexData() {
        float vertices[] = new float[]{

                -1.0f,1.0f,0.0f,
                -1.0f,-1.0f,0.0f,
                1.0f,1.0f,0.0f,

                1.0f,1.0f,0.0f,
                -1.0f,-1.0f,0.0f,
                1.0f,-1.0f,0.0f
        };

        float texCoor[] = new float[]{

                0.0f,0.0f,
                0.0f,1.0f,
                1.0f,0.0f,

                1.0f,0.0f,
                0.0f,1.0f,
                1.0f,1.0f
        };

        mVertexBuffer = MemUtil.makeFloatBuffer(vertices);
        mTexCoorBuffer = MemUtil.makeFloatBuffer(texCoor);

    }

    private void initShader(Context context) {

        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_rect.sh", context.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_rect.sh", context.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
    }

    public void drawSelf(int texId){
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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,6);
    }
}
