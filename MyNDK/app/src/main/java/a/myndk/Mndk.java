package a.myndk;

/**
 * Created by 333 on 2016/7/21.
 */

public class Mndk {
    static {
        System.loadLibrary("libndk");
    }
    public static native void created();
    public static native void changed(int width, int height);
    public static native void step();
    public static native void  Apkpath(String apkpath);
    public static native void  releaseAll();


    public static native void init_headtracker_a() ;
    public static native void getLastHeadView_a(int display_rotation, double secondsSinceLastGyroEvent) ;
    public static native void stopTracking_a();
    public static native void resetTracker_a();
    public static native void startTracking_a();
    public static native void onSensorChanged_a(int sensor_type, float[] event_values, int length_values, long timestamp);
}
