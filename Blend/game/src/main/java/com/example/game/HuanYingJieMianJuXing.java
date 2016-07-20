package com.example.game;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;
//�����Ҫ�ǻ�ӭ���� ���������
public class HuanYingJieMianJuXing 
{
	int mProgram;//�Զ�����Ⱦ������ɫ������id 
    int muMVPMatrixHandle;//�ܱ任��������   
    int muMMatrixHandle;//λ�á���ת�任����
    int maCameraHandle; //�����λ����������  
    int maPositionHandle; //����λ����������  
    int maNormalHandle; //���㷨������������  
    int maTexCoorHandle; //�������������������  
    int maSunLightLocationHandle;//��Դλ����������  
    
    String mVertexShader;//������ɫ������ű�    	 
    String mFragmentShader;//ƬԪ��ɫ������ű�
	
    private FloatBuffer   mVertexBuffer;//���������ݻ���
    private FloatBuffer   mTextureBuffer;//������ɫ��ݻ���
    int vCount;//��������
    int texId;//����Id
    
    float width;
    float height;
    float length;
	public HuanYingJieMianJuXing(float width,float height)
	{
		this.width=width;
    	this.height=height;
    	//���������ݵĳ�ʼ��================begin============================
        vCount=6;//ÿ��������������Σ�ÿ�������3������        
        float vertices[]=
        {
        		-width/2,0,-height/2,
        		-width/2,0,height/2,
        		width/2,0,height/2,
        		
        		-width/2,0,-height/2,
        		width/2,0,height/2,
        		width/2,0,-height/2
        };
        //�������������ݻ���
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ������ϵͳ˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥��������
        mVertexBuffer.position(0);//���û�������ʼλ��
        float textures[]=
        {
        		0,0,0,1f,1f,1f,
        		0,0,1f,1f,1f,0
        };
        //��������������ݻ���
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
        tbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ������ϵͳ˳��
        mTextureBuffer= tbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mTextureBuffer.put(textures);//�򻺳����з��붥����ɫ���
        mTextureBuffer.position(0);//���û�������ʼλ��
       
	}
    public void intShader(int mProgram)
    {
        this.mProgram=mProgram; 
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж������������������id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
	public void drawSelf(int texId)
	{
		 //�ƶ�ʹ��ĳ��shader����
   	 	GLES20.glUseProgram(mProgram);
        //�����ձ任������shader����
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
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
               mTextureBuffer
        );   
        //���?��λ���������
        GLES20.glEnableVertexAttribArray(maPositionHandle);  
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
        
        //������
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        
        //�����������
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
	}
	
}
