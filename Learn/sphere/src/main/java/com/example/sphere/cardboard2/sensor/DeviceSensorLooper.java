package com.example.sphere.cardboard2.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 开始后开启一个线程不断注册监听事件
 * 暂停后停止该线程
 */
public class DeviceSensorLooper implements SensorEventProvider {
    private static final String LOG_TAG = DeviceSensorLooper.class.getSimpleName();
    private boolean isRunning;
    private SensorManager sensorManager;
    private Looper sensorLooper;
    private SensorEventListener sensorEventListener;
    private final ArrayList<SensorEventListener> registeredListeners = new ArrayList();

    public DeviceSensorLooper(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    private Sensor getUncalibratedGyro() {
        return Build.MANUFACTURER.equals("HTC")?null:this.sensorManager.getDefaultSensor(16);
    }

    public void start() {
        if(!this.isRunning) {
            this.sensorEventListener = new SensorEventListener() {
                public void onSensorChanged(SensorEvent event) {
                    synchronized(DeviceSensorLooper.this.registeredListeners) {
                        Iterator var3 = DeviceSensorLooper.this.registeredListeners.iterator();

                        while(var3.hasNext()) {
                            SensorEventListener listener = (SensorEventListener)var3.next();
                            listener.onSensorChanged(event);
                        }
                    }
                }

                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    synchronized(DeviceSensorLooper.this.registeredListeners) {
                        Iterator var4 = DeviceSensorLooper.this.registeredListeners.iterator();
                        while(var4.hasNext()) {
                            SensorEventListener listener = (SensorEventListener)var4.next();
                            listener.onAccuracyChanged(sensor, accuracy);
                        }

                    }
                }
            };
            //注册监听的线程sensorThread
            HandlerThread sensorThread = new HandlerThread("sensor") {
                protected void onLooperPrepared() {
                    Handler handler = new Handler(Looper.myLooper());
                    //注册加速传感器监听
                    Sensor accelerometer = DeviceSensorLooper.this.sensorManager.getDefaultSensor(1);
                    DeviceSensorLooper.this.sensorManager.registerListener(DeviceSensorLooper.this.sensorEventListener, accelerometer, 0, handler);
                    //注册陀螺仪传感器监听
                    Sensor gyroscope = DeviceSensorLooper.this.getUncalibratedGyro();
                    if(gyroscope == null) {
                        Log.i(DeviceSensorLooper.LOG_TAG, "Uncalibrated gyroscope unavailable, default to regular gyroscope.");
                        gyroscope = DeviceSensorLooper.this.sensorManager.getDefaultSensor(4);
                    }
                    DeviceSensorLooper.this.sensorManager.registerListener(DeviceSensorLooper.this.sensorEventListener, gyroscope, 0, handler);
                }
            };
            sensorThread.start();
            this.sensorLooper = sensorThread.getLooper();
            this.isRunning = true;
        }
    }

    public void stop() {
        if(this.isRunning) {
            this.sensorManager.unregisterListener(this.sensorEventListener);
            this.sensorEventListener = null;
            this.sensorLooper.quit();
            this.sensorLooper = null;
            this.isRunning = false;
        }
    }

    public void registerListener(SensorEventListener listener) {
       // ArrayList var2 = this.registeredListeners;
        synchronized(this.registeredListeners) {
            this.registeredListeners.add(listener);
        }
    }

    public void unregisterListener(SensorEventListener listener) {
       // ArrayList var2 = this.registeredListeners;
        synchronized(this.registeredListeners) {
            this.registeredListeners.remove(listener);
        }
    }
}

