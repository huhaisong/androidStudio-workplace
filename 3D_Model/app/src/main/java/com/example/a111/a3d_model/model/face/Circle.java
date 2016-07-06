package com.example.a111.a3d_model.model.face;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.example.a111.a3d_model.glsurfaceview.MySurfaceView;
import com.example.a111.a3d_model.util.MatrixState;
import com.example.a111.a3d_model.util.ShaderUtil;

import static com.example.a111.a3d_model.util.ShaderUtil.createProgram;

//Բ��
public class Circle {
    int mProgram;//�Զ�����Ⱦ������ɫ������id
    int muMVPMatrixHandle;//�ܱ任��������
    int maPositionHandle; //����λ����������
    int maTexCoorHandle; //�������������������
    int muMMatrixHandle;

    int maCameraHandle; //�����λ���������� 
    int maNormalHandle; //���㷨������������ 
    int maLightLocationHandle;//��Դλ����������  


    String mVertexShader;//������ɫ������ű�  	 
    String mFragmentShader;//ƬԪ��ɫ������ű�

    FloatBuffer mVertexBuffer;//���������ݻ���
    FloatBuffer mTexCoorBuffer;//�������������ݻ���
    FloatBuffer mNormalBuffer;//���㷨������ݻ���

    int vCount = 0;
    float xAngle = 0;
    float yAngle = 0;
    float zAngle = 0;

    public Circle(MySurfaceView mv, float scale, float r, int n) {
        initVertexData(scale, r, n);
        initShader(mv);
    }

    public void initVertexData(float scale, float r, int n) {
        r = r * scale;
        float angdegSpan = 360.0f / n;
        vCount = 3 * n;

        float[] vertices = new float[vCount * 3];
        float[] textures = new float[vCount * 2];

        int count = 0;
        int stCount = 0;
        for (float angdeg = 0; Math.ceil(angdeg) < 360; angdeg += angdegSpan) {
            double angrad = Math.toRadians(angdeg);
            double angradNext = Math.toRadians(angdeg + angdegSpan);

            vertices[count++] = 0;
            vertices[count++] = 0;
            vertices[count++] = 0;

            textures[stCount++] = 0.5f;
            textures[stCount++] = 0.5f;

            vertices[count++] = (float) (-r * Math.sin(angrad));
            vertices[count++] = (float) (r * Math.cos(angrad));
            vertices[count++] = 0;

            textures[stCount++] = (float) (0.5f - 0.5f * Math.sin(angrad));
            textures[stCount++] = (float) (0.5f - 0.5f * Math.cos(angrad));

            vertices[count++] = (float) (-r * Math.sin(angradNext));
            vertices[count++] = (float) (r * Math.cos(angradNext));
            vertices[count++] = 0;

            textures[stCount++] = (float) (0.5f - 0.5f * Math.sin(angradNext));
            textures[stCount++] = (float) (0.5f - 0.5f * Math.cos(angradNext));
        }
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);//�������������ݻ���
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
        ByteBuffer cbb = ByteBuffer.allocateDirect(textures.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = cbb.asFloatBuffer();
        mTexCoorBuffer.put(textures);
        mTexCoorBuffer.position(0);
    }

    public void initShader(MySurfaceView mv) {
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_tex_light.sh", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_tex_light.sh", mv.getResources());
        mProgram = createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        maCameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");
        maLightLocationHandle = GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
    }

    public void drawSelf(int texId) {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mTexCoorBuffer);
        GLES20.glVertexAttribPointer(maNormalHandle, 4, GLES20.GL_FLOAT, false, 3 * 4, mNormalBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);
        GLES20.glEnableVertexAttribArray(maNormalHandle);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vCount);
    }
}
