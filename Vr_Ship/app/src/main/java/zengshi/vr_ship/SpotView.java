package zengshi.vr_ship;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


public class SpotView extends View {
	
	private int mWidth=0;
	private int mHeight=0;
    //�ȶ��ٺ���
	
	public View mOnView=null;
	public int mShowX=0;
	public int mShowY=0;
	private boolean loaded=true;
	private Bitmap mBitmap=null;
	private Context mContext=null;
	private void init(Context context)
	{
		mBitmap=BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.cross);
		mContext=context;
	}
	

	public SpotView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		 init(context);
	}
	
	public SpotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public SpotView(Context context) {
		super(context);
		init(context);
	}
	
	public void SetLoad(boolean is)
	{
		loaded=is;
		invalidate();
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
    	   
    	   if(loaded==false)
    	   {
    		   String testString = String.format(mContext.getString(R.string.load));  
    	   
    	   Paint mPaint = new Paint();   
    	   mPaint.setStrokeWidth(3);    
    	   mPaint.setTextSize(40);    
    	   mPaint.setColor(Color.RED);  
    	   mPaint.setTextAlign(Align.LEFT);  
    	   Rect bounds = new Rect();  
    	    mPaint.getTextBounds(testString, 0, testString.length(), bounds);  
    	    canvas.drawText(testString, getMeasuredWidth()/2 - bounds.width()/2, getMeasuredHeight()/2 + bounds.height()/2, mPaint);
    	   }
       }
	}


}
