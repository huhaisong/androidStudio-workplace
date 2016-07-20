package com.example.a111.loadobj;

import android.app.Activity;
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
 *
 */
public class Jpct_collsion4Activity extends Activity {
	private GLSurfaceView glView;
	private MyRenderer mr = new MyRenderer();
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
}
//载入文件
class LoadFile {
	public static Bitmap bitmap1;
	public static Bitmap bitmap2;
	public static Bitmap bitmap3;
	// 载入纹理图片
	public static void loadb(Resources res) {
		bitmap1 = BitmapFactory.decodeResource(res, R.drawable.back);
		bitmap2 = BitmapFactory.decodeResource(res, R.drawable.face);
		bitmap3 = BitmapFactory.decodeResource(res, R.drawable.bg);
	}
}

 