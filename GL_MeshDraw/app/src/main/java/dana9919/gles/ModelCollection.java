package dana9919.gles;

import java.util.ArrayList;


/**
 * 模型队列 为引擎实现标准化的数据格式
 * @author 徐潇
 * mail: dana9919@163.com
 * QQ 61092517
 */
public class ModelCollection {
	ArrayList<G9Model> _arrModel = new ArrayList<G9Model>();
	public ModelCollection() {
		// 初始化
		//暂时没有
	}//ef constructor
	/**
	 * 向集合里增加一个Model
	 * @param model 喵,是指针
	 */
	public void AddModel(G9Model model){
		_arrModel.add(model);
	}//ef
	/**
	 * 取得模型队列
	 * @return 返回的又是指针,用时小心,不小心后里很严重
	 */
	public ArrayList<G9Model> GetMoelList(){
		return _arrModel;
	}//ef
}//EC
