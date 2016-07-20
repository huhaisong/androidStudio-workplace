package com.example.game;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.content.res.Resources;
import android.opengl.GLES20;
//����,������
public class LanBan  
{
	int mProgram;//�Զ�����Ⱦ������ɫ������id 
    int muMVPMatrixHandle;//�ܱ任��������   
    int muMMatrixHandle;//λ�á���ת�任����
    int maCameraHandle; //�����λ����������  
    int maPositionHandle; //����λ����������  
    int maNormalHandle; //���㷨������������  
    int maTexCoorHandle; //�������������������  
    int maSunLightLocationHandle;//��Դλ����������  
    
    public FloatBuffer mVertexBuffer;
	public FloatBuffer mTexCoorBuffer;
    public FloatBuffer   mNormalBuffer;//���㷨������ݻ���
	int vCount; 
	public LanBan(float length,float width,float height,Resources r){
		float l=length/2;
		float w=width/2;
		float h=height/2;
		
		
		vCount=36;
		float[] vertexs=new float[]
		{	
			0-w,0-h,length-l,	
			0-w,0-h,0-l,
			0-w,height-h,0-l,
			
			0-w,0-h,length-l,
			0-w,height-h,0-l,
			0-w,height-h,length-l,
			
			width-w,0-h,0-l,
			0-w,0-h,0-l,
			0-w,height-h,0-l,
			
			width-w,0-h,0-l,
			0-w,height-h,0-l,
			width-w,height-h,0-l,
			
			width-w,height-h,length-l,
			width-w,height-h,0-l,
			width-w,0-h,0-l,
			
			width-w,height-h,length-l,
			width-w,0-h,0-l,
			width-w,0-h,length-l,
			
			width-w,height-h,0-l,
			0-w,height-h,0-l,
			0-w,height-h,length-l,
			
			width-w,height-h,0-l,
			0-w,height-h,length-l,
			width-w,height-h,length-l,
			
			width-w,height-h,length-l,
			0-w,height-h,length-l,
			0-w,0-h,length-l,
			
			width-w,height-h,length-l,
			0-w,0-h,length-l,
			width-w,0-h,length-l,
			
			width-w,0-h,length-l,
			0-w,0-h,length-l,
			0-w,0-h,0-l,
			
			width-w,0-h,length-l,
			0-w,0-h,0-l,
			width-w,0-h,0-l,
		};
		ByteBuffer vbb=ByteBuffer.allocateDirect(vertexs.length*4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer=vbb.asFloatBuffer();
		mVertexBuffer.put(vertexs);
		mVertexBuffer.position(0);
		float[] textures=new float[] 
		{						
	            1,1,1,0,0,0,
	            1,1,0,0,0,1,
	            0,1,1,1,1,0,
	            0,1,1,0,0,0,
	            1,1,1,0,0,0,
	            1,1,0,0,0,1,
	            1,0,0,0,0,1,
	            1,0,0,1,1,1,
	            
	            1,0,0,0,0,1,
	            1,0,0,1,1,1,
	            
	            0,1,1,1,1,0,
	            0,1,1,0,0,0
	           
		};  
		ByteBuffer tbb=ByteBuffer.allocateDirect(textures.length*4);
		tbb.order(ByteOrder.nativeOrder());
		mTexCoorBuffer=tbb.asFloatBuffer();
		mTexCoorBuffer.put(textures);
		mTexCoorBuffer.position(0);
		
		 float norma[]={//���㷨��������
	        		0,1,0, 0,1,0, 0,1,0,
	        		0,1,0, 0,1,0, 0,1,0,
	        };
	        ByteBuffer tnom=ByteBuffer.allocateDirect(norma.length*4);
	        tnom.order(ByteOrder.nativeOrder());//�����ֽ�˳��
	        mNormalBuffer=tnom.asFloatBuffer();//ת��ΪFloat�ͻ���
	        mNormalBuffer.put(norma);//�򻺳�����Ӷ��㷨�������
	        mNormalBuffer.position(0);//���û��������ʼλ��
	}
	 //��ʼ����ɫ����ݵ�initShader����
	 public void initShader(int mProgram)
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
               mTexCoorBuffer
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
