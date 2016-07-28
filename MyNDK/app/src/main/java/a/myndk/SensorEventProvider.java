package a.myndk;

/**
 * Created by 333 on 2016/7/4.
 */
import android.hardware.SensorEventListener;
public interface SensorEventProvider {
    void start();

    void stop();

    void registerListener(SensorEventListener var1);

    void unregisterListener(SensorEventListener var1);
}
