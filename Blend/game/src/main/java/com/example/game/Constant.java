package com.example.game;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Constant
{	
	
	
	
	public static int startY=-60;//������ʵY���
	public static int wenziSize=50;//����ÿһ�еļ��
	public static int wenziwidth=512;
	public static int wenziHeight=512;//���ֱ���ͼƬ�Ĵ�С
	 public static Bitmap generateWLT(String s[],int width,int height)
	   {
		   Paint paint=new Paint();
		   paint.setARGB(255, 255,255, 255);
		   paint.setTextSize(40);
		   paint.setTypeface(null);  
		   paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		   Bitmap bmTemp=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		   Canvas canvasTemp = new Canvas(bmTemp); 	
		   for(int i=0;i<s.length;i++){
			   canvasTemp.drawText(s[i], 35, startY+i*(wenziSize)+512, paint);
		   }
		   	   
		   return bmTemp;
	   }
	   

	   
	   static int cIndex=8;
	   static String[] content=
	   {
		   "����ָ����һ���Ѿ���ֹ",
		   "�������ڵ�λ�ã�Ȼ����",
		   "���ƶ���ָ��һ�ξ����" ,
		   "���ɿ���ָ������ʵ��Ͷ" ,
		   "�����������λ��ƫ��" ,
		   "��Ļ���󷽣�����ָ����" ,
		   "ʱҪ��΢�����ƶ�һ�㣬" ,
		   "������X�����ٶȣ�ͬ" ,
		   "�����������λ��ƫ����" ,
		   "Ļ�ұߣ��򻬶�ʱ����Ļ" ,
		   "��߻���һ����������" ,
		   "X��������ٶȡ�",
		   "",
	   };
	
	
	
	static float gFactor=1.6f;//�������ٶ����ű���
	static float vFactor=(float) Math.sqrt(gFactor);//y�����ٶ����ű���
	
	static int shipingJs;//��Ƶ�̼߳�ʱ��
	
	
	public static boolean isnoHelpView;//�Ƿ�Ϊ������棬trueΪ��
	public static boolean isnoPlay=true;//�Ƿ��ڲ��Ž��������ͣ���棬trueΪ����״̬
	
	public static float shouX=0;
	public static float shouY=-0.9f;//�ֵ�xy���
	
	static Bitmap welcome;//��ӭ����ͼ
	static Bitmap welcome2;
	static Bitmap dot;//���ؽ��
	//������3D�����м��ؽ����� IO��������ͼƬ
	public static void loadWelcomeBitmap(Resources r,int drawableId[])
	{
		  InputStream is=null;
          try  
          {
        	  
        	  is= r.openRawResource(drawableId[0]);	
        	  welcome = BitmapFactory.decodeStream(is);
        	  is= r.openRawResource(drawableId[1]);	
        	  dot=BitmapFactory.decodeStream(is);
        	  is= r.openRawResource(drawableId[2]);
        	  welcome2=BitmapFactory.decodeStream(is);
          } 
	      finally 
	      {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
	      }
	}
	public static boolean flag=true;//����ģ���߳��Ƿ�ֹͣ
	public static boolean isCJmiusic=true;//��������
	public static boolean isBJmiusic=false;//��������
	
	
	
	
	public static float sXtart=0;//2D�������ʼ���
	public static float sYtart=0;
	
	//�ֻ���Ļ�Ŀ�Ⱥ͸߶�
	public static  float SCREEN_WIDHT=480;
	public static  float SCREEN_HEIGHT=854;
	//��Ļ�����ű���
	public static float ratio_width;
	public static float ratio_height;
	public static Bitmap scaleToFit(Bitmap bm,float width_Ratio,float height_Ratio)
	{		
    	int width = bm.getWidth(); 							//ͼƬ���
    	int height = bm.getHeight();							//ͼƬ�߶�
    	Matrix matrix = new Matrix(); 
    	matrix.postScale((float)width_Ratio, (float)height_Ratio);				//ͼƬ�ȱ�����СΪԭ����fblRatio��
    	Bitmap bmResult = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);//����λͼ        	
    	return bmResult;									//���ر����ŵ�ͼƬ
    }
	//---------------------��ź�����--------------------
	public static final short GROUP_HOUSE=(short)0xffff;
	public static final short MASK_HOUSE=(short)0xffff;
	
	public static final short GROUP_BALL1=(short)1;//0xffff;
	public static final short MASK_BALL1=(short)1;
	public static final short GROUP_BALL2=(short)2;//0xffff;
	public static final short MASK_BALL2=(short)2;
	public static final short GROUP_BALL3=(short)4;//0xffff;
	public static final short MASK_BALL3=(short)4;
	//----------------------------------------------------
	
	public static final float STARTBALL_1[]=new float[]{-1f,0.4f,2.0041107f};
	public static final float STARTBALL_2[]=new float[]{0,0.4f,2.0041107f};
	public static final float STARTBALL_3[]=new float[]{1f,0.4f,2.0041107f};
	public static final float STARTBALL[][]=new float[][]
	                                     {STARTBALL_1,STARTBALL_2,STARTBALL_3};//��������Ӧλ������
	public static final float STARTBALL_V[][]=new float[][]{
		{0.95f,10.8f*vFactor,-3.0f},
		{0,10.8f*vFactor,-3.0f},
		{-0.95f,10.8f*vFactor,-3.0f}
	};
	public static final float CAMERA_Y_SK=45;//�����Y��ƫ�����ֵ�Ƕ�
	public static final float CAMERA_Y_SK_FH=5f;//������ԭ��λ��ʱ��ÿ��Yƫ�ƴ�С
	public static final float ZJ_LENGTH=0.20f;//l����֧�ܳ���
	public static final float ZJ_R=0.031f;//����֧�ܰ뾶
	public static final float LANQIU_WIDTH=4f;//����ܵĿ��
	public static final float LANQIU_HEIGHT=3.2f;//����ܵĸ߶�
	
	public static final float LANKUANG_R=0.65f;//����뾶
	public static final float LANKUANG_JM_R=0.032f;//�������뾶
	public static final float UNIT_SIZE=1;
	public static final float LANWANG_H=LANKUANG_R*1.5f;//����ĸ߶�
	public static int lanWangRaodon;//�����Ŷ�ֵ
	//���ӵĳ����?��
	public static final float CHANGJING_WIDTH=4.1f; 
	public static final float CHANGJING_HEIGHT=7f;
	public static final float CHANGJING_LENGTH=4f;
	
	public static final float LANBAN_BILIXISHU=0.08f;//����ܴ�С����ϵ��
	public static final float LANBAN_X=0;//�����λ��x���
	public static final float LANBAN_Y=5;//�����λ��y���
	public static final float LANBAN_Z=-1.0f;//�����λ��z���
	public static final float YBB_WIDTH=2;//�Ǳ����
	public static final float YBB_HEIGHT=0.3f;//�Ǳ��߶�
	public static final float QIU_SPAN=11.25f;//������е�λ�зֵĽǶ�
	
	
	//����и�������С
	public static final float QIU_SPAN_SHU=15f;
	public static final float QIU_R=0.35f;//��뾶
	//�������ٶ�
	public static float G=-10f*gFactor;
	//�����Ŀ��λ��
	public static float CAMERA_X=0;
	public static float CAMERA_Y=CHANGJING_HEIGHT/2-0.35f;
	public static float CAMERA_Z=(CHANGJING_LENGTH+2.4f+8.5f);
	
	public static final float DISTANCE=CAMERA_Z;//LENGTH;
	
	//�Ǳ���е������ֵĴ�С
	public static final float SHUZI_KUANDU=0.1f;
	public static final float SHUZI_GAODU=0.12f;
	
	public static float[] ringCenter;//�������ĵ����
	public static float ringR;//����뾶
	//��ǰ�÷�Ŷ
	public static int defen=0;//�÷�
	public static int daojishi=60;//��Ϸ����ʱ
	public static int deadtimesMS;//��Ϸ����ʱ������
	
	//�˵�����
	public static final int SHENGYING_KG_JIEMIAN=1;
	public static final int CAIDAN_JIEMIAN=2;
	public static final int JIAZAI_JIEMIAN=3;
	public static final int BANGZHU_JIEMIAN=4;
	public static final int GUANYU_JIEMIAN=5;
	public static final int YOUXI_JIEMIAN=6;
	public static final int JIESHU_JIEMIAN=7;
	public static final int CAIDAN_RETRY=8;	
	public static final int JILU_JIEMIAN=9;
	
	public static float LEFT=115f; //�˵�λ��
    //�̱߳�־λ
	public static boolean SHENGYING_FLAG=true;//����	���
	public static boolean SOUND_MEMORY=false;//���ڼ�¼������ҵ�ѡ��
	public static boolean DEADTIME_FLAG=false;//����ʱ�̱߳��
	public static boolean MENU_FLAG=false;//�˵���ť�����̱߳��
	
	public static final FloatBuffer[][] mianFXL=new FloatBuffer[][]{//����λ�ã�Ȼ���Ƿ�����
		{getBuffer(0,0.005f,0),getBuffer(0,1,0),},//����
		{getBuffer(-CHANGJING_WIDTH/2+0.005f,0,0),getBuffer(1,0,0)},//����
		{getBuffer(CHANGJING_WIDTH/2-0.005f,0,0),getBuffer(-1,0,0)},//����
		{getBuffer(0,0,-CHANGJING_LENGTH/2+0.005f),getBuffer(0,0,1)},//������� ����
		{getBuffer(0,0,LANBAN_Z+LANBAN_BILIXISHU+0.01f),getBuffer(0,0,1)},//����Ӱ��ƽ��
	};
	public static FloatBuffer getBuffer(float x,float y,float z){//���������꣬�õ�������Ļ���
		float[] lightLocation=new float[]{0,0,0};
		FloatBuffer lightPositionFB;
		lightLocation[0]=x;
    	lightLocation[1]=y;
    	lightLocation[2]=z;
    	ByteBuffer llbb = ByteBuffer.allocateDirect(3*4);
        llbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        lightPositionFB=llbb.asFloatBuffer();
        lightPositionFB.put(lightLocation);
        lightPositionFB.position(0);
        return lightPositionFB;
	}
}