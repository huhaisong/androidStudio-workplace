package com.example.opengl;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.hardware.*;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.graphics.PixelFormat;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;

public class MainActivity extends Activity {


    private TextView userHint;
    private SensorManager mSensorManager;
    private Boolean sensorRegedFlag = false;
    private OpenGLRenderer glRenderer;
    private GLSurfaceView glView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_main);
        FrameLayout mainLayout = (FrameLayout) findViewById(R.id.MainLayout);
        /**
         * OpenGL图层
         *
         * 必须先添加GLSurfaceView,再添加SurfaceView，否则GLSurfaceView会被SurfaceView遮盖
         * 也有说SurfaceView不能叠加，“Multiple surface views works, but you don't want to have them overlap because the Z-order of them is undefined”
         */
        glView = new GLSurfaceView(this);
        // glView.setEGLContextClientVersion(2);
        // glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        glRenderer = new OpenGLRenderer();
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.setRenderer(glRenderer);
        glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mainLayout.addView(glView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        /**
         * 相机图层
         */
        SurfaceView camView = new SurfaceView(this);
        SurfaceHolder camHolder = camView.getHolder();
        CameraPreview camPreview = new CameraPreview();
        camHolder.addCallback(camPreview);
        camHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mainLayout.addView(camView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.FILL_PARENT));

        // 文本提示图层
        userHint = new TextView(this);
        userHint.setText("waiting...");
        mainLayout.addView(userHint, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        // 传感器
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSensorManager != null) {
            List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
            if (sensors.size() > 0) {
                Sensor sensor = sensors.get(0);
                if (!mSensorManager.registerListener(glRenderer, sensor, SensorManager.SENSOR_DELAY_UI)) {
                    Toast.makeText(this, "MAGNETIC_FIELD Reg Error", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            sensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            if (sensors.size() > 0) {
                Sensor sensor = sensors.get(0);
                if (!mSensorManager.registerListener(glRenderer, sensor, SensorManager.SENSOR_DELAY_UI)) {
                    Toast.makeText(this, "ACCELEROMETER Reg Error", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            sensorRegedFlag = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorManager != null && sensorRegedFlag) {
            mSensorManager.unregisterListener(glRenderer);
        }
    }

    class OpenGLRenderer implements Renderer, SensorEventListener {

        // 三角形顶点
        private FloatBuffer triBuffer = MemUtil.makeFloatBuffer(new float[]{
                0, 1.0f, -6.0f,       //上顶点
                -1.0f, -1.0f, -6.0f,  //左下点
                1.0f, -1.0f, -6.0f}); //右下点

        @Override
        public void onDrawFrame(GL10 gl) {
            // TODO Auto-generated method stub
            // 清除屏幕和深度缓存
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            // 重置当前的模型观察矩阵
            gl.glLoadIdentity();
            // 根据Orientation调整绘图位置
            gl.glLoadMatrixf(remapR, 0); // 可以实现世界坐标系和OpenGL坐标系的对应，即机器围绕大地坐标系的X轴旋转，OpenGL绘制的图像也围绕OpenGL的X轴旋转
            GLU.gluLookAt(gl, 0, 0, 0,
                    (float) Math.sin(orientationValues[0]), -(float) Math.sin(orientationValues[1]), -(float) Math.cos(orientationValues[0]),
                    (float) Math.sin(orientationValues[2]), (float) Math.cos(orientationValues[2]), 0);
            // 允许设置顶点
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            // 设置三角形
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triBuffer);
            // 绘制三角形
            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
            // 取消顶点设置
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            // TODO Auto-generated method stub
            float ratio = (float) width / height;
            // 设置OpenGL场景的大小
            gl.glViewport(0, 0, width, height);
            // 设置投影矩阵
            gl.glMatrixMode(GL10.GL_PROJECTION);
            // 重置投影矩阵
            gl.glLoadIdentity();
            // 设置视口的大小
            gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
            // 选择模型观察矩阵
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            // 重置模型观察矩阵
            gl.glLoadIdentity();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
            // TODO Auto-generated method stub
            // 启用阴影平滑
            gl.glShadeModel(GL10.GL_SMOOTH);
            // 黑色背景
            gl.glClearColor(0, 0, 0, 0);
            // 设置深度缓存
            gl.glClearDepthf(1.0f);
            // 启用深度测试
            gl.glEnable(GL10.GL_DEPTH_TEST);
            // 所作深度测试的类型
            gl.glDepthFunc(GL10.GL_LEQUAL);
            // 告诉系统对透视进行修正
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        }

        private float accelerometerValues[];
        private float magneticFieldValues[];
        private final float orientationValues[] = new float[3];
        private float R[] = new float[16];
        private float I[] = new float[16];
        private float remapR[] = new float[16];

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
            glView.requestRender();
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = event.values.clone();
            }
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values.clone();
            }
            if (magneticFieldValues != null && accelerometerValues != null) {
                if (SensorManager.getRotationMatrix(R, I, accelerometerValues, magneticFieldValues)) {
                    // 坐标转换
                    SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Z, SensorManager.AXIS_MINUS_X, remapR);
                    // 获取Orientation
                    SensorManager.getOrientation(remapR, orientationValues);
                    // 刷新提示
                    if (userHint != null) {
                        userHint.post(new Runnable() {
                            public void run() {
                                userHint.setText("Azimuth " + orientationValues[0] / 3.1415926 * 180
                                        + "\nPitch " + orientationValues[1] / 3.1415926 * 180
                                        + "\nRoll " + orientationValues[2] / 3.1415926 * 180);
                            }
                        });
                    }
                }
            }
        }
    }


    class CameraPreview implements SurfaceHolder.Callback {

        private Camera mCamera;

        @Override
        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            Camera.Parameters para = mCamera.getParameters();
            para.setPreviewSize(480, 320);
            mCamera.setParameters(para);
            try {
                mCamera.setPreviewDisplay(arg0);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                mCamera.release();
            }
            mCamera.startPreview();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            mCamera = Camera.open();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            mCamera.stopPreview();
            mCamera.release();
        }
    }
}