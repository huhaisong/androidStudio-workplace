package com.example.a111.a3d_model.model.line;

import static com.example.a111.a3d_model.util.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.example.a111.a3d_model.glsurfaceview.MySurfaceView;
import com.example.a111.a3d_model.util.MatrixState;
import com.example.a111.a3d_model.util.ShaderUtil;

public class CircleL {
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

    FloatBuffer mVertexBuffer;//���������ݻ���
    FloatBuffer mColorBuffer;    //������ɫ��ݻ���
    FloatBuffer mNormalBuffer;//���㷨������ݻ���
    int vCount = 0;
    float xAngle = 0;
    float yAngle = 0;
    float zAngle = 0;

    public CircleL(MySurfaceView mv, float scale, float r, int n) {
        initVertexData(scale, r, n);
        initShader(mv);
    }

    public void initVertexData(float scale, float r, int n) {
        r = r * scale;
        float angdegSpan = 360.0f / n;
        vCount = 3 * n;

        float[] vertices = new float[vCount * 3];
        float[] colors = new float[vCount * 4];
        int count = 0;
        int colorCount = 0;
        for (float angdeg = 0; Math.ceil(angdeg) < 360; angdeg += angdegSpan) {
            double angrad = Math.toRadians(angdeg);
            double angradNext = Math.toRadians(angdeg + angdegSpan);
            vertices[count++] = 0;
            vertices[count++] = 0;
            vertices[count++] = 0;

            colors[colorCount++] = 1;
            colors[colorCount++] = 1;
            colors[colorCount++] = 1;
            colors[colorCount++] = 1;
            vertices[count++] = (float) (-r * Math.sin(angrad));
            vertices[count++] = (float) (r * Math.cos(angrad));
            vertices[count++] = 0;


            colors[colorCount++] = 1;
            colors[colorCount++] = 1;
            colors[colorCount++] = 1;
            colors[colorCount++] = 1;

            vertices[count++] = (float) (-r * Math.sin(angradNext));
            vertices[count++] = (float) (r * Math.cos(angradNext));
            vertices[count++] = 0;


            colors[colorCount++] = 1;
            colors[colorCount++] = 1;
            colors[colorCount++] = 1;
            colors[colorCount++] = 1;
        }
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        float[] normals = new float[vertices.length];
        for (int i = 0; i < normals.length; i += 3) {
            normals[i] = 0;
            normals[i + 1] = 0;
            normals[i + 2] = 1;
        }
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * 4);
        nbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = nbb.asFloatBuffer();
        mNormalBuffer.put(normals);
        mNormalBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);
    }

    public void initShader(MySurfaceView mv) {
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_color_light.sh", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_color_light.sh", mv.getResources());
        mProgram = createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        maCameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");
        maLightLocationHandle = GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, mColorBuffer);
        GLES20.glVertexAttribPointer(maNormalHandle, 4, GLES20.GL_FLOAT, false, 3 * 4, mNormalBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maColorHandle);
        GLES20.glEnableVertexAttribArray(maNormalHandle);
        GLES20.glLineWidth(2);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vCount);
    }
}
