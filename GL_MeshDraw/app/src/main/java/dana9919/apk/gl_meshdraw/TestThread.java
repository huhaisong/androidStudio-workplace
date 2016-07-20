package dana9919.apk.gl_meshdraw;


import android.opengl.Matrix;
import dana9919.gles.G9Camera;
import dana9919.gles.base.G9Function;

/** 
 * @author 徐潇
 * mail: dana9919@163.com
 * QQ:61092517
 *  * 控制摄像机以原点为中心转动
 */
public class TestThread extends Thread{	
	static float[] _mxTempRX = new float[16];
	static float[] _mxTempRY = new float[16];
	static float[] _mxTemp = new float[16];//合屏旋转矩阵
	
	protected  G9Camera _vcemera = null;
	public boolean bFlag = true;
	public float fXRotate = 0;//屏上X轴的累加移动量 即3维空间按Y轴旋转的弧度
	public float fYRotate = 0;//屏上Y轴的累加移动量 即3维空间按X轴旋转的弧度
	/**
	 * 构造时把摄像机传入
	 */	
	public TestThread(G9Camera vCamera) {
		_vcemera = vCamera;
	}//ef
	
	@Override
	public void run() {
		while (bFlag) {
			//取手指移动的距离，算成 foffX foffY 然后
			try {
				sleep(33);
			} catch (InterruptedException e) {
				// 
				e.printStackTrace();
			}

			synchronized (_vcemera) {
				float[] v3UP = new float[3];_vcemera.GetUp(v3UP);
				float[] v3Right = new float[3];_vcemera.GetRight(v3Right);
				G9Function.glRotateAxis(_mxTempRX, fXRotate, v3UP);
				G9Function.glRotateAxis(_mxTempRY, fYRotate, v3Right);
				Matrix.multiplyMM(_mxTemp, 0,_mxTempRX,0,_mxTempRY,0);
				
				float[] v3Pos =new float[3]; 
				_vcemera.GetPosition(v3Pos);			
				G9Function.glV3Transform(v3Pos, _mxTemp);
				_vcemera.SetPosition(v3Pos);
				_vcemera.SetLookTarget(new float[]{0,0,0});	
			}			
		}//end while
		//super.run();
	}//ef
}//EC
