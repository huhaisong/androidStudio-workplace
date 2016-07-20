package dana9919.gles;


import java.util.ArrayList;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import dana9919.gles.base.G9ENUM;
import dana9919.gles.base.G9GameView;
import dana9919.gles.base.MeshInfo;
import dana9919.gles.effect.Effect_g9phong;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

/**
 * 渲染器 是本框架里重要的一环
 * @author 徐潇
 * mail: dana9919@163.com
 * QQ 61092517
 * 2012-8-26 暂时只有一个 phong效果的着色器,边学边做继续深化
 */
 
public class G9Render implements GLSurfaceView.Renderer{
	//------------------------------------------------常数
	//------------------------------------------------元素
	protected Effect_g9phong _eff_g9phong = null;//初始时必须为空，在建面后给值
	
	protected ModelCollection _mc;//模型队列指针
	G9Camera _vCamera;//视摄像机指针
	G9Camera _lCamera;//光摄像机指针
	G9GameView _sv;//场景主控类 
	
	//------------------------------------------------构造
	public G9Render(G9GameView sv) {	
		_sv = sv;
	}
	//------------------------------------------------函数
	protected void InitShader(){
		_eff_g9phong = new Effect_g9phong(_sv.getResources());
				
			
	}//ef
	public void SetModeCollection(ModelCollection mc){
		_mc = mc;
	}//ef
	public void SetCameras(G9Camera viewCamera,G9Camera  lightCamera){
		_vCamera = viewCamera;
		_lCamera = lightCamera;
	}//ef
	@Override
	public void onDrawFrame(GL10 gl) {
		
		//清屏
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);
		// 绘制所有_mc里包含的模型
		ArrayList<G9Model> arrModel = _mc.GetMoelList();
		int iSize = arrModel.size();
		for(int i=0; i<iSize; i++){
			G9Model model = arrModel.get(i);
			MeshInfo meshinfo = model.GetMeshInfo();			
			switch (meshinfo.enumRENDER_TECH) {
			case G9ENUM.RENDER_TECH_ERROR:
				
				break;
			case G9ENUM.RENDER_TECH_G9PHONG:
				_eff_g9phong.DrawModel(_vCamera, _lCamera, model);
				break;
			
			default:
				break;
			}//end switch			
		}//end for		
	}//ef
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		//设置视口
		GLES20.glViewport(0, 0, width, height);		
		//逻辑上的扭曲啊喵,不是我造成的
		_sv.InitCamera(width, height);			
	}//ef

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearColor(0, 0, 0, 1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		//开背裁
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		//GLES20.glFrontFace(GLES20.GL_CW);
		//逼的没办法了 在这里调用初始着色器与模型网格(纹理加载必须在glSuface建立后)
		InitShader();
		_sv.InitModel();
		
		
	}//ef
	//------------------------------------------------监听
	//------------------------------------------------内类

	

}//EC
