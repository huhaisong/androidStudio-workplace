package org.yanzi.glsurfaceview;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import android.util.Log;

import org.yanzi.camera.CameraInterface;
import org.yanzi.util.MatrixState;

public class MyGLSurfaceView extends GLSurfaceView implements Renderer, SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "CameraGLSurfaceView";
    private Context mContext;
    private SurfaceTexture mSurface;
    private int mTextureID = -1;
    private DirectDrawer mDirectDrawer;
    private DirectDrawer2 mDirectDrawer2;


    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onSurfaceCreated...");
        mTextureID = createTextureID();
        mSurface = new SurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);
        mDirectDrawer = new DirectDrawer(mTextureID);
        mDirectDrawer2 = new DirectDrawer2();
        CameraInterface.getInstance().doOpenCamera(null);
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        MatrixState.setInitStack();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onSurfaceChanged...");
        GLES20.glViewport(0, 0, width, height);
        if (!CameraInterface.getInstance().isPreviewing()) {
            CameraInterface.getInstance().doStartPreview(mSurface, 1.33f);
        }
        float ratio = (float) width / height;
        //调用此方法计算产生透视投影矩阵
        MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 101);
        //调用此方法产生摄像机9参数位置矩阵
        MatrixState.setCamera(0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // TODO Auto-generated method stub
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mSurface.updateTexImage();

        MatrixState.pushMatrix();
        MatrixState.translate(0.0f, 0.0f, -10.0f);
        mDirectDrawer.draw();
        MatrixState.popMatrix();
        MatrixState.pushMatrix();
        MatrixState.translate(0.0f,0.0f,-3.5f);
        mDirectDrawer2.draw();
        MatrixState.popMatrix();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        CameraInterface.getInstance().doStopCamera();
    }

    private int createTextureID() {
        int[] texture = new int[1];

        //创建纹理
        GLES20.glGenTextures(1, texture, 0);
        //绑定纹理  第一个参数为纹理的类型，第二个参数为纹理对象的ID
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        //设置纹理过滤器
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onFrameAvailable...");
        this.requestRender();
    }
}
