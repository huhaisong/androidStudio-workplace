package com.example.texturetest;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MyActivity extends Activity {
	private MySurfaceView mGLSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState)  
    {
    	super.onCreate(savedInstanceState);         
        //����Ϊȫ��
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//����Ϊ����ģʽ
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//�л���������
		setContentView(R.layout.main);		
		//��ʼ��GLSurfaceView
        mGLSurfaceView = new MySurfaceView(this);
        mGLSurfaceView.requestFocus();//��ȡ����
        mGLSurfaceView.setFocusableInTouchMode(true);//����Ϊ�ɴ���  
        //���Զ����GLSurfaceView��ӵ����LinearLayout��
        LinearLayout ll=(LinearLayout)findViewById(R.id.main_liner); 
        ll.addView(mGLSurfaceView); 
        
        //ΪRadioButton��Ӽ�������SxTѡ�����
        RadioButton rab=(RadioButton)findViewById(R.id.edge);
        rab.setOnCheckedChangeListener(
            new OnCheckedChangeListener()
            {
     			@Override
     			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
     			{
     				//GL_CLAMP_TO_EDGEģʽ��
     				if(isChecked)
     				{
     					mGLSurfaceView.currTextureId=mGLSurfaceView.textureCTId;
     				}
     			}        	   
            }         		
        );       
        rab=(RadioButton)findViewById(R.id.repeat);
        rab.setOnCheckedChangeListener(
            new OnCheckedChangeListener()
            {
     			@Override
     			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
     			{
     				if(isChecked)
     				{
     					mGLSurfaceView.currTextureId=mGLSurfaceView.textureREId;
     				}
     			}        	   
            }         		
        );        

        RadioButton rb=(RadioButton)findViewById(R.id.x11);
        rb.setOnCheckedChangeListener(
            new OnCheckedChangeListener()
            {
     			@Override
     			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
     			{
     				if(isChecked)
     				{
     					mGLSurfaceView.trIndex=0;
     				}
     			}        	   
            }         		
        );       
        rb=(RadioButton)findViewById(R.id.x42);
        rb.setOnCheckedChangeListener(
            new OnCheckedChangeListener()
            {
     			@Override
     			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
     			{
     				if(isChecked)
     				{
     					mGLSurfaceView.trIndex=1;
     				}
     			}        	   
            }         		
        );     
        rb=(RadioButton)findViewById(R.id.x44);
        rb.setOnCheckedChangeListener(
            new OnCheckedChangeListener()
            {
     			@Override
     			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
     			{
     				if(isChecked)
     				{//����Ϊ�������SxT=4x4
     					mGLSurfaceView.trIndex=2;
     				}
     			}        	   
            }         		
        );   
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }    
}



