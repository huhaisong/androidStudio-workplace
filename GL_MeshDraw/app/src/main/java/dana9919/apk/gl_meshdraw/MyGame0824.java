package dana9919.apk.gl_meshdraw;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import dana9919.gles.G9Camera;
import dana9919.gles.G9Mesh;
import dana9919.gles.G9Model;
import dana9919.gles.G9Render;
import dana9919.gles.ModelCollection;
import dana9919.gles.base.G9ENUM;

import dana9919.gles.base.G9GameView;

/** 
 * @author 徐潇
 * mail: dana9919@163.com
 * QQ:61092517
 * Class name MyGame0824 派生自 GlSurfaceView 对整个3D场景,摄像机,渲染器进行初始化
 * 	:这个类里面InitModel与 InitCamera方法的调用比较特殊,是在Render::onSurfaceCreated()里面进行的,
 * 当然是有原因的,纹理的构建及摄像机尺寸取得的操作必须在 glSrufaceView创建之后 
 *  :这就又造成了逻辑上的困难,框架构造的时候就准备把渲染器G9Render 做成通用的,如果在Render里面写InitModel InitCamera
 *  就不存在什么通用性了,所以构建了 G9GameView,在glSurfaceView基础上加了两个abstract方法,
 *  并要求G9Render构造时必须传入一个 G9GameView对象 
 *  虽然逻辑上绕了一圈,但减少了耦合 保证了dana9919.gles 包的独立性*  
 * 
 */
public class MyGame0824 extends G9GameView{

	//------------------------------------------------常数
	static final float ONE_DEGREE = 1.0f/180.0f;
	//------------------------------------------------元素
	G9Camera viewCamera = null;//构造在回调函数中
	G9Camera lightCamera = null;//构造在回调函数中
	G9Render render = null;	
	ModelCollection mc = null;
	//操作线程
	TestThread td;
	float fXScreenPre = 0;
	float fYScreenPre = 0;
	//------------------------------------------------构造
	public MyGame0824(Context context) {
		super(context);
		render = new G9Render(this);//传空值进去没用,RENDER里保存的是空指针喵的
		setRenderer(render);
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}//ef constructor

	//------------------------------------------------函数
	@Override
	public void InitModel() {
		// 加载模型
		mc = new ModelCollection();
		G9Mesh mesh = new G9Mesh(this.getResources(), "banana.obj");
		mesh.SetRenderTech(G9ENUM.RENDER_TECH_G9PHONG);
		G9Model model = new G9Model(mesh);
		
		mc.AddModel(model);
		//把 mc 装进render里去
		render.SetModeCollection(mc);
		Log.d("test", "breakPoint");
		
	}//ef
	@Override
	public void InitCamera(int iWinWidth, int iWinHeight) {
		// 初始化摄像机
		viewCamera = new G9Camera(iWinWidth, iWinHeight, 5, 0.8f);
		lightCamera = new G9Camera(100, 100, 100.0f, 0.5f);
		
		viewCamera.SetPosition(new float[]{0,0,1.1f});
		viewCamera.SetLookTarget(new float[]{0,0,0});
		
		lightCamera.SetPosition(new float[]{2,5,0});
		lightCamera.SetLookTarget(new float[]{0,0,0});
		render.SetCameras(viewCamera, lightCamera);
		
		//又一次混乱
		//由于thread初始要传一个CAMERA进去，而且不能传空值进去,所以只有在这儿了
		//怀念C++指向指针的指针
		td = new TestThread(viewCamera);
		td.start();
		
	}//ef
	
	//------------------------------------------------监听
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			fXScreenPre = event.getX();
			fYScreenPre = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float fXNow = event.getX();
			float fYNow = event.getY();
			float fXDelta = fXNow - fXScreenPre;
			float fYDelta = fYNow - fYScreenPre;
			fXScreenPre = fXNow;
			fYScreenPre = fYNow;
			td.fXRotate = -fXDelta*0.5f;
			td.fYRotate = -fYDelta*0.5f;
			break;
			
		default:
			break;
		}
		
		return true;
	}
	//------------------------------------------------内类

	

	 
		
	
}//EC
