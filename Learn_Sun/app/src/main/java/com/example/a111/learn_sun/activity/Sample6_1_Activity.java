package com.example.a111.learn_sun.activity;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.a111.learn_sun.glsurfaceview.MySurfaceView;

public class Sample6_1_Activity extends Activity {
	private MySurfaceView mGLSurfaceView;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mGLSurfaceView = new MySurfaceView(this);
		setContentView(mGLSurfaceView);	
	}

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause(); 
    } 
}