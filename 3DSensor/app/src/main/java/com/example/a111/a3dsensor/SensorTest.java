package com.example.a111.a3dsensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.TextView;

/**
 * Created by 111 on 2016/6/17.
 */
public class SensorTest extends Activity implements SensorEventListener {

    private TextView gyroTextView;
    private SensorManager mSensorManager;
    private float[] angle = new float[3];

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gyroTextView = (TextView) findViewById(R.id.tv_Gyro);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;

    //当传感器的值发生改变时回调该方法
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (timestamp != 0) {
            //  event.timesamp表示当前的时间，单位是纳秒（1百万分之一毫秒）
            final float dT = (event.timestamp - timestamp) * NS2S;
            if (event.values[0] > 0.1||event.values[0] <- 0.1) {

                angle[0] += event.values[0] * dT;
            }
            if (event.values[1] > 0.1||event.values[1] <- 0.1) {
                angle[1] += event.values[1] * dT;

            }
            if (event.values[2] > 0.1||event.values[2] <- 0.1) {

                angle[2] += event.values[2] * dT;
            }
        }
        timestamp = event.timestamp;

        StringBuilder sb = new StringBuilder();
        sb.append("绕x轴旋转的角度： ").append(angle[0] + "\n")
                .append("绕y轴旋转的角度： ").append(angle[1] + "\n")
                .append("绕z轴旋转的角度： ").append(angle[2]);
        gyroTextView.setText(sb.toString());
    }

    //当传感器精度改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
