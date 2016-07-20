package com.example.fog;

import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

class MySurfaceView extends GLSurfaceView {
    private SceneRenderer mRenderer;
    private float mPreviousX;
    float cx = 0;
    float cy = 150;
    float cz = 400;

    float pmScale = 200f;
    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        mRenderer = new SceneRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)

    {

        float TOUCH_SCALE_FACTOR = 180.0f / 200;

        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                cx += dx * TOUCH_SCALE_FACTOR;
                cx = Math.max(cx, -200);
                cx = Math.min(cx, 200);
                break;
        }
        mPreviousX = x;
        return true;
    }

    private class SceneRenderer implements Renderer {
        LoadedObjectVertexNormalFace cft;
        LoadedObjectVertexNormalAverage qt;
        LoadedObjectVertexNormalAverage yh;
        LoadedObjectVertexNormalAverage ch;
        TextureRect pm;
        final float disWithCenter = 12.0f;

        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            MatrixState.setCamera
                    (
                            cx,    //����λ�õ�X
                            cy, //����λ�õ�Y
                            cz, //����λ�õ�Z
                            0,    //�����򿴵ĵ�X
                            0,  //�����򿴵ĵ�Y
                            0,  //�����򿴵ĵ�Z
                            0,    //up����
                            1,
                            0
                    );
            MatrixState.pushMatrix();
            MatrixState.pushMatrix();
            pm.drawSelf();
            MatrixState.popMatrix();
            MatrixState.pushMatrix();
            MatrixState.scale(5.0f, 5.0f, 5.0f);
            MatrixState.pushMatrix();
            MatrixState.translate(-disWithCenter, 0f, 0);
            cft.drawSelf();
            MatrixState.popMatrix();
            MatrixState.pushMatrix();
            MatrixState.translate(disWithCenter, 0f, 0);
            qt.drawSelf();
            MatrixState.popMatrix();
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -disWithCenter);
            yh.drawSelf();
            MatrixState.popMatrix();
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, disWithCenter);
            ch.drawSelf();
            MatrixState.popMatrix();
            MatrixState.popMatrix();

            MatrixState.popMatrix();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            float a = 0.5f;
            MatrixState.setProjectFrustum(-ratio * a, ratio * a, -1 * a, 1 * a, 2, 1000);
            MatrixState.setLightLocation(100, 100, 100);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            MatrixState.setInitStack();
            ch = LoadUtil.loadFromFileVertexOnlyAverage("ch.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            cft = LoadUtil.loadFromFileVertexOnlyFace("cft.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            qt = LoadUtil.loadFromFileVertexOnlyAverage("qt.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            yh = LoadUtil.loadFromFileVertexOnlyAverage("yh.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            pm = new TextureRect(MySurfaceView.this, pmScale, pmScale);
        }
    }
}
