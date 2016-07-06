package com.example.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MySurfaceView extends GLSurfaceView {

    public boolean drawWhatFlag = true;

    private MyRenderer myRenderer;

    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        myRenderer = new MyRenderer();
        setRenderer(myRenderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    private class MyRenderer implements Renderer {

        private CircleFace circleFace;
        private CircleLine circleLine;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            MatrixState.setInitStack();
            circleFace = new CircleFace(MySurfaceView.this);
            //circleLine = new CircleLine();

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

            GLES20.glViewport(0,0,width,height);
            float ratio = (float) width/height;
            MatrixState.setProjectFrustum(-ratio,ratio,-1f,1f,0.1f,100.0f);
            MatrixState.setCamera(0f,0f,8.0f,0f,0f,0f,0f,1.0f,0.0f);
        }

        @Override
        public void onDrawFrame(GL10 gl) {


            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);

            int textureId = initTexture(R.drawable.android_robot0);
            MatrixState.pushMatrix();
            if (drawWhatFlag){

            }else {

            }
            circleFace.drawSelf(textureId);
            MatrixState.popMatrix();
        }
    }

    public int initTexture(int drawableId)
    {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int textureId=textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);

        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try
        {
            bitmapTmp = BitmapFactory.decodeStream(is);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();
        return textureId;
    }

}
