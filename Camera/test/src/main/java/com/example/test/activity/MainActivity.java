package com.example.test.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.test.model.MyGLSurfaceView;
import com.example.test.model2.MyGLSurfaceView2;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置为竖屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        MyGLSurfaceView myGLSurfaceView = new MyGLSurfaceView(this);
        setContentView(myGLSurfaceView);
        myGLSurfaceView.requestFocus();//获取焦点
        myGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控
    }
}
