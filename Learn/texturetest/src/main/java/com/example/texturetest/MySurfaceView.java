package com.example.texturetest;

import java.io.IOException;
import java.io.InputStream;

import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import android.opengl.GLES20;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class MySurfaceView extends GLSurfaceView {
    public final float TOUCH_SCALE_FACTOR = 180.0f / 320;//�Ƕ����ű���
    private SceneRenderer mRenderer;//������Ⱦ��

    private float mPreviousY;//�ϴεĴ���λ��Y���
    private float mPreviousX;//�ϴεĴ���λ��X���

    int textureCTId;
    int textureREId;
    int currTextureId;

    TextureRect[] texRect = new TextureRect[3];
    int trIndex = 2;

    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        mRenderer = new SceneRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;
                float dx = x - mPreviousX;
                for (TextureRect tr : texRect) {
                    tr.yAngle += dx * TOUCH_SCALE_FACTOR;
                    tr.zAngle += dy * TOUCH_SCALE_FACTOR;
                }
        }
        mPreviousY = y;
        mPreviousX = x;
        return true;
    }

    private class SceneRenderer implements Renderer {
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            texRect[trIndex].drawSelf(currTextureId);
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //�����Ӵ���С��λ�� 
            GLES20.glViewport(0, 0, width, height);
            //����GLSurfaceView�Ŀ�߱�
            float ratio = (float) width / height;
            //���ô˷����������͸��ͶӰ����
            MatrixState.setProject(-ratio, ratio, -1, 1, 1, 10);
            //���ô˷������������9����λ�þ���
            MatrixState.setCamera(0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            texRect[0] = new TextureRect(MySurfaceView.this, 1, 1);
            texRect[1] = new TextureRect(MySurfaceView.this, 4, 2);
            texRect[2] = new TextureRect(MySurfaceView.this, 4, 4);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            textureCTId = initTexture(false,R.drawable.robot);
            textureREId = initTexture(true,R.drawable.robot);
            currTextureId = textureREId;
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }
    }
    public int initTexture(boolean isRepeat,int drawableId)//textureId
    {
        int[] textures = new int[1];
        GLES20.glGenTextures
                (
                        1,
                        textures,
                        0
                );
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);


        if (isRepeat) {
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        } else {
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }

        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        GLUtils.texImage2D
                (
                        GLES20.GL_TEXTURE_2D,   //�������ͣ���OpenGL ES�б���ΪGL10.GL_TEXTURE_2D
                        0,                      //����Ĳ�Σ�0��ʾ��ͼ��㣬�������Ϊֱ����ͼ
                        bitmapTmp,              //����ͼ��
                        0                      //����߿�ߴ�
                );
        bitmapTmp.recycle();
        return textureId;
    }
}
