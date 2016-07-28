package a.myndk;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
//import com.google.vr.sdk.base.sensors.Clock;
//import com.google.vr.sdk.base.sensors.DeviceSensorLooper;
//import com.google.vr.sdk.base.sensors.SensorEventProvider;
//import com.google.vr.sdk.base.sensors.SystemClock;
//import com.google.vr.sdk.base.sensors.internal.GyroscopeBiasEstimator;
//import com.google.vr.sdk.base.sensors.internal.Matrix3x3d;
//import com.google.vr.sdk.base.sensors.internal.OrientationEKF;
//import com.google.vr.sdk.base.sensors.internal.Vector3d;
import java.util.concurrent.TimeUnit;
/**
 * Created by 333 on 2016/7/6.
 */
public class HeadTracker implements SensorEventListener{
   // private static final float DEFAULT_NECK_HORIZONTAL_OFFSET = 0.08F;
   // private static final float DEFAULT_NECK_VERTICAL_OFFSET = 0.075F;
  //  private static final float DEFAULT_NECK_MODEL_FACTOR = 1.0F;
  //  private static final float PREDICTION_TIME_IN_SECONDS = 0.058F;
    private final Display display;
   // private final float[] ekfToHeadTracker = new float[16];
   // private final float[] sensorToDisplay = new float[16];
  //  private float displayRotation = 0.0F;
  //  private final float[] neckModelTranslation = new float[16];
  //  private final float[] tmpHeadView = new float[16];
  //  private final float[] tmpHeadView2 = new float[16];
  //  private float neckModelFactor = 1.0F;
  //  private final Object neckModelFactorMutex = new Object();
  //  private volatile boolean tracking;
  //  private final OrientationEKF tracker;
  //  private final Object gyroBiasEstimatorMutex = new Object();
  //  private GyroscopeBiasEstimator gyroBiasEstimator;
    private SensorEventProvider sensorEventProvider;
    private Clock clock;
    private long latestGyroEventClockTimeNs;
  //  private volatile boolean firstGyroValue = true;
 //   private float[] initialSystemGyroBias = new float[3];
//    private final Vector3d gyroBias = new Vector3d();
 //   private final Vector3d latestGyro = new Vector3d();
  //  private final Vector3d latestAcc = new Vector3d();

    public static HeadTracker createFromContext(Context context) {
        SensorManager sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return new HeadTracker(new DeviceSensorLooper(sensorManager), new SystemClock(), display);
    }

    public HeadTracker(SensorEventProvider sensorEventProvider, Clock clock, Display display) {
        this.clock = clock;
        this.sensorEventProvider = sensorEventProvider;
        //this.tracker = new OrientationEKF();
        this.display = display;
       // this.gyroBiasEstimator = new GyroscopeBiasEstimator();
       // Matrix.setIdentityM(this.neckModelTranslation, 0);
        Mndk.init_headtracker_a();
    }

    public void onSensorChanged(SensorEvent event) {
       // Object var2;
        int sensor_type = event.sensor.getType();
        int length_values  = event.values.length;
        long nano_time   = this.clock.nanoTime();
        long timestamp = event.timestamp;
        float[] event_values = new float[length_values];
        int i =0;
       // Log.e("myerror","naotime"+nano_time+"event-time"+timestamp);
        for(;i<length_values;i++) {
            event_values[i] = event.values[i];
            Log.e("myerror", "event_values[" + i + "}:" + event_values[i]);
        }
        if(event.sensor.getType() == 4 || event.sensor.getType() == 16)
            this.latestGyroEventClockTimeNs = this.clock.nanoTime();
            Mndk.onSensorChanged_a(sensor_type,event_values, length_values, timestamp);

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void startTracking() {
        this.sensorEventProvider.registerListener(this);
        this.sensorEventProvider.start();
        Mndk.startTracking_a();
    }

    public void resetTracker() {
        Mndk.resetTracker_a();
    }

    public void stopTracking() {
        this.sensorEventProvider.unregisterListener(this);
        this.sensorEventProvider.stop();
                Mndk.stopTracking_a();
    }
/*
    public void setNeckModelEnabled(boolean enabled) {
        if(enabled) {
            this.setNeckModelFactor(1.0F);
        } else {
            this.setNeckModelFactor(0.0F);
        }

    }

    public float getNeckModelFactor() {
        Object var1 = this.neckModelFactorMutex;
        synchronized(this.neckModelFactorMutex) {
            return this.neckModelFactor;
        }
    }
    public void setNeckModelFactor(float factor) {
        Object var2 = this.neckModelFactorMutex;
        synchronized(this.neckModelFactorMutex) {
            if(factor >= 0.0F && factor <= 1.0F) {
                this.neckModelFactor = factor;
            } else {
                throw new IllegalArgumentException("factor should be within [0.0, 1.0]");
            }
        }
    }
*/
    public void getLastHeadView()
    {

        int display_rotation =  this.display.getRotation() ;
        double secondsSinceLastGyroEvent = (double)TimeUnit.NANOSECONDS.toSeconds(this.clock.nanoTime() - this.latestGyroEventClockTimeNs);
        Mndk.getLastHeadView_a(display_rotation,secondsSinceLastGyroEvent );
    }
/*
    Matrix3x3d getCurrentPoseForTest() {
        return new Matrix3x3d(this.tracker.getRotationMatrix());
    }
    void setGyroBiasEstimator(GyroscopeBiasEstimator estimator) {
        Object var2 = this.gyroBiasEstimatorMutex;
        synchronized(this.gyroBiasEstimatorMutex) {
            this.gyroBiasEstimator = estimator;
        }
    }
    */
}
