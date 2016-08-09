package zengshi.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;


public class SpotView extends View {
	
	private int mWidth=0;
	private int mHeight=0;
	
	public View mOnView=null;
	public int mShowX=0;
	public int mShowY=0;
 
	private Bitmap mBitmap=null;
	private void init()
	{
		mBitmap=BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.cross);
	}
	

	public SpotView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		 init();
	}
	
	public SpotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public SpotView(Context context) {
		super(context);
		init();
	}
	

	@SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
       super.onDraw(canvas);
       if(mBitmap!=null)
       {
    	   if(mWidth!=getWidth())
    		   mWidth=getWidth();
    	   if(mHeight!=getHeight())
    		   mHeight=getHeight();
    	   mShowX = (mWidth  -	mBitmap.getWidth())/2;
    	   mShowY = (mHeight -	mBitmap.getHeight())/2;
    	   canvas.drawBitmap(mBitmap, mShowX, mShowY, null);
       }
	}


}
