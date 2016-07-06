package com.example.a3d_load;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.example.a3d_load.model.LoadedObjectVertexAndIndex;
import com.example.a3d_load.model.LoadedObjectVertexNormal;
import com.example.a3d_load.model.LoadedObjectVertexNormalTexture;
import com.example.a3d_load.model.LoadedObjectVertexOnly;
import com.example.a3d_load.util.LoadUtil;
import com.example.a3d_load.util.LoadUtil2;
import com.example.a3d_load.util.MatrixState;

import java.io.IOException;
import java.io.InputStream;

public class MySurfaceView extends GLSurfaceView {
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;//�Ƕ����ű���
    private SceneRenderer mRenderer;//������Ⱦ��    

    private float mPreviousY;//�ϴεĴ���λ��Y���
    private float mPreviousX;//�ϴεĴ���λ��X���
    private int textureId;

    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        mRenderer = new SceneRenderer();
        setRenderer(mRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;//���㴥�ر�Yλ��
                float dx = x - mPreviousX;//���㴥�ر�Xλ��
                mRenderer.yAngle += dx * TOUCH_SCALE_FACTOR;//������y����ת�Ƕ�
                mRenderer.xAngle += dy * TOUCH_SCALE_FACTOR;//������x����ת�Ƕ�
                requestRender();//�ػ滭��
        }
        mPreviousY = y;//��¼���ر�λ��
        mPreviousX = x;//��¼���ر�λ��
        return true;
    }

    private class SceneRenderer implements Renderer {
        float yAngle;
        float xAngle;
        LoadedObjectVertexAndIndex lovo;

        public void onDrawFrame(GL10 gl) {
            Log.i("aaa", "onDrawFrame: ");
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            MatrixState.pushMatrix();
            MatrixState.translate(0, -2f, -75f);   //ch.obj
            MatrixState.rotate(yAngle, 0, 1, 0);
            MatrixState.rotate(xAngle, 1, 0, 0);

            if (lovo != null) {
                lovo.drawSelf();
            }
            MatrixState.popMatrix();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.i("aaa", "onSurfaceChanged: ");
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 200);
            MatrixState.setCamera(0, 0, 0, 0f, 0f, -1f, 0f, 1.0f, 0.0f);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.i("aaa", "onSurfaceCreated: ");
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            MatrixState.setInitStack();
            //初始化光源位置
            MatrixState.setLightLocation(40, 10, 20);
            lovo = LoadUtil2.loadFromFile("123.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
           // textureId = initTexture(R.drawable.ghxp);
        }
    }

    public int initTexture(int drawableId)//textureId
    {
        //生成纹理ID
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

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

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLUtils.getInternalFormat(bitmapTmp), bitmapTmp, GLUtils.getType(bitmapTmp), 0);
        bitmapTmp.recycle();          //纹理加载成功后释放图片
        return textureId;
    }
}
