package com.example.a3d_model.glsurfaceview;

import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.GLES20;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.a3d_model.util.MatrixState;
import com.example.a3d_model.R;
import com.example.a3d_model.model.Regular20;
import com.example.a3d_model.model.Regular20L;

public class MySurfaceView extends GLSurfaceView {
    
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//�Ƕ����ű���
	private float mPreviousY;//�ϴεĴ���λ��Y���
    private float mPreviousX;//�ϴεĴ���λ��X���
	
	private SceneRenderer mRenderer;//������Ⱦ��
    int textureId;      //ϵͳ���������id 
    
    public boolean drawWhatFlag=true;	//��������䷽ʽ�ı�־λ
    public boolean lightFlag=true;		//������ת�ı�־λ

	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //����ʹ��OPENGL ES2.0
        mRenderer = new SceneRenderer();	//����������Ⱦ��
        setRenderer(mRenderer);				//������Ⱦ��		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ   
    }
	
	//�����¼��ص�����
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//���㴥�ر�Yλ��
            float dx = x - mPreviousX;//���㴥�ر�Xλ��
            mRenderer.regular20.yAngle += dx * TOUCH_SCALE_FACTOR;//������y����ת�Ƕ�
            mRenderer.regular20.zAngle+= dy * TOUCH_SCALE_FACTOR;//������z����ת�Ƕ�
            
            mRenderer.regular20l.yAngle += dx * TOUCH_SCALE_FACTOR;//������y����ת�Ƕ�
            mRenderer.regular20l.zAngle+= dy * TOUCH_SCALE_FACTOR;//������z����ת�Ƕ�
        }
        mPreviousY = y;//��¼���ر�λ��
        mPreviousX = x;//��¼���ر�λ��
        return true;
    }
    
	private class SceneRenderer implements Renderer
    {   
		
		Regular20 regular20;
		Regular20L regular20l;
		
        public void onDrawFrame(GL10 gl) 
        { 
        	//�����Ȼ�������ɫ����
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);   
            
            //�����ֳ�
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -10);
            
            if(drawWhatFlag)
            {
            	regular20.drawSelf(textureId);
            }
            else
            {
            	regular20l.drawSelf();
            }
            
            MatrixState.popMatrix();
        }   

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //�����Ӵ���С��λ�� 
        	GLES20.glViewport(0, 0, width, height); 
        	//����GLSurfaceView�Ŀ�߱�
            float ratio= (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
            MatrixState.setCamera(0,0,8.0f,0f,0f,0f,0f,1.0f,0.0f);
            
	        MatrixState.setLightLocation(10 , 0 , -10);
	                      
	        new Thread()
	        {
				public void run()
				{
					float redAngle = 0;
					while(lightFlag)
					{	
						redAngle=(redAngle+5)%360;
						float rx=(float) (15*Math.sin(Math.toRadians(redAngle)));
						float rz=(float) (15*Math.cos(Math.toRadians(redAngle)));
						MatrixState.setLightLocation(rx, 0, rz);
						
						try {
								Thread.sleep(40);
							} catch (InterruptedException e) {				  			
								e.printStackTrace();
							}
					}
				}
	        }.start();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //������Ļ����ɫRGBA
            GLES20.glClearColor(0.0f,0.0f,0.0f, 1.0f);  
            //������Ȳ���
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    		//����Ϊ�򿪱������
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            //��ʼ���任����
            MatrixState.setInitStack();
            //��������
            textureId=initTexture(R.drawable.android_robot0);
            
            //������20�������
            regular20 = new Regular20(MySurfaceView.this,2,1.6f,5);
            //������20����Ǽܶ���
            regular20l= new Regular20L(MySurfaceView.this,2,1.6f,5);
            
        }
    }
	
	public int initTexture(int drawableId)//textureId
	{
		//�������ID
		int[] textures = new int[1];
		GLES20.glGenTextures
		(
				1,          //���������id������
				textures,   //����id������
				0           //ƫ����
		);    
		int textureId=textures[0];    
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        
        //ͨ������������ͼƬ===============begin===================
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
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
        //ͨ������������ͼƬ===============end=====================  
        
        //ʵ�ʼ�������
        GLUtils.texImage2D
        (
        		GLES20.GL_TEXTURE_2D,   //�������ͣ���OpenGL ES�б���ΪGL10.GL_TEXTURE_2D
        		0, 					  //����Ĳ�Σ�0��ʾ��ͼ��㣬�������Ϊֱ����ͼ
        		bitmapTmp, 			  //����ͼ��
        		0					  //����߿�ߴ�
        );
        bitmapTmp.recycle(); 		  //������سɹ����ͷ�ͼƬ
        
        return textureId;
	}
}
