package com.example.a3d_model.model;

import static com.example.a3d_model.util.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES20;

import com.example.a3d_model.util.ShaderUtil;
import com.example.a3d_model.util.VectorUtil;
import com.example.a3d_model.glsurfaceview.MySurfaceView;
import com.example.a3d_model.util.MatrixState;

/*
 * ���ʮ����
 * ����������ഹֱ�Ļƽ𳤷���
 */
public class Regular20L 
{	
	int mProgram;//�Զ�����Ⱦ������ɫ������id
    int muMVPMatrixHandle;//�ܱ任��������
    int maPositionHandle; //����λ����������
    int maColorHandle; //������ɫ�������� 
    int muMMatrixHandle;
    
    int maCameraHandle; //�����λ����������
    int maNormalHandle; //���㷨������������
    int maLightLocationHandle;//��Դλ���������� 
    
    
    String mVertexShader;//������ɫ��    	 
    String mFragmentShader;//ƬԪ��ɫ��
	
	FloatBuffer   mVertexBuffer;//���������ݻ���
	FloatBuffer   mColorBuffer;	//������ɫ��ݻ���
	FloatBuffer   mNormalBuffer;//���㷨������ݻ���
    int vCount=0;   
    public float xAngle=0;//��x����ת�ĽǶ�
    public float yAngle=0;//��y����ת�ĽǶ�
    public float zAngle=0;//��z����ת�ĽǶ�
    
    float bHalf=0;//�ƽ𳤷��εĿ�
    float r=0;//��İ뾶
    
    public Regular20L(MySurfaceView mv, float scale, float aHalf, int n)
    {
    	//���ó�ʼ��������ݵ�initVertexData����
    	initVertexData(scale,aHalf,n);
    	//���ó�ʼ����ɫ����intShader����
    	initShader(mv);
    }
    
    //�Զ���ĳ�ʼ��������ݵķ���
    public void initVertexData(float scale, float aHalf, int n) //��С���ƽ𳤷��εĳ���һ�룬�ֶ���
	{
		aHalf*=scale;
		//��ʼ����Ա����
		bHalf=aHalf*0.618034f;
		r=(float) Math.sqrt(aHalf*aHalf+bHalf*bHalf);
		vCount=3*20*n*n;//���������20������Σ�ÿ������ζ����������
		//��20���������ݳ�ʼ��
		ArrayList<Float> alVertix20=new ArrayList<Float>();//��20����Ķ����б?δ���ƣ�
		ArrayList<Integer> alFaceIndex20=new ArrayList<Integer>();//��20������֯����Ķ��������ֵ�б?����ʱ����ƣ�
		//��20���嶥��
		alVertix20.add(0f); alVertix20.add(aHalf); alVertix20.add(-bHalf);//������׶����
		
		alVertix20.add(0f); alVertix20.add(aHalf); alVertix20.add(bHalf);//�����ϵĵ�
		alVertix20.add(aHalf); alVertix20.add(bHalf); alVertix20.add(0f);
		alVertix20.add(bHalf); alVertix20.add(0f); alVertix20.add(-aHalf);
		alVertix20.add(-bHalf); alVertix20.add(0f); alVertix20.add(-aHalf);
		alVertix20.add(-aHalf); alVertix20.add(bHalf); alVertix20.add(0f);
		
		alVertix20.add(-bHalf); alVertix20.add(0f); alVertix20.add(aHalf);
		alVertix20.add(bHalf); alVertix20.add(0f); alVertix20.add(aHalf);
		alVertix20.add(aHalf); alVertix20.add(-bHalf); alVertix20.add(0f);
		alVertix20.add(0f); alVertix20.add(-aHalf); alVertix20.add(-bHalf);
		alVertix20.add(-aHalf); alVertix20.add(-bHalf); alVertix20.add(0f);
		
		alVertix20.add(0f); alVertix20.add(-aHalf); alVertix20.add(bHalf);//����׶����
		//��20��������
		alFaceIndex20.add(0); alFaceIndex20.add(1); alFaceIndex20.add(2);
		alFaceIndex20.add(0); alFaceIndex20.add(2); alFaceIndex20.add(3);
		alFaceIndex20.add(0); alFaceIndex20.add(3); alFaceIndex20.add(4);
		alFaceIndex20.add(0); alFaceIndex20.add(4); alFaceIndex20.add(5);
		alFaceIndex20.add(0); alFaceIndex20.add(5); alFaceIndex20.add(1);
		
		alFaceIndex20.add(1); alFaceIndex20.add(6); alFaceIndex20.add(7);
		alFaceIndex20.add(1); alFaceIndex20.add(7); alFaceIndex20.add(2);
		alFaceIndex20.add(2); alFaceIndex20.add(7); alFaceIndex20.add(8);
		alFaceIndex20.add(2); alFaceIndex20.add(8); alFaceIndex20.add(3);
		alFaceIndex20.add(3); alFaceIndex20.add(8); alFaceIndex20.add(9);
		alFaceIndex20.add(3); alFaceIndex20.add(9); alFaceIndex20.add(4);
		alFaceIndex20.add(4); alFaceIndex20.add(9); alFaceIndex20.add(10);
		alFaceIndex20.add(4); alFaceIndex20.add(10); alFaceIndex20.add(5);
		alFaceIndex20.add(5); alFaceIndex20.add(10); alFaceIndex20.add(6);
		alFaceIndex20.add(5); alFaceIndex20.add(6); alFaceIndex20.add(1);
		
		alFaceIndex20.add(6); alFaceIndex20.add(11); alFaceIndex20.add(7);
		alFaceIndex20.add(7); alFaceIndex20.add(11); alFaceIndex20.add(8);
		alFaceIndex20.add(8); alFaceIndex20.add(11); alFaceIndex20.add(9);
		alFaceIndex20.add(9); alFaceIndex20.add(11); alFaceIndex20.add(10);
		alFaceIndex20.add(10); alFaceIndex20.add(11); alFaceIndex20.add(6);
		//������ƶ���
		float[] vertices20= VectorUtil.cullVertex(alVertix20, alFaceIndex20);//ֻ���㶥��

		//�����ݳ�ʼ��
		ArrayList<Float> alVertix=new ArrayList<Float>();//ԭ�����б?δ���ƣ�
		ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();//��֯����Ķ��������ֵ�б?����ʱ����ƣ�
		int vnCount=0;//ǰi-1��ǰ���ж�����ĺ�
		for(int k=0;k<vertices20.length;k+=9)//����20����ÿ���������ѭ��
		{
			float [] v1=new float[]{vertices20[k+0], vertices20[k+1], vertices20[k+2]};
			float [] v2=new float[]{vertices20[k+3], vertices20[k+4], vertices20[k+5]};
			float [] v3=new float[]{vertices20[k+6], vertices20[k+7], vertices20[k+8]};
			//����
			for(int i=0;i<=n;i++)
			{
				float[] viStart=VectorUtil.devideBall(r, v1, v2, n, i);
				float[] viEnd=VectorUtil.devideBall(r, v1, v3, n, i);
				for(int j=0;j<=i;j++)
				{
					float[] vi=VectorUtil.devideBall(r, viStart, viEnd, i, j);
					alVertix.add(vi[0]); alVertix.add(vi[1]); alVertix.add(vi[2]);
				}
			}
			//����
			for(int i=0;i<n;i++)
			{
				if(i==0){//���ǵ�0�У�ֱ�Ӽ�����ƺ󶥵�����012
					alFaceIndex.add(vnCount+0); alFaceIndex.add(vnCount+1);alFaceIndex.add(vnCount+2);
					vnCount+=1;
					if(i==n-1){//�����ÿ��������ε����һ��ѭ��������һ�еĶ������Ҳ����
						vnCount+=2;
					}
					continue;
				}
				int iStart=vnCount;//��i�п�ʼ������
				int viCount=i+1;//��i�ж�����
				int iEnd=iStart+viCount-1;//��i�н�������
				
				int iStartNext=iStart+viCount;//��i+1�п�ʼ������
				int viCountNext=viCount+1;//��i+1�ж�����
				int iEndNext=iStartNext+viCountNext-1;//��i+1�н��������
				//ǰ����ı���
				for(int j=0;j<viCount-1;j++)
				{
					int index0=iStart+j;//�ı��ε��ĸ���������
					int index1=index0+1;
					int index2=iStartNext+j;
					int index3=index2+1;
					alFaceIndex.add(index0); alFaceIndex.add(index2);alFaceIndex.add(index3);//����ǰ����ı���
					alFaceIndex.add(index0); alFaceIndex.add(index3);alFaceIndex.add(index1);				
				}// j
				alFaceIndex.add(iEnd); alFaceIndex.add(iEndNext-1);alFaceIndex.add(iEndNext); //���һ�������
				vnCount+=viCount;//��i��ǰ���ж�����ĺ�
				if(i==n-1){//�����ÿ��������ε����һ��ѭ��������һ�еĶ������Ҳ����
					vnCount+=viCountNext;
				}
			}// i
		}
		
		//������ƶ���
		float[] vertices=VectorUtil.cullVertex(alVertix, alFaceIndex);//ֻ���㶥��
		float[] normals=vertices;//������Ƿ�����
		
		//���������ݳ�ʼ��
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//�������������ݻ���
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥��������
        mVertexBuffer.position(0);//���û�������ʼλ��
        //��������ݳ�ʼ��  
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);//�������㷨������ݻ���
        nbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mNormalBuffer = nbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mNormalBuffer.put(normals);//�򻺳����з��붥�㷨�������
        mNormalBuffer.position(0);//���û�������ʼλ��
        
		float[] colors=new float[vCount*4];//������ɫ����
		int Count=0;
		for(int i=0;i<vCount;i++)
		{
			colors[Count++]=1;	//r
			colors[Count++]=1;	//g
			colors[Count++]=1;	//b
			colors[Count++]=1;	//a
			
		}
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
    	
   	 	MatrixState.rotate(xAngle, 1, 0, 0);
   	 	MatrixState.rotate(yAngle, 0, 1, 0);
   	 	MatrixState.rotate(zAngle, 0, 0, 1);
    	
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
         //���Ͷ�����ɫ���
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
         GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vCount); 
    }
}
