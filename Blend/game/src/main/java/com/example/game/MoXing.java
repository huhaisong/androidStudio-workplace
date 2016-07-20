package com.example.game;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


import android.content.res.Resources;
import android.opengl.GLES20;

//���غ�����塪��Я�����Ϣ���Զ�������ƽ������
public class MoXing 
{	
	int mProgram;//�Զ�����Ⱦ������ɫ������id  
    int muMVPMatrixHandle;//�ܱ任��������
    int muMMatrixHandle;//λ�á���ת�任����
    int maPositionHandle; //����λ����������  
    int maNormalHandle; //���㷨������������  
    int maLightLocationHandle;//��Դλ����������  
    int maCameraHandle; //�����λ����������
    int maTexCoorHandle; //�������������������  
    String mVertexShader;//������ɫ������ű�    	 
    String mFragmentShader;//ƬԪ��ɫ������ű�   
	
	FloatBuffer   mVertexBuffer;//���������ݻ���  
	FloatBuffer   mNormalBuffer;//���㷨������ݻ���
	FloatBuffer   mTexCoorBuffer;//�������������ݻ���
    int dingdianShu=0;  
    public MoXing(Resources r,float[] vertices,float[] normals,float texCoors[])
    {    	
    	//���ó�ʼ��������ݵ�initVertexData����
    	initVertexData(vertices,normals,texCoors);    	
    }
    
    //��ʼ��������ݵ�initVertexData����
    public void initVertexData(float[] vertices,float[] normals,float texCoors[])
    {
    	//���������ݵĳ�ʼ��================begin============================
    	dingdianShu=vertices.length/3;   
		
        //�������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥��������
        mVertexBuffer.position(0);//���û�������ʼλ��
        
        //���㷨������ݵĳ�ʼ��================begin============================  
        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length*4);
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mNormalBuffer = cbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mNormalBuffer.put(normals);//�򻺳����з��붥�㷨�������
        mNormalBuffer.position(0);//���û�������ʼλ��
        
        //�������������ݵĳ�ʼ��================begin============================  
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length*4);
        tbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mTexCoorBuffer = tbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mTexCoorBuffer.put(texCoors);//�򻺳����з��붥������������
        mTexCoorBuffer.position(0);//���û�������ʼλ��

    }

    //��ʼ����ɫ����initShader����
    public void initShader(int mProgram)
    {
        this.mProgram=mProgram; 
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж�����ɫ��������id  
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
        //��ȡλ�á���ת�任��������id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix"); 
        //��ȡ�����й�Դλ������id
        maLightLocationHandle=GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //��ȡ�����ж������������������id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor"); 
        //��ȡ�����������λ������id
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera"); 
    }
    
   

	public void drawSelf() {
		 //�ƶ�ʹ��ĳ��shader����
	   	 GLES20.glUseProgram(mProgram);
	        //�����ձ任������shader����
	        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
	        //��λ�á���ת�任������shader����
	        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);   
	        //����Դλ�ô���shader����   
	        GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
	        //�������λ�ô���shader����   
	        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
	        //���붥��λ�����
	        GLES20.glVertexAttribPointer  
	        (
	        		maPositionHandle,   
	        		3, 
	        		GLES20.GL_FLOAT, 
	        		false,
	               3*4,   
	               mVertexBuffer
	        );       
	        //���붥�㷨�������
	        GLES20.glVertexAttribPointer  
	        (
	       		maNormalHandle, 
	        		3,   
	        		GLES20.GL_FLOAT, 
	        		false,
	               3*4,   
	               mNormalBuffer
	        );   
	        //���붥������������
	        GLES20.glVertexAttribPointer  
	        (
	       		maTexCoorHandle, 
	        		2, 
	        		GLES20.GL_FLOAT, 
	        		false,
	               2*4,   
	               mTexCoorBuffer
	        );   
	        //���?��λ�á�����������������������
	        GLES20.glEnableVertexAttribArray(maPositionHandle);  
	        GLES20.glEnableVertexAttribArray(maNormalHandle);  
	        GLES20.glEnableVertexAttribArray(maTexCoorHandle); 
	        //���Ƽ��ص�����
	        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, dingdianShu);  
		
	}
}
