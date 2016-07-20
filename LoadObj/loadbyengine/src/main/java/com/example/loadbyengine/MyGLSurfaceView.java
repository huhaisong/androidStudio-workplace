package com.example.loadbyengine;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.example.loadbyengine.cardboard2.sensor.HeadTracker;
import com.example.loadbyengine.cardboard2.sensor.HeadTransform;

public class MyGLSurfaceView extends GLSurfaceView {
    //private Context mContext;
    public static HeadTracker mHeadTracker;
    public static HeadTransform mHeadTransform;

    public void onPause() {
        super.onPause();
        mHeadTracker.stopTracking();
    }

    public void onResume() {
        super.onResume();
        mHeadTracker.startTracking();
    }

    public MyGLSurfaceView(Context context) {
        super(context);
        TInit(context);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TInit(context);
    }

    private void TInit(Context context) {
        //mContext=context;
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mHeadTracker = HeadTracker.createFromContext(context);
        mHeadTransform = new HeadTransform();
    }

}