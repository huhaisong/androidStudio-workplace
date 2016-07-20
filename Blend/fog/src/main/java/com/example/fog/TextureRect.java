package com.example.fog;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;
public class TextureRect 
{
	int mProgram;//�Զ�����Ⱦ������ɫ������id
    int muMVPMatrixHandle;//�ܱ任��������  
    int muMMatrixHandle;//λ�á���ת�任����
    int maCameraHandle; //�����λ���������� 
    int maPositionHandle; //����λ���������� 
    int maNormalHandle; //���㷨������������  
    int maLightLocationHandle;//��Դλ����������  
    
    String mVertexShader;//������ɫ��    	 
    String mFragmentShader;//ƬԪ��ɫ��
	
    private FloatBuffer   mVertexBuffer;//���������ݻ���
	FloatBuffer   mNormalBuffer;//���㷨������ݻ���
    int vCount;//��������
    
    float width;
    float height;
    
	public TextureRect(MySurfaceView mv, 
			float width,float height	//���εĿ��
			)
	{

		this.width=width;
    	this.height=height;
    	
		initVertexData();
        initShader(mv);
        
	}
    //��ʼ�������������ɫ��ݵķ���
    public void initVertexData()
    {
    	//���������ݵĳ�ʼ��================begin============================
        vCount=6;//ÿ��������������Σ�ÿ�������3������        
        float vertices[]=
        {
        		-width/2, 0,-height/2,
        		-width/2, 0,height/2,
        		width/2, 0,height/2,
        		
        		-width/2, 0,-height/2,
        		width/2, 0,height/2,
        		width/2, 0, -height/2,
        };
        //�������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);//���û�������ʼλ��
        
        float[] normals = {
        		0,1,0,
        		0,1,0,
        		0,1,0,
        		0,1,0,
        		0,1,0,
        		0,1,0,
        };
        //���㷨������ݵĳ�ʼ��================begin============================  
        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length*4);
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mNormalBuffer = cbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mNormalBuffer.put(normals);//�򻺳����з��붥�㷨�������
        mNormalBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ��ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //������ɫ��ݵĳ�ʼ��================end============================
    }
    public void initShader(MySurfaceView mv)
    {
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_light.sh", mv.getResources());
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_light.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera");
        maLightLocationHandle=GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix"); 
    }
	public void drawSelf()
	{
		 //�ƶ�ʹ��ĳ����ɫ������
   	 	GLES20.glUseProgram(mProgram);
        //�����ձ任��������ɫ������
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //��λ�á���ת�任��������ɫ������
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);   
        //�������λ�ô�����ɫ������   
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        //����Դλ�ô�����ɫ������   
        GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        //�����㷨������ݴ�����Ⱦ����
        GLES20.glVertexAttribPointer  
        (
        		maPositionHandle,   
        		3, 
        		GLES20.GL_FLOAT, 
        		false,
               3*4,   
               mVertexBuffer
        );       
        //�����㷨������ݴ�����Ⱦ����
        GLES20.glVertexAttribPointer  
        (
       		maNormalHandle, 
        		3,   
        		GLES20.GL_FLOAT, 
        		false,
               3*4,   
               mNormalBuffer
        );   
        //���?��λ���������
        GLES20.glEnableVertexAttribArray(maPositionHandle);  
        GLES20.glEnableVertexAttribArray(maNormalHandle);  
        
        //���ƾ���
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
	}
	
}
