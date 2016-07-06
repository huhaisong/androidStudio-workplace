package com.example.a111.a3d_model.model.line;
import static com.example.a111.a3d_model.util.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;

import com.example.a111.a3d_model.glsurfaceview.MySurfaceView;
import com.example.a111.a3d_model.util.MatrixState;
import com.example.a111.a3d_model.util.ShaderUtil;

//Բ�����Ǽ���
public class CylinderSideL 
{	
	int mProgram;//�Զ�����Ⱦ������ɫ������id
    int muMVPMatrixHandle;//�ܱ任��������
    int maPositionHandle; //����λ���������� 
    int maColorHandle; //������ɫ�������� 
    
    int muMMatrixHandle;//λ�á���ת�����ű任����
    int maCameraHandle; //�����λ����������
    int maNormalHandle; //���㷨������������
    int maLightLocationHandle;//��Դλ���������� 
    
    
    String mVertexShader;//������ɫ������ű�   	 
    String mFragmentShader;//ƬԪ��ɫ������ű�
	
	FloatBuffer   mVertexBuffer;//���������ݻ���
	FloatBuffer   mColorBuffer;	//������ɫ��ݻ���
	FloatBuffer   mNormalBuffer;//���㷨������ݻ���
    int vCount=0;   
    float xAngle=0;//��x����ת�ĽǶ�
    float yAngle=0;//��y����ת�ĽǶ�
    float zAngle=0;//��z����ת�ĽǶ�
    
    public CylinderSideL(MySurfaceView mv, float scale, float r, float h, int n)
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
		float[] colors=new float[vCount*4];//������ɫֵ����
		//�����ݳ�ʼ��
		int count=0;
		int colorCount=0;
		for(float angdeg=0;Math.ceil(angdeg)<360;angdeg+=angdegSpan)//����
		{
			double angrad=Math.toRadians(angdeg);//��ǰ����
			double angradNext=Math.toRadians(angdeg+angdegSpan);//��һ����
			//��Բ��ǰ��---0
			vertices[count++]=(float) (-r*Math.sin(angrad));
			vertices[count++]=0;
			vertices[count++]=(float) (-r*Math.cos(angrad));
			
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			//��Բ��һ��---3
			vertices[count++]=(float) (-r*Math.sin(angradNext));
			vertices[count++]=h;
			vertices[count++]=(float) (-r*Math.cos(angradNext));
			
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			//��Բ��ǰ��---2
			vertices[count++]=(float) (-r*Math.sin(angrad));
			vertices[count++]=h;
			vertices[count++]=(float) (-r*Math.cos(angrad));
			
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			
			//��Բ��ǰ��---0
			vertices[count++]=(float) (-r*Math.sin(angrad));
			vertices[count++]=0;
			vertices[count++]=(float) (-r*Math.cos(angrad));
			
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			//��Բ��һ��---1
			vertices[count++]=(float) (-r*Math.sin(angradNext));
			vertices[count++]=0;
			vertices[count++]=(float) (-r*Math.cos(angradNext));
			
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			//��Բ��һ��---3
			vertices[count++]=(float) (-r*Math.sin(angradNext));
			vertices[count++]=h;
			vertices[count++]=(float) (-r*Math.cos(angradNext));
			
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			colors[colorCount++]=1;
			colors[colorCount++]=1;
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//�������������ݻ���
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥��������
        mVertexBuffer.position(0);//���û�������ʼλ��
        //��������ݳ�ʼ��        
        for(int i=0;i<vertices.length;i++){
        	if(i%3==1){
        		vertices[i]=0;
        	}
        }
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length*4);//�������㷨������ݻ���
        nbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mNormalBuffer = nbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mNormalBuffer.put(vertices);//�򻺳����з��붥�㷨�������
        mNormalBuffer.position(0);//���û�������ʼλ��
        
        //����������ɫ��ݻ���
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mColorBuffer = cbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mColorBuffer.put(colors);//�򻺳����з��붥����ɫ���
        mColorBuffer.position(0);//���û�������ʼλ��
    }

    //��ʼ����ɫ��
    public void initShader(MySurfaceView mv)
    {
    	//���ض�����ɫ���Ľű�����
        mVertexShader= ShaderUtil.loadFromAssetsFile("vertex_color_light.sh", mv.getResources());
        //����ƬԪ��ɫ���Ľű�����
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_color_light.sh", mv.getResources());  
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж�����ɫ��������id  
        maColorHandle= GLES20.glGetAttribLocation(mProgram, "aColor");
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
    
    public void drawSelf()
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
         //���Ͷ���������
         GLES20.glVertexAttribPointer  
         (
        		maColorHandle, 
         		4, 
         		GLES20.GL_FLOAT, 
         		false,
                4*4,   
                mColorBuffer
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
         //���ö�����ɫ���
         GLES20.glEnableVertexAttribArray(maColorHandle);  
         //���ö��㷨�������
         GLES20.glEnableVertexAttribArray(maNormalHandle);
         //���������Ĵ�ϸ
         GLES20.glLineWidth(2);
         //����
         GLES20.glDrawArrays(GLES20.GL_LINES, 0, vCount); 
    }
}
