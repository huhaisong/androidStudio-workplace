package dana9919.gles.effect;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import dana9919.gles.G9Camera;
import dana9919.gles.G9Model;
import dana9919.gles.base.MeshInfo;
import dana9919.gles.base.ShaderUtil;
import dana9919.gles.base.SubInfo;
/**
 * 作用: 渲染器配套的着色器应用类 本类实现经典高洛德着色 
 * PS:必须把配套的两个着色器文本放在系统assets里面 文件名为: g9phong.vs g9phong.ps
 * @author 徐潇
 * mail: dana9919@163.com
 * QQ: 61092517
 */
public class Effect_g9phong {
	//主EFFECT PROGRAM
	public int hProgram;
	//UNIFORM
	public int hmxWVP;
	public int hmxView;
	public int hmxWorldView;
	public int hv3LightDir;
	public int hspDiffuse;//采样器
	//ATTRIBUTE
	public int hv3Pos;
	public int hv3Normal;
	public int hv2UV;
	//CONSTRUCTOR
	public Effect_g9phong(Resources resource) {
		// NEW 的时候自动全赋值
		String textVS = ShaderUtil.loadFromAssetsFile("g9phong.vs", resource);
		String textPS = ShaderUtil.loadFromAssetsFile("g9phong.ps", resource);	
		//创建effect句柄
		hProgram = ShaderUtil.createProgram(textVS, textPS);
		//uniform
		hmxWVP = GLES20.glGetUniformLocation(hProgram, "g_mxWVP");
		hmxView = GLES20.glGetUniformLocation(hProgram, "g_mxView");		
		hmxWorldView=GLES20.glGetUniformLocation(hProgram, "g_mxWorldView");
		hv3LightDir = GLES20.glGetUniformLocation(hProgram, "g_v3LightDir");
		hspDiffuse = GLES20.glGetUniformLocation(hProgram, "spDiffuse");
		//attribute
		hv3Pos = GLES20.glGetAttribLocation(hProgram, "v3Pos");
		hv3Normal = GLES20.glGetAttribLocation(hProgram, "v3Normal");
		hv2UV  = GLES20.glGetAttribLocation(hProgram, "v2UV");		
	}//ef constructor
	/**
	 * 用此effect来绘制指定模型
	 * @param vcamera 视摄像机
	 * @param lcamera 光摄像机
	 * @param model 要绘制的模型
	 */
	public void DrawModel(G9Camera vcamera,G9Camera lcamera,G9Model model){
		//在Effect类里面渲染 好像有点与理不合，不过确实方便不少
		//空值不检测了 使用时小心		
		GLES20.glUseProgram(hProgram);
		MeshInfo meshinfo = model.GetMeshInfo();
		//计算矩阵 
		float[] mxWVP = new float[16];
		float[] mxWorldView = new float[16];
		float[] mxView = new float[16];
		
		vcamera.GetViewMatrix(mxView);		
		Matrix.multiplyMM(mxWorldView, 0, mxView, 0, model.GetWorldMatrix(), 0);
		Matrix.multiplyMM(mxWVP, 0, vcamera.GetProjMatrix(), 0, mxWorldView, 0);
		
		//传矩阵
		GLES20.glUniformMatrix4fv(hmxWVP, 1, false, mxWVP, 0);
		GLES20.glUniformMatrix4fv(hmxView, 1, false, mxView, 0);
		GLES20.glUniformMatrix4fv(hmxWorldView, 1, false, mxWorldView, 0);
		//传光向量
		float[] v3LightDir = new float[3];
		lcamera.GetLook(v3LightDir);
		GLES20.glUniform3fv(hv3LightDir, 1, v3LightDir, 0);
		//分SUBSET 传顶点及纹理 并 draw\
		SubInfo curSub = null;
		for(int i=0; i<meshinfo.iSubsets; i++)		
		{
			curSub = meshinfo.GetSubset(i);
			//顶点操作
			GLES20.glVertexAttribPointer(hv3Pos, 
					3, GLES20.GL_FLOAT,false,
					curSub.posInfo._iStride, 
					curSub.posInfo._fbuf);
			GLES20.glVertexAttribPointer(hv3Normal,
					3,GLES20.GL_FLOAT,false,
					curSub.normalInfo._iStride,
					curSub.normalInfo._fbuf);
			GLES20.glVertexAttribPointer(hv2UV,
					2,GLES20.GL_FLOAT,false,
					curSub.uvInfo._iStride,
					curSub.uvInfo._fbuf);
			GLES20.glEnableVertexAttribArray(hv3Pos);
			GLES20.glEnableVertexAttribArray(hv3Normal);
			GLES20.glEnableVertexAttribArray(hv2UV);
			//TEXTURE操作//又中了次枪
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, curSub.idDTex);//再次希望WP8超过ANDROID
			GLES20.glUniform1i(hspDiffuse, 0);
			
			
			//绘制当前 subset
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, curSub.iSubVertCount);			
		}//end for		
	}//ef
}
