package dana9919.gles.base;


/**
 * 所有要进G9管线的模型,都要派生自这个接口勒
 * 方法慢慢加
 * @author dana9919
 *
 */
 
public interface MeshDrawable {
	/**
	 * 取出 G9VertInfo 供G9Render用(用于确定向GPU发送哪些顶点属性)
	 * 由于里面包含了所有要绘制的信息(矩阵不算) 所以省去了drawMesh 
	 * @return
	 */	
	public MeshInfo GetMeshInfo();
}
