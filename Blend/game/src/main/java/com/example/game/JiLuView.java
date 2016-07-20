package com.example.game;

import java.util.Vector;




import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bn.tl.R;

import static com.example.game.Constant.*;

//������������
public class JiLuView extends SurfaceView implements SurfaceHolder.Callback
{
	BasketBall_Shot_Activity activity;
	Canvas canvas;//����
	SurfaceHolder holder;
	Paint paint;
	Bitmap beijing;//����ͼƬ
	Bitmap shijianBeijin;//��ʾʱ��ĺͷ���ı���
	Bitmap shijianBeijin2;//��ʾʱ��ͷ��ֱ���ͼ2
	boolean isnoGradePaixu=true;//�Ƿ�Ϊ��װ��������
	
	Bitmap[] iscore=new Bitmap[10];//�÷�ͼ
    Bitmap JianHaotupian;//����ͼ
    Bitmap hengXian;//����
	Bitmap maohao;//ð��
	Bitmap gundontiao;//������
	
	Bitmap fanHui;//���ذ�ť
	Bitmap queDing;//ȷ����ť
	boolean isnoDianJi=true;//�Ƿ�����
	int color[][]=new int[][]{
			{100,250,205,0},
			{100,250,60,0}
	};
	Vector<Vector<String>> vector;//��Ž�������
	
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
	
	int geziHeight=(int)(40*ratio_height);//
	int geziWidth=(int)(450*ratio_width);//���ӵĳߴ�
	
	
	int scoreWidth=(int)(15*ratio_width);//���ֿ��
	int scoreHeght=(int)(20*ratio_height);//���ֵĸ߶�
	
	int fanggeGeshu=30;//������
	
	float mDounX;
	float mDounY;//�ϴδ�����λ��
	
	float mtimegradeStartX;//����ʱ������λ��
	float mtimeGradeStartY;
	
	boolean isnoFanhui;//�Ƿ���ʱ�ڷ��ذ�ť��
	boolean isnoQueDing;//�Ƿ���ʱ��ȷ����ť��
	public JiLuView(BasketBall_Shot_Activity activity) 
	{
		super(activity);
		this.activity=activity;
		this.getHolder().addCallback(this);
		paint=new Paint();
		paint.setAntiAlias(true);				
		initBitmap();
		String sql_select="select grade,time from paihangbang  order by grade desc limit 0,30;";
    	vector=SQLiteUtil.query(sql_select);//����ݿ���ȡ����Ӧ�����
    	fanggeGeshu=vector.size();
    	if(fanggeGeshu<12){
    		fanggeGeshu=12;
    	}
		timeStartX=sXtart+15*ratio_width;//������������ʼλ��
		timeStartY=sYtart+150*ratio_height;
		
		timeStartXCaiJian=timeStartX+20*ratio_height;//�ü������ʼλ��
		timeStartYCaiJian=sYtart+timeStartY+100*ratio_height;
		
		caijianWidth=430*ratio_width;
		caijianHeight=400*ratio_height;//�ü���Ŀ��
		
		timegradeinitX=timeStartXCaiJian;
		timegradeinitY=timeStartYCaiJian;//timeStartY+73*ratio_height;//��������ĳ�ʼλ��
		
		timegradeStartX=timegradeinitX;
		timeGradeStartY=timegradeinitY;//���ƹ���ʱ����ʼλ��
		
	}
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawBitmap(beijing, sXtart, sYtart, null);//����		
		if(isnoGradePaixu){
			canvas.drawBitmap(shijianBeijin, timeStartX, timeStartY, null);//ʱ��ͷ����������	
		}else{
			canvas.drawBitmap(shijianBeijin2, timeStartX, timeStartY, null);//ʱ��ͷ����������	
		}
		canvas.save();
		canvas.clipRect(new RectF(timegradeinitX,timegradeinitY,
				timeStartX+caijianWidth,timegradeinitY+caijianHeight));
		
		drawRectBeijing(canvas,timegradeStartX,timeGradeStartY);//������ɫ��	
		for(int i=0;i<vector.size();i++)//ѭ���������а�ķ���Ͷ�Ӧʱ��
    	{
			int j=i;
			if(!isnoGradePaixu){
				j=vector.size()-i-1;
			}
			drawRiQi(canvas,vector.get(j).get(1).toString(),//����ʱ��
					(int)(timegradeStartX+15*ratio_width),(int)(timeGradeStartY+i*geziHeight+5*ratio_height));
			drawScoreStr(canvas,vector.get(i).get(0).toString(),
					(int)timegradeStartX+(int)(300*ratio_width),(int)(timeGradeStartY+i*geziHeight+5*ratio_height));
    	}
		float gundontiaoheight=(-timeGradeStartY+timegradeinitY)*(caijianHeight-50*ratio_height)/
		(fanggeGeshu*geziHeight-caijianHeight)+timegradeinitY;
		if(isnoDianJi){
			canvas.drawBitmap(gundontiao, timeStartX+caijianWidth-8*ratio_width, gundontiaoheight, null);//������
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
	public void drawRectBeijing(Canvas canvas,float startX,float startY){
		
		for(int i=0;i<fanggeGeshu;i++){
			paint.setARGB(color[i%2][0], color[i%2][1], color[i%2][2],color[i%2][3]);//���û�����ɫ	
			Rect r=new Rect((int)(startX),(int)(startY+i*geziHeight),
					(int) (startX+geziWidth),(int) (startY+(1+i)*geziHeight));
			canvas.drawRect(r, paint);
		}
	}
	
	public void drawScoreStr(Canvas canvas,String s,int width,int height)//���������ַ���
	{
    	//���Ƶ÷�
    	String scoreStr=s; 
    	for(int i=0;i<scoreStr.length();i++){//ѭ�����Ƶ÷�
    		int tempScore=scoreStr.charAt(i)-'0';
    		canvas.drawBitmap(iscore[tempScore], width+i*scoreWidth,height, null);
    		}
	}
	public void drawRiQi(Canvas canvas,String s,int width,int height)//������
	{
		String ss[]=s.split("-");//�и�õ�������
		for(int i=1;i<ss.length;i++){
			if(ss[ss.length-i].length()<2){
				ss[ss.length-i]="0"+ss[ss.length-i];
			}
			drawScoreStr(canvas,ss[ss.length-i],width+scoreWidth*((ss.length-i-1)*3+0),height);//����������
			if(i<3){
				canvas.drawBitmap(maohao,width+scoreWidth*((ss.length-i-1)*3-1),height, null);//��ð��
			}
			else if(i==4){
				canvas.drawBitmap(hengXian,width+scoreWidth*((ss.length-i-1)*3-1),height, null);//������
			}
			
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
			if(x>timeStartX&&x<timeStartX+caijianWidth/2&&y>timeStartY&&y<timeStartY+74*ratio_height){//���������ʱ������
				Log.w("dsfjdskfj","2222222222");
				String sql_select="select grade,time from paihangbang   desc limit 0,30;";
		    	vector=SQLiteUtil.query(sql_select);//����ݿ���ȡ����Ӧ�����
				isnoGradePaixu=false;
			}else if(x>timeStartX+caijianWidth/2&&x<timeStartX+caijianWidth&&y>timeStartY&&y<timeStartY+74*ratio_height){//������ճɼ�����
				isnoGradePaixu=true;
				String sql_select="select grade,time from paihangbang  order by grade desc limit 0,30;";
		    	vector=SQLiteUtil.query(sql_select);//����ݿ���ȡ����Ӧ�����
				Log.w("dsfjdskfj","11111111");
			}
			
			if(x>timeStartX&&x<timeStartX+caijianWidth&&y>timeStartYCaiJian+74*ratio_height&&y<timeStartYCaiJian+caijianHeight+74*ratio_height){
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
				if(mtimeGradeStartY+dy<timegradeinitY+caijianHeight-fanggeGeshu*geziHeight){
					timeGradeStartY=timegradeinitY+caijianHeight-fanggeGeshu*geziHeight;
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
		iscore[0] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d0),ratio_width,ratio_height);//����ͼ
		iscore[1] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d1),ratio_width,ratio_height);
		iscore[2] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d2),ratio_width,ratio_height);
		iscore[3] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d3),ratio_width,ratio_height);
		iscore[4] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d4),ratio_width,ratio_height);
		iscore[5] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d5),ratio_width,ratio_height);
		iscore[6] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d6),ratio_width,ratio_height);
		iscore[7] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d7),ratio_width,ratio_height);
		iscore[8] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d8),ratio_width,ratio_height);
		iscore[9] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d9),ratio_width,ratio_height);
		hengXian=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.hengxian),ratio_width,ratio_height);//����
		maohao=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.maohao),ratio_width,ratio_height);//ð��
		shijianBeijin=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.timegradebeijing),ratio_width,ratio_height);//ʱ�����
		shijianBeijin2=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.shijianbeijing2),ratio_width,ratio_height);//ʱ�����
		gundontiao=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.gundontiao),ratio_width,ratio_height);//������		
		beijing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.background),ratio_width,ratio_height);//����ͼƬ
		fanHui=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.fanhuianniu),ratio_width,ratio_height);//���ذ�ť
		queDing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.quedinganniu),ratio_width,ratio_height);//ȷ����ť
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