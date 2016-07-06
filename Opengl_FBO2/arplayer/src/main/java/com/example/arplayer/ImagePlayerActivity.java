package com.example.arplayer;

import java.util.ArrayList;


//import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.util.*;

public class ImagePlayerActivity extends Activity {
  
	private ArrayList<String> mPlayList = null;
	private int mCurrentIndex=-1;
	private VRImageView mImageview=null;
	private boolean is3d=false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//mPlaypath=getIntent().getStringExtra("PlayPath");
		Bundle bundle = getIntent().getExtras().getBundle("playlist");
		mPlayList = bundle.getStringArrayList("list");
		mCurrentIndex = bundle.getInt("index");
		/*if(bundle.getInt("Vr")==2){
			is3d=false;
		} else {
			is3d=true;
		}*/
			
		setContentView(R.layout.image_player);
		mImageview = (VRImageView) findViewById(R.id.imagevrview);
		mImageview.SetList(mPlayList);
		mImageview.SetIndex(mCurrentIndex);
		mImageview.Set3d(is3d);
	
		Log.e("ImagePlayerActivity", "onCreate");
	}
    
    @Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);  
	
			if(keyCode==KeyEvent.KEYCODE_HEADSETHOOK)
			{
				this.finish();
			}
			return true;
	
    }

}
