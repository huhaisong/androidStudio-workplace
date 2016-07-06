package com.example.a111.a3d_model.activity;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.a111.a3d_model.glsurfaceview.MySurfaceView;
import com.example.a111.a3d_model.R;


public class MyActivity extends Activity {
	MySurfaceView mySurfaceView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main);
		mySurfaceView = new MySurfaceView(this);
		mySurfaceView.requestFocus();
		mySurfaceView.setFocusableInTouchMode(true);
        LinearLayout ll=(LinearLayout)findViewById(R.id.main_liner); 
        ll.addView(mySurfaceView); 

        RadioButton rab=(RadioButton)findViewById(R.id.RadioButton01);
        rab.setOnCheckedChangeListener(
            new OnCheckedChangeListener()
            {
     			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
     			{
     				if(isChecked)
     				{
     					mySurfaceView.drawWhatFlag=true;
     				}
     			}        	   
            }         		
        );       
        rab=(RadioButton)findViewById(R.id.RadioButton02);
        
        rab.setOnCheckedChangeListener(
            new OnCheckedChangeListener()
            {
     			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
     			{
     				if(isChecked)
     				{
     					mySurfaceView.drawWhatFlag=false;
     				}
     			}        	   
            }         		
        );         
    }
    @Override
    protected void onResume() {
        super.onResume();
        mySurfaceView.onResume();
        mySurfaceView.lightFlag=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mySurfaceView.onPause();
        mySurfaceView.lightFlag=false;
    } 
    
    
    public boolean onKeyDown(int keyCode,KeyEvent e)
    {
    	switch(keyCode)
        	{
    	case 4:
    		System.exit(0);
    		break;
        	}
    	return true;
    };
}