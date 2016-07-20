//本类并不独立使用，本类应由派生类产生实际作用
package dana9919.gles.base;

import android.content.Context;
import android.opengl.GLSurfaceView;
/**
 * 作用: GLSurfaceView的abstract派生类,提供了 initModel() initcamera()的方法模型,方便着色器类进行标准化的调用 
 * @author 徐潇
 * mail: dana9919@163.com
 * QQ: 61092517
 */
public abstract class G9GameView extends GLSurfaceView{
	protected Context _context;

	public G9GameView(Context context){
		super(context);
		//一些必然的东东放在这里了
		_context = context;
		this.setEGLContextClientVersion(2);//这句注意不要忘记, 标明GLES的版本号		
	}//ef constructor
	
	//函数
	/**这个函数的调用 是在 Render类里面的 （由于模型导入及camera初始都要在surface建成后）
	定个型先，在派生类中再重写
	*/
	public abstract void InitModel();
	/**
	 * 这个函数的调用 是在 Render类里面的 （由于模型导入及camera初始都要在surface建成后
	 * 一般在 onSurfaceChange()里可以得到屏高屏宽）
	 * 定个型先，在派生类中再重写
	 */
	public abstract void InitCamera(int iWinWidth,int iWinHeight);
	
	
}//EC
