package com.example.a111.loadobj;


import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

/**
 * Activity类
 *
 * @author itde1
 */
public class Jpct_Collsion3Activity extends Activity {
    private GLSurfaceView glView;
    private MyRenderer2 mr = new MyRenderer2();
    //这里设置为public static，是因为在MyRenderer里面用到
    public static boolean up = false; // 方向上下左右
    public static boolean down = false;
    public static boolean left = false;
    public static boolean right = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 传入Resources方法
        LoadFile.loadb(getResources());
        new LoadFile2(getResources());
        glView = new GLSurfaceView(this);
        glView.setRenderer(mr);
        setContentView(glView);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        glView.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        glView.onResume();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { // 按键处理，当上下左右中的一个按下时，则将相应的变量置true
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                up = true;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                down = true;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                left = true;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                right = true;
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) { //松开按键
        up = false;
        down = false;
        left = false;
        right = false;
        return super.onKeyUp(keyCode, event);
    }
}

//载入文件
class LoadFile2 {
    public static Resources resource;
    public static Bitmap bitmap1;
    public static Bitmap bitmap2;
    public static Bitmap bitmap3;

    public LoadFile2(Resources res) {
        resource = res;
    }

    // 载入模型
    public static InputStream loadf(String fileName) {
        AssetManager am = LoadFile2.resource.getAssets();
        try {
            return am.open(fileName);
        } catch (IOException e) {
            return null;
        }
    }

    // 载入纹理图片
    public static void loadb2(Resources res) {
        bitmap1 = BitmapFactory.decodeResource(res, R.drawable.table);
        bitmap2 = BitmapFactory.decodeResource(res, R.drawable.bool1);
        bitmap3 = BitmapFactory.decodeResource(res, R.drawable.bool2);
    }
}

