package com.example.texture3.surfaceview;

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

import com.example.texture3.R;
import com.example.texture3.model.Celestial;
import com.example.texture3.model.Earth;
import com.example.texture3.model.Moon;
import com.example.texture3.util.MatrixState;

import static com.example.texture3.util.Constant.*;

public class MySurfaceView extends GLSurfaceView {
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private SceneRenderer mRenderer;

    private float mPreviousX;
    private float mPreviousY;

    int textureIdEarth;
    int textureIdEarthNight;
    int textureIdMoon;

    float yAngle = 0;
    float xAngle = 0;

    float eAngle = 0;
    float cAngle = 0;

    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        mRenderer = new SceneRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                yAngle += dx * TOUCH_SCALE_FACTOR;
                float sunx = (float) (Math.cos(Math.toRadians(yAngle)) * 100);
                float sunz = -(float) (Math.sin(Math.toRadians(yAngle)) * 100);
                MatrixState.setLightLocationSun(sunx, 5, sunz);

                float dy = y - mPreviousY;
                xAngle += dy * TOUCH_SCALE_FACTOR;
                if (xAngle > 90) {
                    xAngle = 90;
                } else if (xAngle < -90) {
                    xAngle = -90;
                }
                float cy = (float) (7.2 * Math.sin(Math.toRadians(xAngle)));
                float cz = (float) (7.2 * Math.cos(Math.toRadians(xAngle)));
                float upy = (float) Math.cos(Math.toRadians(xAngle));
                float upz = -(float) Math.sin(Math.toRadians(xAngle));
                MatrixState.setCamera(0, cy, cz, 0, 0, 0, 0, upy, upz);
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

    private class SceneRenderer implements Renderer {
        Earth earth;
        Moon moon;
        Celestial cSmall;
        Celestial cBig;

        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            MatrixState.pushMatrix();
            MatrixState.rotate(eAngle, 0, 1, 0);
            earth.drawSelf(textureIdEarth, textureIdEarthNight);
            MatrixState.transtate(2f, 0, 0);
            MatrixState.rotate(eAngle, 0, 1, 0);
            moon.drawSelf(textureIdMoon);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.rotate(cAngle, 0, 1, 0);
            cSmall.drawSelf();
            cBig.drawSelf();
            MatrixState.popMatrix();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
            MatrixState.setCamera(0, 0, 7.2f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            textureIdEarth = initTexture(R.drawable.earth);
            textureIdEarthNight = initTexture(R.drawable.earthn);
            textureIdMoon = initTexture(R.drawable.moon);
            MatrixState.setLightLocationSun(100, 5, 0);

            new Thread() {
                public void run() {
                    while (threadFlag) {
                        eAngle = (eAngle + 2) % 360;
                        cAngle = (cAngle + 0.2f) % 360;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            earth = new Earth(MySurfaceView.this, 2.0f);
            moon = new Moon(MySurfaceView.this, 1.0f);
            cSmall = new Celestial(1, 0, 1000, MySurfaceView.this);
            cBig = new Celestial(2, 0, 500, MySurfaceView.this);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            MatrixState.setInitStack();
        }
    }

    public int initTexture(int drawableId) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

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
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();
        return textureId;
    }
}