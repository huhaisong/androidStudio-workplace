package zengshi.vr_launcher.test;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;

//���غ�����塪��Я�����Ϣ���Զ�������ƽ������
public class LoadedObjectVertexNormalAverage extends TouchableObject
{	
	int mProgram;//�Զ�����Ⱦ������ɫ������id  
    int muMVPMatrixHandle;//�ܱ任��������
    int muMMatrixHandle;//λ�á���ת�任����
    int maPositionHandle; //����λ����������  
    int maNormalHandle; //���㷨������������  
    int maLightLocationHandle;//��Դλ����������  
    int maCameraHandle; //�����λ���������� 
    int muProjCameraMatrixHandle;//ͶӰ���������Ͼ�������
    int muColorHandle;//������ɫ
    
    String mVertexShader;//������ɫ������ű�    	 
    String mFragmentShader;//ƬԪ��ɫ������ű�    
	
	FloatBuffer   mVertexBuffer;//���������ݻ���  
	FloatBuffer   mNormalBuffer;//���㷨������ݻ���
    int vCount=0;  
    
   
    public LoadedObjectVertexNormalAverage(MySurfaceView mv,float[] vertices,float[] normals)
    {    	
    	//��ʼ�������������ɫ���
    	initVertexData(vertices,normals);
    	//��ʼ��shader        
    	initShader(mv);
    	//��ʼ����Χ��
    	preBox = new AABB3(vertices);
    }
    
    //��ʼ�������������ɫ��ݵķ���
    public void initVertexData(float[] vertices,float[] normals)
    {
    	//���������ݵĳ�ʼ��================begin============================
    	vCount=vertices.length/3;   
		
        //�������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥��������
        mVertexBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ��ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //���������ݵĳ�ʼ��================end============================
        
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

    //��ʼ��shader
    public void initShader(MySurfaceView mv)
    {
    	//���ض�����ɫ���Ľű�����
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //����ƬԪ��ɫ���Ľű�����
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());  
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж���λ����������  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж�����ɫ��������  
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal");
        //��ȡ�������ܱ任��������
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
        //��ȡλ�á���ת�任��������
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix"); 
        //��ȡ�����й�Դλ������
        maLightLocationHandle=GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //��ȡ�����������λ������
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera");  
        //��ȡ������ͶӰ���������Ͼ�������
        muProjCameraMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMProjCameraMatrix");
        muColorHandle=GLES20.glGetUniformLocation(mProgram, "aColor"); 
    }
    
    public void drawSelf()
    { 
    	 copyM();//���Ʊ任����
    	 
    	 //�ƶ�ʹ��ĳ����ɫ������
    	 GLES20.glUseProgram(mProgram);
         //�����ձ任��������ɫ������
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
         //��λ�á���ת�任��������ɫ������
         GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);   
         //����Դλ�ô�����ɫ������   
         GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
         //�������λ�ô�����ɫ������   
         GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
         //��ͶӰ���������Ͼ�������ɫ������
         GLES20.glUniformMatrix4fv(muProjCameraMatrixHandle, 1, false, MatrixState.getViewProjMatrix(), 0);     
         //������λ����ݴ�����Ⱦ����
         GLES20.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //���붥����ɫ���
         GLES20.glUniform4fv(muColorHandle, 1, color, 0);
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
         //���ö���λ�á����������
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maNormalHandle);  
         //���Ƽ��ص�����
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
    }
}
