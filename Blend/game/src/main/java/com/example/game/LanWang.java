package com.example.game;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES20;

public class LanWang {
	int mProgram;//�Զ�����Ⱦ������ɫ������id
    int muMVPMatrixHandle;//�ܱ任��������
    int maPositionHandle; //����λ����������  
    int maTexCoorHandle; //�������������������  
    int maNormalHandle; //���㷨������������      
    int muraodonHandle;//�Ŷ�ֵ����
	
	FloatBuffer   mVertexBuffer;//���������ݻ���
	FloatBuffer   mTexCoorBuffer;//�������������ݻ���
    int vCount=0;   
	public LanWang(float R,float r,float height,int hSection){
		ArrayList<Float> alVertix=new ArrayList<Float>();
		float h=height/hSection;//ÿһС�εĸ߶�
		int dians=18;//ÿ���зֶ���
		int arey=360/dians;
		for(int i=0;i<hSection;i++){
			for(int j=0;j<arey;j++){
				float hr=i*h;//��һ���ֶεĸ߶�
				float Rhr1=r+(hr/height)*(R-r);//��һ���ֶεİ뾶
				float hr2=(i+1)*h;//��һ���ֶεĸ߶�
				float Rhr2=r+(hr2/height)*(R-r);//��һ�εİ뾶
				
				float x1=Rhr1*(float)(Math.cos(Math.toRadians(dians*j)));
				float y1=hr;
				float z1=Rhr1*(float)(Math.sin(Math.toRadians(dians*j)));
				
				float x2=Rhr1*(float)(Math.cos(Math.toRadians(dians*(1+j))));
				float y2=hr;
				float z2=Rhr1*(float)(Math.sin(Math.toRadians(dians*(1+j))));
				
				float x3=Rhr2*(float)(Math.cos(Math.toRadians(dians*j)));
				float y3=hr2;
				float z3=Rhr2*(float)(Math.sin(Math.toRadians(dians*j)));
				
				float x4=Rhr2*(float)(Math.cos(Math.toRadians(dians*(1+j))));
				float y4=hr2;
				float z4=Rhr2*(float)(Math.sin(Math.toRadians(dians*(1+j))));
				
				//������һ�����
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3);
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);        		
        		//�����ڶ������
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		
        		//�����
        		//������һ�����
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3);
        		//�����ڶ������
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
			}
		}
		
		 	vCount=alVertix.size()/3;//���������Ϊ���ֵ������1/3����Ϊһ��������3�����
	        //��alVertix�е����ֵת�浽һ��float������
	        float vertices[]=new float[vCount*3];
	    	for(int i=0;i<alVertix.size();i++)
	    	{
	    		vertices[i]=alVertix.get(i);
	    	}
	    	//�������������ݻ���
	        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
	        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ��ϵͳ����˳��
	        mVertexBuffer = vbb.asFloatBuffer();//ת��ΪFloat�ͻ���
	        mVertexBuffer.put(vertices);//�򻺳����з��붥��������
	        mVertexBuffer.position(0);//���û�������ʼλ��

	        //�������ƶ��㷨��������
	        //�������������ݵĳ�ʼ��================begin============================
	        float texCoor[]=generateTexCoor
	    	(
	   			 (int)(360/dians), //����ͼ�зֵ�����
	   			hSection  //����ͼ�зֵ�����
	   	    );
	        //�����������������ݻ���
	        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
	        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ��ϵͳ����˳��
	        mTexCoorBuffer = cbb.asFloatBuffer();//ת��ΪFloat�ͻ���
	        mTexCoorBuffer.put(texCoor);//�򻺳����з��붥����ɫ���
	        mTexCoorBuffer.position(0);//���û�������ʼλ��
	}
	//��ʼ����ɫ����initShader����
    public void initShader(int mProgram)
    {
    	this.mProgram=mProgram; 
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж������������������id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //��ȡ�Ŷ�֡����ID
        muraodonHandle=GLES20.glGetUniformLocation(mProgram, "uraodon");
    }
    public void drawSelf(int texId,int raodon)
	{
		 //�ƶ�ʹ��ĳ��shader����
   	 	GLES20.glUseProgram(mProgram);
        //�����ձ任������shader����
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
        //����Ҫ�Ŷ���֡��
        GLES20.glUniform1i(muraodonHandle, raodon);
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
        //����λ���������
        GLES20.glEnableVertexAttribArray(maPositionHandle);  
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
        //������
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        //�����������
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
	}
	 //�Զ��з����������������ķ���
    public float[] generateTexCoor(int bw,int bh)
    {
    	float[] result=new float[bw*bh*6*2*2]; 
    	float sizew=1.0f/bw;//����
    	float sizeh=1.0f/bh;//����
    	int c=0;
    	for(int i=0;i<bh;i++)
    	{
    		for(int j=0;j<bw;j++)
    		{
    			//ÿ����һ�����Σ�����������ι��ɣ�������㣬12���������
    			float s=(bw-j)*sizew;
    			float t=(bh-i)*sizeh;
    			
    			result[c++]=s;
    			result[c++]=t;
    			
    			result[c++]=s;
    			result[c++]=t-sizeh;
    			
    			result[c++]=s-sizew;
    			result[c++]=t-sizeh;
    			
    			result[c++]=s;
    			result[c++]=t;
    			
    			result[c++]=s-sizew;
    			result[c++]=t-sizeh;
    			
    			result[c++]=s-sizew;
    			result[c++]=t;
    			
    			
    			result[c++]=s;
    			result[c++]=t;
    			
    			
    			
    			result[c++]=s-sizew;
    			result[c++]=t-sizeh;
    			
    			result[c++]=s;
    			result[c++]=t-sizeh;
    			
    			
    			
    			result[c++]=s;
    			result[c++]=t;
    			
    			
    			
    			result[c++]=s-sizew;
    			result[c++]=t;
    			
    			result[c++]=s-sizew;
    			result[c++]=t-sizeh;
    			    			
    		}
    	}
    	return result;
    }                                              

}
