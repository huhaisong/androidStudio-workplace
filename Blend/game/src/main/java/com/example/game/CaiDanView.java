package com.example.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bn.tl.R;

import static com.example.game.Constant.*;

public class CaiDanView extends SurfaceView implements SurfaceHolder.Callback
{
	BasketBall_Shot_Activity activity;
	Canvas canvas;//����
	SurfaceHolder holder;
	Paint paint;	
	Bitmap kaishi;
	Bitmap beijingtupian;
	Bitmap tuichu;
	Bitmap guanyu;
	Bitmap bangzhu;
	Bitmap lishijilu;//��ʷ��¼
	Bitmap shezhi;//���ð�ťͼƬ
	
	boolean isKaishi;//�Ƿ����˿�ʼ��ť
	boolean isshezhi;//�Ƿ������ð�ť	
	boolean isguanyu;//�Ƿ��¹��ڰ�ť
	boolean isbangzhu;//�Ƿ��°���ť
	boolean islishijilu;//�Ƿ�����ʷ��¼	
	boolean istuichu;//�Ƿ����˳���ť��
	
	float left1=LEFT*ratio_width+sXtart;
	
	public CaiDanView(BasketBall_Shot_Activity activity) 
	{
		
		super(activity);
		this.activity=activity;
		this.getHolder().addCallback(this);//�����������ڻص��ӿڵ�ʵ����	
		paint = new Paint();
		paint.setAntiAlias(true);//�򿪿����
		initBitmap();//��ʼ��λͼ
	}
	public void onDraw(Canvas canvas)
	{
		float xstar=25*ratio_width;
		float ystar=6*ratio_height;
		if(canvas==null) return;
		super.onDraw(canvas);
		canvas.drawBitmap(beijingtupian, sXtart, sYtart, paint);//����
		if(isKaishi){
			canvas.drawBitmap(scaleToFit(kaishi,1.2f,1.2f), left1-xstar, 160*ratio_height+sYtart-ystar, paint);//��ʼ 
		}else{
			canvas.drawBitmap(kaishi, left1, 160*ratio_height+sYtart, paint);//��ʼ 
		}
		if(isshezhi){
			canvas.drawBitmap(scaleToFit(shezhi,1.2f,1.2f), left1-xstar,
					240*ratio_height+sYtart-ystar, paint);//����
		}
		else
		{
			canvas.drawBitmap(shezhi, left1,
					240*ratio_height+sYtart, paint);//����
		}
		if(isguanyu){
			canvas.drawBitmap(scaleToFit(guanyu,1.2f,1.2f), left1-xstar, 320*ratio_height+sYtart-ystar, paint);//����
		}
		else
		{
			canvas.drawBitmap(guanyu, left1, 320*ratio_height+sYtart, paint);//����
		}
		if(isbangzhu)
		{
			canvas.drawBitmap(scaleToFit(bangzhu,1.2f,1.2f), left1-xstar, 
					400*ratio_height+sYtart-ystar, paint);//����
		}else{
			canvas.drawBitmap(bangzhu, left1, 400*ratio_height+sYtart, paint);//����
		}
		if(islishijilu){
			canvas.drawBitmap(scaleToFit(lishijilu,1.2f,1.2f), left1-xstar, 480*ratio_height+sYtart-ystar, paint);//��ʷ��¼
		}else{
			canvas.drawBitmap(lishijilu, left1, 480*ratio_height+sYtart, paint);//��ʷ��¼
		}
		if(istuichu){
			canvas.drawBitmap(scaleToFit(tuichu,1.2f,1.2f), left1-xstar, 560*ratio_height+sYtart-ystar, paint);//�˳�
		}
		else{
			canvas.drawBitmap(tuichu, left1, 560*ratio_height+sYtart, paint);//�˳�
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
			if(x>left1&&x<left1+250*ratio_width&&y>160*ratio_height+sYtart&&y<160*ratio_height+sYtart+60*ratio_height){
				isKaishi=true;//��ʼ��ť
			}else{
				isKaishi=false;
			}
			
			
			if(x>left1&&x<left1+250*ratio_width&&y>240*ratio_height+sYtart&&y<240*ratio_height+sYtart+60*ratio_height){
				isshezhi=true;//���ð�ť
			}else{
				isshezhi=false;
			}
			
			if(x>left1&&x<left1+250*ratio_width&&y>320*ratio_height+sYtart&&y<320*ratio_height+sYtart+60*ratio_height){
				isguanyu=true;//���ڰ�ť
			}else{
				isguanyu=false;
			}
			
			if(x>left1&&x<left1+250*ratio_width&&y>400*ratio_height+sYtart&&y<400*ratio_height+sYtart+60*ratio_height){
				isbangzhu=true;//����ť
			}else{
				isbangzhu=false;
			}
			
			if(x>left1&&x<left1+250*ratio_width&&y>480*ratio_height+sYtart&&y<480*ratio_height+sYtart+60*ratio_height){
				islishijilu=true;//��ʷ��ť
			}else{
				islishijilu=false;
			}
			
			if(x>left1&&x<left1+250*ratio_width&&y>560*ratio_height+sYtart&&y<560*ratio_height+sYtart+60*ratio_height){
				istuichu=true;//�˳���ť
			}else{
				istuichu=false;
			}
			
			 doDraw();
			break;
		case MotionEvent.ACTION_UP:
			
			if(isKaishi&&x>left1&&x<left1+250*ratio_width&&y>160*ratio_height+sYtart&&y<160*ratio_height+sYtart+60*ratio_height){
				isKaishi=false;
				isnoHelpView=false;
				
				activity.xiaoxichuli.sendEmptyMessage(JIAZAI_JIEMIAN);  //���ؽ���
			}else{
				isKaishi=false;
			}
			if(isshezhi&&x>left1&&x<left1+250*ratio_width&&y>240*ratio_height+sYtart&&y<240*ratio_height+sYtart+60*ratio_height){
				isshezhi=false;//���ð�ť
				//��������������
				activity.xiaoxichuli.sendEmptyMessage(SHENGYING_KG_JIEMIAN);
			}else{
				isshezhi=false;
			}
			
			if(isguanyu&&x>left1&&x<left1+250*ratio_width&&y>320*ratio_height+sYtart&&y<320*ratio_height+sYtart+60*ratio_height){
				isguanyu=false;//���ڰ�ť
				activity.xiaoxichuli.sendEmptyMessage(GUANYU_JIEMIAN);
			}else{
				isguanyu=false;
			}
			
			if(isbangzhu&&x>left1&&x<left1+250*ratio_width&&y>400*ratio_height+sYtart&&y<400*ratio_height+sYtart+60*ratio_height){
				isbangzhu=false;//����ť
				activity.xiaoxichuli.sendEmptyMessage(BANGZHU_JIEMIAN);
			}else{
				isbangzhu=false;
			}
			
			if(islishijilu&&x>left1&&x<left1+250*ratio_width&&y>480*ratio_height+sYtart&&y<480*ratio_height+sYtart+60*ratio_height){
				islishijilu=false;//��ʷ��ť
				activity.xiaoxichuli.sendEmptyMessage(JILU_JIEMIAN);
			}else{
				islishijilu=false;
			}
			
			if(istuichu&&x>left1&&x<left1+250*ratio_width&&y>560*ratio_height+sYtart&&y<560*ratio_height+sYtart+60*ratio_height){
				istuichu=false;//�˳���ť
				System.exit(0);
			}else{
				istuichu=false;
			}
			
			doDraw();
		}		
		return true;
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
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {	
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		this.holder=holder;
		doDraw();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
	}
	public void initBitmap()
	{
		beijingtupian=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.background),ratio_width,ratio_height);	
		kaishi=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.begin),ratio_width,ratio_height);
		tuichu=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.shut),ratio_width,ratio_height);
		guanyu=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.about1),ratio_width,ratio_height);
		bangzhu=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.help1),ratio_width,ratio_height);
		lishijilu=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.jilu),ratio_width,ratio_height);//��ʷ��¼��ť
		shezhi=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.shezhi),ratio_width,ratio_height);//���ð�ť
	}	
}