package com.example.game;


import android.content.res.Resources;

public class ShaderManager
{
	final static int shaderCount=5;
	final static String[][] shaderName=
	{
		{"vertex.sh","frag.sh"},
		{"vertex_yingzi.sh","frag_yingzi.sh"},
		{"lightvertex.sh","lightfrag.sh"},
		{"vertex.sh","frag_blackground.sh"},
		{"vertex_net.sh","frag_net.sh"}
	};
	static String[]mVertexShader=new String[shaderCount];
	static String[]mFragmentShader=new String[shaderCount];
	static int[] program=new int[shaderCount];
	
	public static void loadCodeFromFile(Resources r)
	{
		for(int i=0;i<shaderCount;i++)
		{
			//���ض�����ɫ���Ľű�����       
	        mVertexShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][0],r);
	        //����ƬԪ��ɫ���Ľű����� 
	        mFragmentShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][1], r);
		}	
	}
	//������Ҫ�Ǳ���3D�л�ӭ�����е�shader
	public static void compileShader()
	{
		for(int i=0;i<1;i++)
		{
			program[i]=ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
		}
	}
	//����3D�����shader
	public static void compileShaderReal()
	{
		for(int i=1;i<shaderCount;i++)
		{
			program[i]=ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
		}
	}
	//���ﷵ�ص�����ͨ��shader����
	public static int getCommTextureShaderProgram()
	{
		return program[0];
	}
	//���ﷵ�ص���Ӱ�ӵ�shader����
	public static int getShadowshaderProgram()
	{
		return program[1];
	}
	//���ﷵ�ص��ǹ��������shader����
	public static int getLigntAndTexturehaderProgram()
	{
		return program[2];
	}
	//���ﷵ�ص����Ǳ�屳��Ϊ��ɫ��Ȼ������ɫ����ת����͸����shader����
	public static int getBlackgroundShaderProgram()
	{
		return program[3];
	}
	public static int getBasketNetShaderProgram()
	{
		return program[4];
	}
}
