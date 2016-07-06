package com.example.fbotest;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 111 on 2016/7/5.
 */
public class MyRenderer implements GLSurfaceView.Renderer{

    private float vertices[] = new float[]{

            -1.0f,1.0f,0.0f,
            -1.0f,-1.0f,0.0f,
            1.0f,1.0f,0.0f

            -1.0f,-1.0f,0.0f,
            1.0f,-1.0f,0.0f,
            1.0f,1.0f,0.0f

    };

    private float texCoors[] = new float[]{

            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,0.0f,

            0.0f,1.0f,
            1.0f,1.0f,
            1.0f,0.0f
    };

    FloatBuffer vertexBuffer,texCoorBuffer;
    public MyRenderer(){

        vertexBuffer = MemUtil.makeFloatBuffer(vertices);
        texCoorBuffer = MemUtil.makeFloatBuffer(texCoors);


    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
/*
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");*/

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }



}
