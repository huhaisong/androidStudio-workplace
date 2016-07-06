package com.example.a111.a3d_model.glsurfaceview;

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

import com.example.a111.a3d_model.model.face.Cylinder;
import com.example.a111.a3d_model.model.line.CylinderL;
import com.example.a111.a3d_model.R;
import com.example.a111.a3d_model.util.MatrixState;

public class MySurfaceView extends GLSurfaceView {
    
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;
	private float mPreviousY;
    private float mPreviousX;
	
	private SceneRenderer mRenderer;
    int textureId;
    
    public boolean drawWhatFlag=true;
    public boolean lightFlag=true;

	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        mRenderer = new SceneRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//���㴥�ر�Yλ��
            float dx = x - mPreviousX;//���㴥�ر�Xλ��
            mRenderer.cylinder.yAngle += dx * TOUCH_SCALE_FACTOR;//������y����ת�Ƕ�
            mRenderer.cylinder.zAngle+= dy * TOUCH_SCALE_FACTOR;//������z����ת�Ƕ�
            
            mRenderer.cylinderl.yAngle += dx * TOUCH_SCALE_FACTOR;//������y����ת�Ƕ�
            mRenderer.cylinderl.zAngle+= dy * TOUCH_SCALE_FACTOR;//������z����ת�Ƕ�
        }
        mPreviousY = y;//��¼���ر�λ��
        mPreviousX = x;//��¼���ر�λ��
        return true;
    }
    
	private class SceneRenderer implements Renderer
    {   
		
		Cylinder cylinder;
		CylinderL cylinderl;
		
        public void onDrawFrame(GL10 gl) 
        { 
        	//�����Ȼ�������ɫ����
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);   
            
            //�����ֳ�
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -10);
            if(drawWhatFlag)
            {
            	cylinder.drawSelf();
            }else
            {
            	cylinderl.drawSelf();
            }
            MatrixState.popMatrix();
            
        }   

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //�����Ӵ���С��λ�� 
        	GLES20.glViewport(0, 0, width, height); 
        	//����GLSurfaceView�Ŀ�߱�
            float ratio= (float) width / height;
            //���ô˷����������͸��ͶӰ����
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
            //���ô˷������������9����λ�þ���
            MatrixState.setCamera(0,0,8.0f,0f,0f,0f,0f,1.0f,0.0f); 
            
	        //��ʼ����Դ
	        MatrixState.setLightLocation(10 , 0 , -10);
	                      
	        //����һ���̶߳�ʱ�޸ĵƹ��λ��
	        new Thread()
	        {
				public void run()
				{
					float redAngle = 0;
					while(lightFlag)
					{	
						//��ݽǶȼ���ƹ��λ��
						redAngle=(redAngle+5)%360;
						float rx=(float) (15*Math.sin(Math.toRadians(redAngle)));
						float rz=(float) (15*Math.cos(Math.toRadians(redAngle)));
						MatrixState.setLightLocation(rx, 0, rz);
						
						try {
								Thread.sleep(100);
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
            //����Բ�����
            cylinder = new Cylinder(MySurfaceView.this,1,1.2f,3.9f,36,textureId,textureId,textureId);
            //����Բ��Ǽܶ���
            cylinderl= new CylinderL(MySurfaceView.this,1,1.2f,3.9f,36);
        }
    }
	
	public int initTexture(int drawableId)
	{
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int textureId=textures[0];    
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);

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
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();
        return textureId;
	}
}
