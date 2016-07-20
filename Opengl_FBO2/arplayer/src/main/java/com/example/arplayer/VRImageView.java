package com.example.arplayer;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
//import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class VRImageView extends View {

    private ArrayList<String> mPlayList = null;
    private int mCurrentIndex = -1;
    private int SCREENWIDTH = 854;
    private int SCREENHEIGHT = 480;
    private int OTHERBMPWIDTH = 30;
    private boolean Is3d = false;
    private int ShowOther = 0;

    public VRImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public VRImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VRImageView(Context context) {
        super(context);
    }

    public void SetList(ArrayList<String> PlayList) {
        mPlayList = PlayList;
    }

    public void SetIndex(int index) {
        mCurrentIndex = index;
    }

    public void Set3d(boolean is3d) {
        Is3d = is3d;
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 3333) {
                invalidate();
            }
        }
    };

    public void Update() {
        Message message = new Message();
        message.what = 3333;
        mHandler.sendMessage(message);
    }

    public void ShowNext() {
        if (ShowOther != 1) {
            ShowOther = 1;
            Update();
        }
    }

    public void ShowPrevious() {
        if (ShowOther != -1) {
            ShowOther = -1;
            Update();
        }
    }

    public void Hide() {
        if (ShowOther != 0) {
            ShowOther = 0;
            Update();
        }
    }


    public void Previous() {
        if (mCurrentIndex > 0) {
            mCurrentIndex = mCurrentIndex - 1;
            Update();
        }
    }

    public void Next() {
        if ((mCurrentIndex + 1) < mPlayList.size()) {
            mCurrentIndex = mCurrentIndex + 1;
            Update();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);
        int action = event.getAction();
        int X = (int) event.getX();
        //int Y = (int) event.getY();
        Log.e("ar110", "x=" + X + ",ShowOther=" + ShowOther);
        if (action == MotionEvent.ACTION_DOWN) {
            if (X > SCREENWIDTH / 4 && X < (SCREENWIDTH / 2 - OTHERBMPWIDTH)) {
                ShowOther = 1;
            } else if (X > OTHERBMPWIDTH && X < SCREENWIDTH / 4) {
                ShowOther = -1;
            } else if (ShowOther != 0) {
                Log.e("ar110", "ShowOther=" + ShowOther + ",x=" + X);
                if (X < OTHERBMPWIDTH) {
                    if (mCurrentIndex > 0) {
                        mCurrentIndex = mCurrentIndex - 1;

                    }
                } else if (X > (SCREENWIDTH / 2 - OTHERBMPWIDTH)) {
                    if ((mCurrentIndex + 1) < mPlayList.size()) {
                        mCurrentIndex = mCurrentIndex + 1;
                    }

                }
            } else {
                ShowOther = 0;
            }

            Message message = new Message();
            message.what = 3333;
            mHandler.sendMessage(message);
        }

        return value;
    }

    public int mTop = 120;

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPlayList != null) {
            Log.e("ar110", "onDraw");

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mPlayList.get(mCurrentIndex), options);
            //int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            int inSampleSize = imageWidth / 1280;

            Bitmap bitmap = null;
            BitmapFactory.Options noptions = new BitmapFactory.Options();
            noptions.inSampleSize = inSampleSize;
            bitmap = BitmapFactory.decodeFile(mPlayList.get(mCurrentIndex), noptions);
            if (Is3d) {
                int newbh = SCREENWIDTH * bitmap.getHeight() / bitmap.getWidth();
                int newbw = SCREENWIDTH;
                if (newbh > SCREENHEIGHT) {
                    newbh = SCREENHEIGHT;
                    newbw = newbh * bitmap.getWidth() / bitmap.getHeight();
                }
                int left = (SCREENWIDTH - newbw) / 2;
                int top = (SCREENHEIGHT - newbh) / 2;

                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(left, top, left + newbw, top + newbh), null);
                bitmap.recycle();


/*if(ShowOther==1)
			   {
				   if((mCurrentIndex+1)<mPlayList.size())
				   {
					    Bitmap OtherBitmap = BitmapFactory.decodeFile(mPlayList.get(mCurrentIndex+1));
					    int B3dwidth=OtherBitmap.getWidth()/2;
					    int obh=SCREENWIDTH/2*OtherBitmap.getHeight()/B3dwidth;
				    	int obw=SCREENWIDTH/2;
				    	if(obh>SCREENHEIGHT)
				    	{
				    		obh=SCREENHEIGHT;
				    		obw=newbh*B3dwidth/OtherBitmap.getHeight();
				    	}
				    	int otop=(SCREENHEIGHT-obh)/2;
				    	int oleft=SCREENWIDTH/2-OTHERBMPWIDTH;
				    	int obdw=B3dwidth*OTHERBMPWIDTH/obw;
				    //	Log.e("ar110","otop="+otop);
				    	canvas.drawBitmap(OtherBitmap, new Rect(0,0,obdw,OtherBitmap.getHeight()), new Rect(oleft,otop,oleft+OTHERBMPWIDTH,otop+obh), null);

				    	oleft=oleft+SCREENWIDTH/2;
						canvas.drawBitmap(OtherBitmap, new Rect(B3dwidth,0,B3dwidth+obdw,OtherBitmap.getHeight()), new Rect(oleft,otop,oleft+OTHERBMPWIDTH,otop+obh), null);
						OtherBitmap.recycle();

				   }else
				   {
					   ShowOther=0;
				   }
			   }else if(ShowOther==-1)
			   {
				   if((mCurrentIndex)>0)
				   {
					    Bitmap OtherBitmap = BitmapFactory.decodeFile(mPlayList.get(mCurrentIndex+-1));
					    int B3dwidth=OtherBitmap.getWidth()/2;
					    int obh=SCREENWIDTH/2*OtherBitmap.getHeight()/B3dwidth;
				    	int obw=SCREENWIDTH/2;
				    	if(obh>SCREENHEIGHT)
				    	{
				    		obh=SCREENHEIGHT;
				    		obw=newbh*B3dwidth/OtherBitmap.getHeight();
				    	}
				    	int otop=(SCREENHEIGHT-obh)/2;
				    	int oleft=0;
				    	int obdw=B3dwidth*OTHERBMPWIDTH/obw;
				    	Log.e("ar110","otop="+otop);
				    	canvas.drawBitmap(OtherBitmap, new Rect(B3dwidth-obdw,0,B3dwidth,OtherBitmap.getHeight()), new Rect(oleft,otop,oleft+OTHERBMPWIDTH,otop+obh), null);

				    	oleft=oleft+SCREENWIDTH/2;
						canvas.drawBitmap(OtherBitmap, new Rect(OtherBitmap.getWidth()-obdw,0,OtherBitmap.getWidth(),OtherBitmap.getHeight()), new Rect(oleft,otop,oleft+OTHERBMPWIDTH,otop+obh), null);
						OtherBitmap.recycle();

				   }else
				   {
					   ShowOther=0;
				   }
			   }*/
            } else {

                int newbh = SCREENWIDTH / 2 * bitmap.getHeight() / bitmap.getWidth();
                int newbw = SCREENWIDTH / 2;


                if (newbh > SCREENHEIGHT) {
                    newbh = SCREENHEIGHT;
                    newbw = newbh * bitmap.getWidth() / bitmap.getHeight();
                }
                int left = (SCREENWIDTH / 2 - newbw) / 2;
                int top = (SCREENHEIGHT - newbh) / 2;

                //Log.e("ar110","newbh="+newbh+",newbw="+newbw+",left="+left+",top="+top+","+bitmap.getWidth());
                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(left, top, left + newbw, top + newbh), null);

                left = left + SCREENWIDTH / 2;
                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(left, top, left + newbw, top + newbh), null);
                bitmap.recycle();

            }

			   /*if(ShowOther==1)
			   {
				   if((mCurrentIndex+1)<mPlayList.size())
				   {
					    Bitmap OtherBitmap = BitmapFactory.decodeFile(mPlayList.get(mCurrentIndex+1));
					    int obh=SCREENWIDTH/2*OtherBitmap.getHeight()/OtherBitmap.getWidth();
				    	int obw=SCREENWIDTH/2;
				    	if(obh>SCREENHEIGHT)
				    	{
				    		obh=SCREENHEIGHT;
				    		obw=newbh*OtherBitmap.getWidth()/OtherBitmap.getHeight();
				    	}
				    	int otop=(SCREENHEIGHT-obh)/2;
				    	int oleft=SCREENWIDTH/2-OTHERBMPWIDTH;
				    	int obdw=OtherBitmap.getWidth()*OTHERBMPWIDTH/obw;
				    	Log.e("ar110","otop="+otop);
				    	canvas.drawBitmap(OtherBitmap, new Rect(0,0,obdw,OtherBitmap.getHeight()), new Rect(oleft,otop,oleft+OTHERBMPWIDTH,otop+obh), null);

				    	oleft=oleft+SCREENWIDTH/2;
						canvas.drawBitmap(OtherBitmap, new Rect(0,0,obdw,OtherBitmap.getHeight()), new Rect(oleft,otop,oleft+OTHERBMPWIDTH,otop+obh), null);
						OtherBitmap.recycle();

				   }else
				   {
					   ShowOther=0;
				   }
			   }else if(ShowOther==-1)
			   {
				   if((mCurrentIndex)>0)
				   {
					    Bitmap OtherBitmap = BitmapFactory.decodeFile(mPlayList.get(mCurrentIndex-1));
					    int obh=SCREENWIDTH/2*OtherBitmap.getHeight()/OtherBitmap.getWidth();
				    	int obw=SCREENWIDTH/2;
				    	if(obh>SCREENHEIGHT)
				    	{
				    		obh=SCREENHEIGHT;
				    		obw=newbh*OtherBitmap.getWidth()/OtherBitmap.getHeight();
				    	}
				    	int otop=(SCREENHEIGHT-obh)/2;
				    	int oleft=0;
				    	int obdw=OtherBitmap.getWidth()*OTHERBMPWIDTH/obw;
				    	Log.e("ar110","otop="+otop);
				    	canvas.drawBitmap(OtherBitmap, new Rect(OtherBitmap.getWidth()-obdw,0,OtherBitmap.getWidth(),OtherBitmap.getHeight()), new Rect(oleft,otop,oleft+OTHERBMPWIDTH,otop+obh), null);

				    	oleft=oleft+SCREENWIDTH/2;
						canvas.drawBitmap(OtherBitmap, new Rect(OtherBitmap.getWidth()-obdw,0,OtherBitmap.getWidth(),OtherBitmap.getHeight()), new Rect(oleft,otop,oleft+OTHERBMPWIDTH,otop+obh), null);
						OtherBitmap.recycle();

				   }else
				   {
					   ShowOther=0;
				   }
			   }*/

            //Log.e("ar110","onDraw newbh="+newbh+",newbw="+newbw);
        }
    }
}
