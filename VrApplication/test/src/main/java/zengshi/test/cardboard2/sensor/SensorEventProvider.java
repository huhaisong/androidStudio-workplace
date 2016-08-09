package zengshi.test.cardboard2.sensor;

import android.hardware.SensorEventListener;

public interface SensorEventProvider {
    void start();

    void stop();

    void registerListener(SensorEventListener var1);

    void unregisterListener(SensorEventListener var1);
}
