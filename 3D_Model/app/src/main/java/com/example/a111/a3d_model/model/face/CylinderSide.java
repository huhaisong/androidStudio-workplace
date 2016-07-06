package com.example.a111.a3d_model.model.face;
import static com.example.a111.a3d_model.util.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;

import com.example.a111.a3d_model.glsurfaceview.MySurfaceView;
import com.example.a111.a3d_model.util.MatrixState;
import com.example.a111.a3d_model.util.ShaderUtil;

//Բ�����
public class CylinderSide 
{	
	int mProgram;//�Զ�����Ⱦ������ɫ������id
    int muMVPMatrixHandle;//�ܱ任��������
    int maPositionHandle; //����λ���������� 
    int maTexCoorHandle; //�������������������
    
    int muMMatrixHandle;//λ�á���ת�����ű任����
    int maCameraHandle; //�����λ����������
    int maNormalHandle; //���㷨������������
    int maLightLocationHandle;//��Դλ���������� 
    
    
    String mVertexShader;//������ɫ������ű�	 
    String mFragmentShader;//ƬԪ��ɫ������ű�
	
	FloatBuffer   mVertexBuffer;//���������ݻ���
	FloatBuffer   mTexCoorBuffer;//�������������ݻ���
	FloatBuffer   mNormalBuffer;//���㷨������ݻ���
    int vCount=0;   
    float xAngle=0;//��x����ת�ĽǶ�
    float yAngle=0;//��y����ת�ĽǶ�
    float zAngle=0;//��z����ת�ĽǶ�
    
    public CylinderSide(MySurfaceView mv, float scale, float r, float h, int n)
    {    	
    	//���ó�ʼ��������ݵ�initVertexData����
    	initVertexData(scale,r,h,n);
    	//���ó�ʼ����ɫ����intShader����   
    	initShader(mv);
    }
    
    //�Զ����ʼ�����������ݵķ���
    public void initVertexData(
    		float scale,	//��С
    		float r,		//�뾶
    		float h,		//�߶�
    		int n			//�зֵķ���
    	)
    {
    	r=scale*r;
    	h=scale*h;
    	
		float angdegSpan=360.0f/n;
		vCount=3*n*4;//���������3*n*4������Σ�ÿ������ζ����������
		//�����ݳ�ʼ��
		float[] vertices=new float[vCount*3];
		float[] textures=new float[vCount*2];//��������S��T���ֵ����
		//�����ݳ�ʼ��
		int count=0;
		int stCount=0;
		for(float angdeg=0;Math.ceil(angdeg)<360;angdeg+=angdegSpan)//����
		{
			double angrad=Math.toRadians(angdeg);//��ǰ����
			double angradNext=Math.toRadians(angdeg+angdegSpan);//��һ����
			//��Բ��ǰ��---0
			vertices[count++]=(float) (-r*Math.sin(angrad));
			vertices[count++]=0;
			vertices[count++]=(float) (-r*Math.cos(angrad));
			
			textures[stCount++]=(float) (angrad/(2*Math.PI));//st���
			textures[stCount++]=1;
			//��Բ��һ��---3
			vertices[count++]=(float) (-r*Math.sin(angradNext));
			vertices[count++]=h;
			vertices[count++]=(float) (-r*Math.cos(angradNext));
			
			textures[stCount++]=(float) (angradNext/(2*Math.PI));//st���
			textures[stCount++]=0;
			//��Բ��ǰ��---2
			vertices[count++]=(float) (-r*Math.sin(angrad));
			vertices[count++]=h;
			vertices[count++]=(float) (-r*Math.cos(angrad));
			
			textures[stCount++]=(float) (angrad/(2*Math.PI));//st���
			textures[stCount++]=0;
			
			//��Բ��ǰ��---0
			vertices[count++]=(float) (-r*Math.sin(angrad));
			vertices[count++]=0;
			vertices[count++]=(float) (-r*Math.cos(angrad));
			
			textures[stCount++]=(float) (angrad/(2*Math.PI));//st���
			textures[stCount++]=1;
			//��Բ��һ��---1
			vertices[count++]=(float) (-r*Math.sin(angradNext));
			vertices[count++]=0;
			vertices[count++]=(float) (-r*Math.cos(angradNext));
			
			textures[stCount++]=(float) (angradNext/(2*Math.PI));//st���
			textures[stCount++]=1;
			//��Բ��һ��---3
			vertices[count++]=(float) (-r*Math.sin(angradNext));
			vertices[count++]=h;
			vertices[count++]=(float) (-r*Math.cos(angradNext));
			
			textures[stCount++]=(float) (angradNext/(2*Math.PI));//st���
			textures[stCount++]=0;
		}
        //��������ݳ�ʼ��  
        float[] normals=new float[vertices.length];
        for(int i=0;i<vertices.length;i++){
        	if(i%3==1){
        		normals[i]=0;  
        	}else{
            	normals[i]=vertices[i];
        	}
        }
		
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//�������������ݻ���
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥��������
        mVertexBuffer.position(0);//���û�������ʼλ��

        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length*4);//�������㷨������ݻ���
        nbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mNormalBuffer = nbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mNormalBuffer.put(normals);//�򻺳����з��붥�㷨�������
        mNormalBuffer.position(0);//���û�������ʼλ��
        
        //st�����ݳ�ʼ��
        ByteBuffer cbb = ByteBuffer.allocateDirect(textures.length*4);//��������������ݻ���
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mTexCoorBuffer = cbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mTexCoorBuffer.put(textures);//�򻺳����з��붥���������
        mTexCoorBuffer.position(0);//���û�������ʼλ��
    }

    //�Զ����ʼ����ɫ����initShader����
    public void initShader(MySurfaceView mv)
    {
    	//���ض�����ɫ���Ľű�����
        mVertexShader= ShaderUtil.loadFromAssetsFile("vertex_tex_light.sh", mv.getResources());
        //����ƬԪ��ɫ���Ľű�����
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_tex_light.sh", mv.getResources());  
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж������������������id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        
        //��ȡ�����ж��㷨������������id  
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal"); 
        //��ȡ�����������λ������id
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera"); 
        //��ȡ�����й�Դλ������id
        maLightLocationHandle=GLES20.glGetUniformLocation(mProgram, "uLightLocation"); 
        //��ȡλ�á���ת�任��������id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");  
        
        
    }
    
    public void drawSelf(int texId)
    {        
    	 //�ƶ�ʹ��ĳ��shader����
    	 GLES20.glUseProgram(mProgram);        
         //�����ձ任������shader����
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         //��λ�á���ת�任������shader����
         GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0); 
         //�������λ�ô���shader����   
         GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
         //����Դλ�ô���shader����   
         GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
         
         //���Ͷ���λ�����
         GLES20.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //���Ͷ�������������
         GLES20.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES20.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         ); 
         //���Ͷ��㷨�������
         GLES20.glVertexAttribPointer  
         (
        		maNormalHandle, 
         		4, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mNormalBuffer
         ); 
         
         //���ö���λ�����
         GLES20.glEnableVertexAttribArray(maPositionHandle);
         //���ö����������
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
         //���ö��㷨�������
         GLES20.glEnableVertexAttribArray(maNormalHandle);
         //������
         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
         
         //�����������
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
    }
}
