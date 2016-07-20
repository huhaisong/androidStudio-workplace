package com.example.game;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import android.opengl.GLES20;
import static com.example.game.Constant.*;
//���ڻ��Ƶ���
public class BasketBallTextureByVertex 
{	
	int mProgram;//�Զ�����Ⱦ������ɫ������id
    int muMVPMatrixHandle;//�ܱ任��������
    int muMMatrixHandle;//λ�á���ת�任��������
    int muCameraMatrixHandle;//������������
    int muProjMatrixHandle;//ͶӰ��������
    int maPositionHandle; //����λ����������
    int maTexCoorHandle; //������������������� 
    int maNormalHandle; //���㷨������������ 
    int maLightLocationHandle;//��Դλ����������
    int maCameraHandle; //�����λ����������
    int muIsShadow;//�Ƿ������Ӱ��������  
    int muIsLanBanShdow;//�Ƿ�Ϊ�����ϵ���Ӱ
    int muIsShadowFrag;//�Ƿ������Ӱ��������
    int muBallTexHandle;//����������������
    int muTableTexHandle;//���ڻ�����Ӱ������������������ 
    int muPlaneN;//ƽ�淨��������
    int muPlaneV;//ƽ���ϵ�һ����
    String mVertexShader;//������ɫ������ű�    	 
    String mFragmentShader;//ƬԪ��ɫ������ű�
	
	FloatBuffer   mVertexBuffer;//���������ݻ���
	FloatBuffer   mTexCoorBuffer;//�������������ݻ���
	FloatBuffer   mNormalBuffer;//���㷨������ݻ���
    int vCount=0;   
    public BasketBallTextureByVertex(float scale)
    {    	
    	//���ó�ʼ��������ݵ�initVertexData����
    	initVertexData(scale); 
    }
    //��ʼ��������ݵ�initVertexData����
    public void initVertexData(float scale)
    {
    	//���������ݵĳ�ʼ��
    	ArrayList<Float> alVertix=new ArrayList<Float>();//��Ŷ�������ArrayList    	
        for(float vAngle=90;vAngle>-90;vAngle=vAngle-QIU_SPAN)//��ֱ����angleSpan��һ��
        {
        	for(float hAngle=360;hAngle>0;hAngle=hAngle-QIU_SPAN)//ˮƽ����angleSpan��һ��
        	{
        		//����������һ���ǶȺ�����Ӧ�Ĵ˵��������ϵ��ı��ζ������
        		//��������������ı��ε������
        		
        		double xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle));
        		float x1=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
        		float z1=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
        		float y1=(float)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));
        		
        		xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle-QIU_SPAN));
        		float x2=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
        		float z2=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
        		float y2=(float)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle-QIU_SPAN)));
        		
        		xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle-QIU_SPAN));
        		float x3=(float)(xozLength*Math.cos(Math.toRadians(hAngle-QIU_SPAN)));
        		float z3=(float)(xozLength*Math.sin(Math.toRadians(hAngle-QIU_SPAN)));
        		float y3=(float)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle-QIU_SPAN)));
        		
        		xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle));
        		float x4=(float)(xozLength*Math.cos(Math.toRadians(hAngle-QIU_SPAN)));
        		float z4=(float)(xozLength*Math.sin(Math.toRadians(hAngle-QIU_SPAN)));
        		float y4=(float)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));   
        		
        		//������һ�����
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);        		
        		//�����ڶ������
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3); 
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
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥��������
        mVertexBuffer.position(0);//���û�������ʼλ��

        //�������ƶ��㷨��������
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length*4);
        nbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mNormalBuffer = nbb.asFloatBuffer();//ת��Ϊint�ͻ���
        mNormalBuffer.put(vertices);//�򻺳����з��붥��������
        mNormalBuffer.position(0);//���û�������ʼλ��     
        //�������������ݵĳ�ʼ��================begin============================
        float texCoor[]=generateTexCoor
    	(
   			 (int)(360/QIU_SPAN), //����ͼ�зֵ�����
   			 (int)(180/QIU_SPAN)  //����ͼ�зֵ�����
   	    );
        //�����������������ݻ���
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mTexCoorBuffer = cbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mTexCoorBuffer.put(texCoor);//�򻺳����з��붥����ɫ���
        mTexCoorBuffer.position(0);//���û�������ʼλ��

    }
    //��ʼ����ɫ����intShader����
    public void initShader(int mProgram)
    {
        this.mProgram=mProgram; 
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж������������������id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //��ȡλ�á���ת�任��������id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");  
        //��ȡ�����ж��㷨������������id  
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal");
        //��ȡ�����й�Դλ������id
        maLightLocationHandle=GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //��ȡ�����������λ������id
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera"); 
        //��ȡ�������Ƿ������Ӱ��������id
        muIsShadow=GLES20.glGetUniformLocation(mProgram, "uisShadow"); 
        muIsShadowFrag=GLES20.glGetUniformLocation(mProgram, "uisShadowFrag");
        //��ȡ�Ƿ������������Ӱ����Ӧ��ID
        muIsLanBanShdow=GLES20.glGetUniformLocation(mProgram, "uisLanbanFrag");
        //��ȡ������������������id
        muCameraMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMCameraMatrix"); 
        //��ȡ������ͶӰ��������id
        muProjMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMProjMatrix");  
        //��ȡ����������������id 
        muBallTexHandle=GLES20.glGetUniformLocation(mProgram, "usTextureBall"); 
        //��ȡ������ƽ�淨��������id;
        muPlaneN=GLES20.glGetUniformLocation(mProgram, "uplaneN");
        //��ȡ������ƽ���ϵĵ�����õ�Id
        muPlaneV=GLES20.glGetUniformLocation(mProgram, "uplaneA");
    }
    public void drawSelf(int ballTexId,int isShadow,int planeId,int isLanbanYy)//0-no shadow 1-with shadow
    {        
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
         //���Ƿ������Ӱ���Դ���shader���� 
         GLES20.glUniform1i(muIsShadow, isShadow);
         GLES20.glUniform1i(muIsShadowFrag, isShadow);     
         GLES20.glUniform1i(muIsLanBanShdow, isLanbanYy);
         //������������shader����
         GLES20.glUniformMatrix4fv(muCameraMatrixHandle, 1, false, MatrixState.mVMatrix, 0); 
         //��ͶӰ������shader����
         GLES20.glUniformMatrix4fv(muProjMatrixHandle, 1, false, MatrixState.mProjMatrix, 0); 
         //��ƽ��λ�ô������
         GLES20.glUniform3fv(muPlaneV, 1, Constant.mianFXL[planeId][0]);
         //��ƽ�淨�����������
         GLES20.glUniform3fv(muPlaneN, 1, Constant.mianFXL[planeId][1]);
         
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
         
         //���?��λ�á�������ꡢ�������������
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
         GLES20.glEnableVertexAttribArray(maNormalHandle);  
         
         //������
         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ballTexId);    
         GLES20.glUniform1i(muBallTexHandle, 0);
         //�����������
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
    }
    //�Զ��з����������������ķ���
    public float[] generateTexCoor(int bw,int bh)
    {
    	float[] result=new float[bw*bh*6*2]; 
    	float sizew=1.0f/bw;//����
    	float sizeh=1.0f/bh;//����
    	int c=0;
    	for(int i=0;i<bh;i++)
    	{
    		for(int j=0;j<bw;j++)
    		{
    			//ÿ����һ�����Σ�����������ι��ɣ�������㣬12���������
    			float s=j*sizew;
    			float t=i*sizeh;
    			
    			result[c++]=s;
    			result[c++]=t;
    			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s+sizew;
    			result[c++]=t;
    			
    			
    			result[c++]=s+sizew;
    			result[c++]=t;
    			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s+sizew;
    			result[c++]=t+sizeh;    			
    		}
    	}
    	return result;
    }
}
