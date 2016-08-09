package com.example.loadbyengine;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {
    private VRGLSurfaceView mGLView;
    //private static final String TAG = "MainActivity";

    protected void onCreate(Bundle savedInstanceState) {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLView = (VRGLSurfaceView) findViewById(R.id.view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}