package zengshi.vr_ship;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class Main extends Activity {

	private VRView mGLView;
	SpotView LSpotView;
	SpotView RSpotView;
	
	//private ShowRenderer mRenderer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
