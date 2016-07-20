package com.example.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bn.tl.R;

import static com.example.game.Constant.*;

//������������
public class ShengyinKGJiemian extends SurfaceView implements SurfaceHolder.Callback
{
	BasketBall_Shot_Activity activity;
	Canvas canvas;//����
	SurfaceHolder holder;
	Paint paint;
	Bitmap beijing;//����ͼƬ
	Bitmap isnoChangjing;//�Ƿ񲥷ų�������ͼƬ
	Bitmap isnoBeijing;//�Ƿ񲥷ű�������ͼƬ
	Bitmap baiseFangfe;//��ɫ����
	Bitmap honGou;//��ɫ��
	
	Bitmap fanHui;//���ذ�ť
	Bitmap queDing;//ȷ����ť
	boolean isnoFanhui;//�Ƿ���ʱ�ڷ��ذ�ť��
	boolean isnoQueDing;//�Ƿ���ʱ��ȷ����ť��
	public ShengyinKGJiemian(BasketBall_Shot_Activity activity) 
	{
		super(activity);
		this.activity=activity;
		this.getHolder().addCallback(this);
		paint=new Paint();
		paint.setAntiAlias(true);
		initBitmap();
	}
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawBitmap(beijing, sXtart, sYtart, paint);//����
		canvas.drawBitmap(isnoBeijing, sXtart+80*ratio_width, sYtart+300*ratio_height, paint);//�Ƿ񲥷ű�����������
		canvas.drawBitmap(isnoChangjing, sXtart+80*ratio_width, sYtart+420*ratio_height, paint);//�Ƿ񲥷ų�����������
		
		canvas.drawBitmap(baiseFangfe, sXtart+350*ratio_width, sYtart+310*ratio_height, paint);//��ɫ����
		canvas.drawBitmap(baiseFangfe, sXtart+350*ratio_width, sYtart+430*ratio_height, paint);//��ɫ����
		if(isBJmiusic){
			canvas.drawBitmap(honGou, sXtart+350*ratio_width, sYtart+310*ratio_height, paint);//��ɫ��
		}
		if(isCJmiusic){
			canvas.drawBitmap(honGou, sXtart+350*ratio_width, sYtart+430*ratio_height, paint);//��ɫ��
		}
		
		
		
		
		if(isnoFanhui){
			canvas.drawBitmap(scaleToFit(fanHui,1.2f,1.2f), sXtart+225*ratio_width, sYtart+572*ratio_height, null);//ȷ����ť
		}else{
			canvas.drawBitmap(fanHui, sXtart+250*ratio_width, sYtart+580*ratio_height, null);//ȷ����ť
		}
		if(isnoQueDing){
			canvas.drawBitmap(scaleToFit(queDing,1.2f,1.2f), sXtart+55*ratio_width, sYtart+572*ratio_height, null);//���ذ�ť
		}else{
			canvas.drawBitmap(queDing, sXtart+70*ratio_width, sYtart+580*ratio_height, null);//���ذ�ť
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		float x=e.getX();
		float y=e.getY();
		switch(e.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			if(x>sXtart+50*ratio_width&&x<sXtart+70*ratio_width+180*ratio_width&&y>sYtart+580*ratio_height&&y<sYtart+640*ratio_height){//ȷ����ť
				isnoQueDing=true;
			}else{
				isnoQueDing=false;
			}
			if(x>sXtart+250*ratio_width&&x<sXtart+250*ratio_width+160*ratio_width&&y>sYtart+580*ratio_height&&y<sYtart+640*ratio_height){//ȷ����ť
				isnoFanhui=true;
			}else{
				isnoFanhui=false;
			}
			onDrawcanvas();
			break;
		case MotionEvent.ACTION_UP:
			
			if(isnoQueDing&&x>sXtart+70*ratio_width&&x<sXtart+70*ratio_width+160*ratio_width&&y>sYtart+580*ratio_height&&y<sYtart+640*ratio_height){//���ذ�ť
				isnoQueDing=false;//ȷ��
				activity.xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);//���ص��˵�����
			}else{
				isnoQueDing=false;
			}
			if(isnoFanhui&&x>sXtart+250*ratio_width&&x<sXtart+250*ratio_width+160*ratio_width&&y>sYtart+580*ratio_height&&y<sYtart+640*ratio_height){//ȷ����ť
				isnoFanhui=false;//����
				activity.xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);//���ص��˵�����
			}else{
				isnoFanhui=false;
			}
			
			if(x>=sXtart+340*ratio_width&&x<=sXtart+400*ratio_width&&y>=sYtart+300*ratio_height&&y<=sYtart+360*ratio_height)
			{
				if(isBJmiusic){
					
					activity.beijingyinyue.stop();
					activity.beijingyinyue=null;
				}
				else{
					if(activity.beijingyinyue==null){
						activity.beijingyinyue=MediaPlayer.create(activity,R.raw.beijingyingyu);
						activity.beijingyinyue.setLooping(true);
						activity.beijingyinyue.setVolume(0.2f, 0.2f);
					}
				}
				isBJmiusic=!isBJmiusic;
			}
			else if(x>=sXtart+340*ratio_width&&x<=sXtart+400*ratio_width&&y>=sYtart+420*ratio_height&&y<=sYtart+480*ratio_height)
			{
				
				isCJmiusic=!isCJmiusic;
			}
			onDrawcanvas();
		}
		return true;		
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3){
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder){
		this.holder=holder;
		onDrawcanvas();
	}
	public void onDrawcanvas()
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
	public void surfaceDestroyed(SurfaceHolder holder){
	}
	public void initBitmap()
	{
		beijing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.background),ratio_width,ratio_height);//����ͼƬ
		isnoBeijing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.beijingyinyu),ratio_width,ratio_height);//���ű�������
		isnoChangjing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.changjinyinyu),ratio_width,ratio_height);	//���ų�������
		baiseFangfe=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.baisefangge),ratio_width,ratio_height);	//��ɫ����
		honGou=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.honsegou),ratio_width,ratio_height);	//��ɫ��
		fanHui=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.fanhuianniu),ratio_width,ratio_height);//���ذ�ť
		queDing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.quedinganniu),ratio_width,ratio_height);//ȷ����ť
	
		
		
	}
}