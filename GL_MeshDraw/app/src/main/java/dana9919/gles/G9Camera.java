package dana9919.gles;

import dana9919.gles.base.G9Function;
import android.opengl.Matrix;
/**
 * 摄像机类
 * @author 徐潇
 * mail: dana9919@163.com
 * QQ 61092517
 */
public class G9Camera {
	//------------------------------------------------常数
	//------------------------------------------------元素
		//STATIC TEMP
	protected static float[] _mxTemp= new float[16];
		//四向量 喵的,跟D3D 不同的喔 喵的,还是用_right,
	protected float[]	_right = new float[]{1.0f,0f,0f};
	protected float[]   _up = new float[]{0f,1.0f,0f};
	protected float[]	_invLook = new float[]{0f,0f,1.0f};//喵，NN的
	protected float[]	_pos = new float[]{0f,0f,0f};
			//矩阵
	protected float[] _mxView = new float[16];
	protected float[] _mxProj = new float[16];
	//------------------------------------------------构造
	/**
	 * 设置一个基础摄像机 投影使用透视直角
	 * @param iWinWidth GL窗口宽
	 * @param iWinHeight GL窗口高
	 * @param zf 远平面
	 * @param zn 近平面
	 */
	public G9Camera(int iWinWidth,int iWinHeight,float zf,float zn) {
		// 设置一个基础的mxProj与一个基础的mxView
		
		float fRatio = (float)iWinWidth/iWinHeight;
		RefreshViewMatrix(this);//按类内四分量重新构建一个mxView
		//建mxProj
		Matrix.frustumM(_mxProj, 0, -fRatio, fRatio, -1, 1, zn, zf);
		
	}//ef constructor
	//------------------------------------------------函数
	private static void RefreshViewMatrix(G9Camera pthis) {
		// look不变,
		G9Function.glV3Normalize(pthis._invLook);
		//先算_left
		//G9Function.glV3Cross(pthis._left, pthis._invLook, pthis._up);
		G9Function.glV3Cross(pthis._right, pthis._up, pthis._invLook);
		G9Function.glV3Normalize(pthis._right);
		//再回过来算up
		G9Function.glV3Cross(pthis._up,pthis._invLook, pthis._right);
		G9Function.glV3Normalize(pthis._up);
		//再建mxView
		pthis._mxView[0] = pthis._right[0];pthis._mxView[1] = pthis._up[0]; pthis._mxView[2] = pthis._invLook[0];pthis._mxView[3] = 0;
		pthis._mxView[4] = pthis._right[1];pthis._mxView[5] = pthis._up[1]; pthis._mxView[6] = pthis._invLook[1];pthis._mxView[7] = 0;
		pthis._mxView[8] = pthis._right[2];pthis._mxView[9] = pthis._up[2]; pthis._mxView[10] = pthis._invLook[2];pthis._mxView[11] = 0;
		pthis._mxView[12] = -G9Function.glV3Dot(pthis._right, pthis._pos);
		pthis._mxView[13] = -G9Function.glV3Dot(pthis._up, pthis._pos);
		pthis._mxView[14] = -G9Function.glV3Dot(pthis._invLook, pthis._pos);
		pthis._mxView[15] = 1.0f;
	}//ef
	public void getWorldMatrix(float[] mxWorldOut)//取世界矩阵
	{
		if(mxWorldOut.length !=16)
			throw new RuntimeException("err in Camera::getMxWorld");
		Matrix.invertM(mxWorldOut, 0, _mxView, 0);
		return;
	}//ef
	public void SetDirection(float[] v3Direction){
		for(int i=0; i<3 ; i++){
			_invLook[i] = -v3Direction[i];
		}
		
		G9Function.glV3Normalize(_invLook);
		RefreshViewMatrix(this);
	}//ef
	public void SetPosition(float[] v3Pos){
		G9Function.glFArrayCopy(_pos, v3Pos);
		RefreshViewMatrix(this);
	}//ef
	public void Set_Up(float[] v3Up){
		G9Function.glFArrayCopy(_up, v3Up);
		RefreshViewMatrix(this);
	}//ef
	public void SetLookTarget(float[] v3LookTarget){
		G9Function.glV3Subtract(_invLook,  _pos,v3LookTarget);//_pos到target的向量
		G9Function.glV3Normalize(_invLook);
		RefreshViewMatrix(this);
	}//ef
	public void setRight(float[] v3Right){
		G9Function.glFArrayCopy(_right, v3Right);
		G9Function.glV3Normalize(_right);
		RefreshViewMatrix(this);
	}//ef
	public void SetProjMatrix(float[] mxProjSource){
		G9Function.glFArrayCopy(_mxProj, mxProjSource);
	}//ef
	/*Get 操作中暂时全以参数形式输出，保证类元素的安全
	 * 但有两个重载的方法例外  getViewMatrix getProjMatrix 的返回float[]的重载是传出指针的
	 * 就要求小心使用了，使用不当后果滴可怕
	 * */
	public void GetPosition(float[] v3PosOut){
		if(v3PosOut.length != 3)
			throw new RuntimeException("err in GetPosition");
		for(int i=0; i<3 ; i++){
			v3PosOut[i] = _pos[i];
		}
	}//ef
	public void GetRight(float[] v3RightOut){		
		G9Function.glFArrayCopy(v3RightOut, _right);
	}//ef
	public void GetUp(float[] v3UpOut){
		G9Function.glFArrayCopy(v3UpOut, _up);
	}//ef
	public void GetLook(float[] v3LookOut){
		if(v3LookOut.length!=3)
			throw new RuntimeException("err in camera");
		for(int i=0; i<3; i++)
			v3LookOut[i] = -_invLook[i];		
	}//ef
	public void GetInvLook(float[] v3InvLookOut){
		G9Function.glFArrayCopy(v3InvLookOut, _invLook);
	}//ef
	public void GetViewMatrix(float[] mxViewOut){
		G9Function.glFArrayCopy(mxViewOut, _mxView);
	}
	/**
	 * 这个函数可能存在风险，原因是输出的不是考贝，而是类元素的指针
	 * PS: 注意不要先new 一个mx 再 mx = GetViewMatrix() ，
	 * 		直接声明一个空指针的float[] 然后 运行函数
	 * @return 
	 */
	public float[] GetViewMatrix(){
	
		return _mxView;
	}//ef
	public void GetProjMatrix(float[] mxProjOut){
		G9Function.glFArrayCopy(mxProjOut, _mxProj);
	}//ef
	/**
	 * 这个函数可能存在风险，原因是输出的不是考贝，而是类元素的指针
	 * @return 是FLOAT串的指针 
	 */
	public float[] GetProjMatrix(){
		return _mxProj;
	}//ef
	public void GetWorldMatrix(float[] mxWorldOut){
		//
		if(mxWorldOut.length!=16)
			throw new RuntimeException("err in GetWorldMatrix");
		Matrix.invertM(mxWorldOut, 0, _mxView, 0);
		return;
	}//ef
			//摄像机转动函数 移动函数
	public void strafe(float fOffset)//left<->right
	{
		for(int i=0;i<3;i++){
			_pos[i] += _right[i]*fOffset;
		}		
	}//ef
	public void fly(float fOffset)//up<.>down
	{
		for(int i=0; i<3; i++){
			_pos[i] += _up[i]*fOffset;
		}
	}//ef
	public void walk(float fOffset)//z+<.>z-
	{
		for(int i=0; i<3; i++){
			_pos[i] -= _invLook[i]*fOffset;
		}
	}//ef
	public void pitch(float fAngle)//以right为轴
	{
		//使用类内STATIC 的TEMP量 _mxTemp;省的每次建一个新矩阵而不知道什么时候系统才会去干掉它
		//这里把_mxTemp作为旋转矩阵
		G9Function.glRotateAxis(_mxTemp, fAngle, _right);
		G9Function.glV3Transform(_up, _mxTemp);
		G9Function.glV3Transform(_invLook, _mxTemp);
		
		//刷新视矩阵
		RefreshViewMatrix(this);		
	}//ef
	public void yaw(float fAngle)//up为轴
	{
		G9Function.glRotateAxis(_mxTemp, fAngle, _up);
		G9Function.glV3Transform(_right, _mxTemp);
		G9Function.glV3Transform(_invLook, _mxTemp);
		RefreshViewMatrix(this);
	}//ef
	public void roll(float fAngle)//以look为轴
	{
		G9Function.glRotateAxis(_mxTemp, fAngle, _invLook);
		G9Function.glV3Transform(_up, _mxTemp);
		G9Function.glV3Transform(_right, _mxTemp);
		RefreshViewMatrix(this);
	}//ef
	//------------------------------------------------监听
	//------------------------------------------------内类
	
}//EC
