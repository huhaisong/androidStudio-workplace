package zengshi.vr_ship;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;

public class VRGLSurfaceView extends GLSurfaceView{
	//private Context mContext;
	static HeadTracker mHeadTracker;
	static HeadTransform mHeadTransform;

	public void onPause() {
		super.onPause();
		mHeadTracker.stopTracking();

	 }
	 
	 public void onResume() {
		super.onResume();
		mHeadTracker.startTracking();

	 }
	public VRGLSurfaceView(Context context) {
		super(context);
		TInit(context);
	}
	
	 public VRGLSurfaceView(Context context, AttributeSet attrs) {
			super(context, attrs);
			TInit(context);
	}
	 
	 private void TInit(Context context)
		{
		 	//mContext=context;	
			setEGLContextClientVersion(2);
			setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		 	
			mHeadTracker = HeadTracker.createFromContext(context);
			mHeadTransform = new HeadTransform();
			
		}

}
