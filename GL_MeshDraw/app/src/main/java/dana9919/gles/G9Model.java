package dana9919.gles;

import dana9919.gles.base.G9Function;
import dana9919.gles.base.MeshDrawable;
import dana9919.gles.base.MeshInfo;
import android.opengl.Matrix;
/**
 * 空间模型类
 * @author 徐潇
 * mail: dana9919@163.com
 * QQ 61092517
 */
public class G9Model {
	protected MeshDrawable _mesh = null;//模型类内包含的网格体
	protected float[] _mxWorld = new float[16];
	/**
	 * 基础的INIT
	 */
	void defultInit(){
		Matrix.setIdentityM(_mxWorld, 0);
	}//ef
	public G9Model(MeshDrawable mesh) {
		// 
		_mesh = mesh;
		defultInit();
	}//ef constructor
	public MeshInfo GetMeshInfo(){
		//仅包装传出MESH的 vertinfo
		return _mesh.GetMeshInfo();
	}//ef
	/**
	 * 返回指针,小心使用
	 * @return
	 */
	public float[] GetWorldMatrix(){
		return _mxWorld;
	}//ef
	/******以下为测试专用 **********/
	float _fAngle=0.0f;
	public void TestMen(){
		//累计按轴轮动
		float[] mxRotate = new float[16];
		G9Function.glRotateAxis(mxRotate,1,new float[]{1,0,0});
		Matrix.multiplyMM(_mxWorld, 0, _mxWorld, 0, mxRotate, 0);
		
	}//ef
	public void TestMen2(float fXAngle, float fYAngle){
		float[] mxRotateX = new float[16];
		float[] mxRotateY = new float[16];
		float[] mxR  = new float[16];
		G9Function.glRotateAxis(mxRotateX, fXAngle, new float[]{1,0,0});
		G9Function.glRotateAxis(mxRotateY, fXAngle, new float[]{0,1,0});
		Matrix.multiplyMM(mxR, 0, mxRotateX, 0, mxRotateY, 0);
		Matrix.multiplyMM(_mxWorld,0,_mxWorld,0,mxR,0);
		//fXAngle = 0;
		//fYAngle = 0;
		
	}//ef
	
}//EC
