package com.example.learn_sun2;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class MySurfaceView extends GLSurfaceView {
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private SceneRenderer mRenderer;
    private Ball ball;
    private float lightOffset = -4;
    private float mPreviousY;
    private float mPreviousX;

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
                ball.yAngle += dx * TOUCH_SCALE_FACTOR;
                ball.xAngle += dy * TOUCH_SCALE_FACTOR;
        }
        mPreviousY = y;
        mPreviousX = x;
        return true;
    }

    private class SceneRenderer implements Renderer {

        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            MatrixState.setLightLocation(lightOffset, 0, 1.5f);
            MatrixState.pushMatrix();
            MatrixState.pushMatrix();
            MatrixState.translate(-1.2f, 0, 0);
            ball.drawSelf();
            MatrixState.popMatrix();
            MatrixState.pushMatrix();
            MatrixState.translate(1.2f, 0, 0);
            ball.drawSelf();
            MatrixState.popMatrix();
            MatrixState.popMatrix();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            Constant.ratio = (float) width / height;
            MatrixState.setProjectFrustum(-Constant.ratio, Constant.ratio, -1, 1, 20, 100);
            MatrixState.setCamera(0, 0f, 30, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            MatrixState.setInitStack();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0f, 0f, 0f, 1.0f);
            ball = new Ball(MySurfaceView.this);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }
    }

    public void setLightOffset(float lightOffset) {
        this.lightOffset = lightOffset;
    }
}
