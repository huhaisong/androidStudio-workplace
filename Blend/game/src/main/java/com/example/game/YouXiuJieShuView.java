package com.example.game;

import static com.example.game.Constant.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bn.tl.R;

public class YouXiuJieShuView extends SurfaceView implements SurfaceHolder.Callback
{
	BasketBall_Shot_Activity activity;
	SurfaceHolder holder;
	Canvas canvas;
	Paint paint;
	CaiDanView w;
	
	Bitmap background;
	Bitmap exit;//�˳�
	Bitmap retry;//����һ��
	Bitmap fanhuicaidan;//���ز˵�
	boolean isnoretry;//�Ƿ���������һ��
	boolean isnofanhuicaidan;//�Ƿ����˷��ز˵���ť
	boolean isnoexit;//�Ƿ������˳���ť
	
	public YouXiuJieShuView(BasketBall_Shot_Activity activity,CaiDanView w) {
		super(activity);
		this.activity=activity;
		this.getHolder().addCallback(this);
		this.w=w;
		paint=new Paint();
		paint.setAntiAlias(true);
		initBitmap();
	}
	
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);	
		
		canvas.drawBitmap(background, sXtart, sYtart, paint);//����
		
		if(isnoretry){
			canvas.drawBitmap(scaleToFit(retry,1.2f,1.2f), sXtart+ratio_width*90, sYtart+244*ratio_height, paint);//����һ��
		}else{
			canvas.drawBitmap(retry, sXtart+ratio_width*115, sYtart+250*ratio_height, paint);//����һ��
		}
		if(isnofanhuicaidan){
			canvas.drawBitmap(scaleToFit(fanhuicaidan,1.2f,1.2f), sXtart+ratio_width*90, sYtart+364*ratio_height, paint);//���ز˵�
		}else{
			canvas.drawBitmap(fanhuicaidan, sXtart+ratio_width*115, sYtart+370*ratio_height, paint);//���ز˵�
		}
		
		if(isnoexit){
			canvas.drawBitmap(scaleToFit(exit,1.2f,1.2f), sXtart+ratio_width*90, sYtart+484*ratio_height, paint);//�˳�
		}else{
			canvas.drawBitmap(exit, sXtart+ratio_width*115, sYtart+490*ratio_height, paint);//�˳�
		}
		
	}
	
	public boolean onTouchEvent(MotionEvent e)
	{
		float x=e.getX();
		float y=e.getY();
		switch(e.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			if(x>sXtart+ratio_width*115&&x<sXtart+ratio_width*365&&y>sYtart+250*ratio_height&&y<sYtart+310*ratio_height){
				isnoretry=true;//����һ��
			}else{
				isnoretry=false;
			}
			
			if(x>sXtart+ratio_width*115&&x<sXtart+ratio_width*365&&y>sYtart+370*ratio_height&&y<sYtart+430*ratio_height){
				isnofanhuicaidan=true;//���ز˵�
			}else{
				isnofanhuicaidan=false;
			}
			
			if(x>sXtart+ratio_width*115&&x<sXtart+ratio_width*365&&y>sYtart+490*ratio_height&&y<sYtart+550*ratio_height){
				isnoexit=true;//����
			}else{
				isnoexit=false;
			}
			doDraw();
			break;
		case MotionEvent.ACTION_UP:
			if(isnoretry&&x>sXtart+ratio_width*115&&x<sXtart+ratio_width*365&&y>sYtart+250*ratio_height&&y<sYtart+310*ratio_height){
				//����һ��
				activity.xiaoxichuli.sendEmptyMessage(JIAZAI_JIEMIAN);  //���ؽ���
			}
			
			if(isnofanhuicaidan&&x>sXtart+ratio_width*115&&x<sXtart+ratio_width*365&&y>sYtart+370*ratio_height&&y<sYtart+430*ratio_height){
				//���ز˵�
				activity.xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);
			}
			
			if(isnoexit&&x>sXtart+ratio_width*115&&x<sXtart+ratio_width*365&&y>sYtart+490*ratio_height&&y<sYtart+550*ratio_height){
			//�˳�
			System.exit(0);
			}
			isnoretry=false;
			isnofanhuicaidan=false;
			isnoexit=false;
			doDraw();
			break;
		}
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.holder=holder;
		doDraw();
	}
	public void doDraw()
	{
		canvas=holder.lockCanvas();
		try
		{
			synchronized(holder)
			{
				onDraw(canvas);//����
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(canvas!=null)
			{
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	public void initBitmap()
	{
		background=scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.background),ratio_width,ratio_height);
		retry=scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.retry),ratio_width,ratio_height);//����һ��
		fanhuicaidan=scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.fanhuicaidan),ratio_width,ratio_height);//���ز˵�
		exit=scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.shut),ratio_width,ratio_height);//�˳���ť
		
	}
	
}