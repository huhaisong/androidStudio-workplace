package com.example.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bn.tl.R;

import static com.example.game.Constant.*;

//������������
public class GuanYuView extends SurfaceView implements SurfaceHolder.Callback
{
	BasketBall_Shot_Activity activity;
	Canvas canvas;//����
	SurfaceHolder holder;
	Paint paint;
	Bitmap beijing;//����ͼƬ
	Bitmap shijianBeijin;//��ɫ��
	Bitmap wenzitupian;//����ͼƬ
	Bitmap gundontiao;//������
	
	Bitmap fanHui;//���ذ�ť
	Bitmap queDing;//ȷ����ť
	boolean isnoDianJi=true;//�Ƿ�����
	
	float timeStartX;
	float timeStartY;//������������ʼλ��
	
	float timeStartXCaiJian;//�ü������ʼλ��
	float timeStartYCaiJian;//
	
	float caijianWidth;//�ü���Ŀ��
	float caijianHeight;//�ü���ĸ߶�
	
	float timegradeStartX;//���������ʱ����ʼλ��
	float timeGradeStartY;
	
	
	
	float timegradeinitX;//���������������ʼλ��
	float timegradeinitY;
	
	
	
	float mDounX;
	float mDounY;//�ϴδ�����λ��
	
	float mtimegradeStartX;//����ʱ������λ��
	float mtimeGradeStartY;
	
	boolean isnoFanhui;//�Ƿ���ʱ�ڷ��ذ�ť��
	boolean isnoQueDing;//�Ƿ���ʱ��ȷ����ť��
	public GuanYuView(BasketBall_Shot_Activity activity) 
	{
		super(activity);
		this.activity=activity;
		this.getHolder().addCallback(this);
		paint=new Paint();
		paint.setAntiAlias(true);
		initBitmap();
		timeStartX=sXtart+15*ratio_width;//������������ʼλ��
		timeStartY=sYtart+150*ratio_height;
		
		timeStartXCaiJian=timeStartX;//�ü������ʼλ��
		timeStartYCaiJian=sYtart+timeStartY+4*ratio_height;
		
		caijianWidth=450*ratio_width;
		caijianHeight=shijianBeijin.getHeight();//440*ratio_height;//�ü���Ŀ��
		
		timegradeinitX=timeStartX;
		timegradeinitY=timeStartY+4*ratio_height;//��������ĳ�ʼλ��
		
		timegradeStartX=timegradeinitX;
		timeGradeStartY=timegradeinitY;//���ƹ���ʱ����ʼλ��
		
	}
	public void onDraw(Canvas canvas)	{
		super.onDraw(canvas);
		canvas.drawBitmap(beijing, sXtart, sYtart, null);//����		
		canvas.drawBitmap(shijianBeijin,timeStartX, timeStartY, null);//��ɫ����
		canvas.save();
		canvas.clipRect(new RectF(timegradeinitX,timegradeinitY,
				timeStartX+caijianWidth,timegradeinitY+caijianHeight));
		canvas.drawBitmap(wenzitupian,timegradeStartX, timeGradeStartY, null);//��������
		float gundontiaoheight=(-timeGradeStartY+timegradeinitY)*(caijianHeight-50*ratio_height)/
		(wenzitupian.getHeight()-caijianHeight)+timegradeinitY;
		if(isnoDianJi){
			canvas.drawBitmap(gundontiao, timeStartX+caijianWidth-12*ratio_width, gundontiaoheight, null);//������
		}
		canvas.restore();		
		if(isnoQueDing){
			canvas.drawBitmap(scaleToFit(queDing,1.2f,1.2f), sXtart+35*ratio_width,sYtart+ 692*ratio_height, null);//���ذ�ť
		}else{
			canvas.drawBitmap(queDing, sXtart+50*ratio_width, sYtart+700*ratio_height, null);//���ذ�ť
		}
		if(isnoFanhui){
			canvas.drawBitmap(scaleToFit(fanHui,1.2f,1.2f), sXtart+245*ratio_width,sYtart+ 692*ratio_height, null);//ȷ����ť
		}else{
			canvas.drawBitmap(fanHui, sXtart+270*ratio_width, sYtart+700*ratio_height, null);//ȷ����ť
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
			if(x>timeStartX&&x<timeStartX+caijianWidth&&y>timeStartYCaiJian&&y<timeStartYCaiJian+caijianHeight){
				mDounX=x;
				mDounY=y;
				mtimeGradeStartY=timeGradeStartY;
				isnoDianJi=true;
			}else{
				isnoDianJi=false;
			}
			if(x>sXtart+50*ratio_width&&x<sXtart+50*ratio_width+160*ratio_width&&y>sYtart+700*ratio_height&&y<sYtart+780*ratio_height){//���ذ�ť
				isnoQueDing=true;
			}else{
				isnoQueDing=false;
			}
			if(x>sXtart+270*ratio_width&&x<sXtart+270*ratio_width+160*ratio_width&&y>sYtart+700*ratio_height&&y<sYtart+780*ratio_height){//ȷ����ť
				isnoFanhui=true;
			}else{
				isnoFanhui=false;
			}
			onDrawcanvas();
			break;
		case MotionEvent.ACTION_MOVE:
			float dy=y-mDounY;
			if(isnoDianJi==true){
				if(mtimeGradeStartY+dy<timegradeinitY+caijianHeight-wenzitupian.getHeight()){
					timeGradeStartY=timegradeinitY+caijianHeight-wenzitupian.getHeight();
				}
				else if(mtimeGradeStartY+dy>timegradeinitY){
					timeGradeStartY=timegradeinitY;  
				}else{
					timeGradeStartY=mtimeGradeStartY+dy;
				}
				 onDrawcanvas();
			}
			
			break;
		case MotionEvent.ACTION_UP:
			if(isnoQueDing&&x>sXtart+50*ratio_width&&x<sXtart+50*ratio_width+160*ratio_width&&y>sYtart+700*ratio_height&&y<sYtart+780*ratio_height){//���ذ�ť
				isnoQueDing=false;//ȷ��
				activity.xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);//���ص��˵�����
			}else{
				isnoQueDing=false;
			}
			if(isnoFanhui&&x>sXtart+270*ratio_width&&x<sXtart+270*ratio_width+160*ratio_width&&y>sYtart+700*ratio_height&&y<sYtart+780*ratio_height){//ȷ����ť
				isnoFanhui=false;//����
				activity.xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);//���ص��˵�����
			}else{
				isnoFanhui=false;
			}
			
			if(isnoDianJi){
				isnoDianJi=false;
				 
			}
			onDrawcanvas();
			break;			
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
	@Override
	public void surfaceDestroyed(SurfaceHolder holder){
	}
	public void initBitmap()
	{				
		shijianBeijin=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.baisewaikuang),ratio_width,ratio_height);//ʱ�����
		gundontiao=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.gundontiao),ratio_width,ratio_height);//������		
		beijing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.background),ratio_width,ratio_height);//����ͼƬ
		fanHui=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.fanhuianniu),ratio_width,ratio_height);//���ذ�ť
		queDing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.quedinganniu),ratio_width,ratio_height);//ȷ����ť
		
		wenzitupian=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.guanyuwenzitu),ratio_width,ratio_height);//ȷ����ť
		
		
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
}