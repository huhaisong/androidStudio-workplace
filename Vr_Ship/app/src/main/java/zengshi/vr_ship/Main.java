package zengshi.vr_ship;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 */
public class Main extends Activity {

	private VRView mGLView;
	SpotView LSpotView;
	SpotView RSpotView;

	public static int  REQUEST_CODE_SOME_FEATURES_PERMISSIONS=1;
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case 1: {
				for (int i = 0; i < permissions.length; i++) {
					if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
						Log.e("TTT","Permissions --> " + "Permission Granted: " + permissions[i]);
					} else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
						Log.e("TTT","Permissions --> " + "Permission Denied: " + permissions[i]);
					}
				}
			}
			break;
			default: {
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
			}
		}
	}


	//private ShowRenderer mRenderer;
	@TargetApi(Build.VERSION_CODES.M)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		int hasCallPhonePermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
		List<String> permissions = new ArrayList<String>();
		if (hasCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
			permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
		}
		if (!permissions.isEmpty()) {
			requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
		}
			
		setContentView(R.layout.activity_main);
		LSpotView=(SpotView)findViewById(R.id.spot);
		RSpotView=(SpotView)findViewById(R.id.spotR);
		LSpotView.SetLoad(false);
		RSpotView.SetLoad(false);
		mGLView = (VRView) findViewById(R.id.vrview);
		mGLView.SetHandler(mHandler);
		mGLView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		
    		switch(msg.what)  
    		{  
    		case 8888:  
    			LSpotView.SetLoad(true);
    			RSpotView.SetLoad(true);
    			break;
    		case 7777:  
    			LSpotView.SetLoad(false);
    			RSpotView.SetLoad(false);
    			break;
    		}
    	}
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		LSpotView.SetLoad(false);
		RSpotView.SetLoad(false);
		mGLView.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
