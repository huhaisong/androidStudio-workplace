package dana9919.gles.base;

import java.util.ArrayList;

import android.util.Log;
/**
 * 作用:3D网格 的实际数据包容类
 * @author 徐潇
 * mail: dana9919@163.com
 * QQ: 61092517
 */
public class MeshInfo {
	public String strMeshName = "";
	public int iSubsets=0;//SUBSET的个数
	public int enumRENDER_TECH = -1;
	public ArrayList<SubInfo> arrSubInfo = new ArrayList<SubInfo>();
	
	public int GetSubIndex(String strSubName){
		for(int i=0; i<arrSubInfo.size(); i++){
			if((arrSubInfo.get(i).strSubsetName)==strSubName)
				return i;
		}//end for
		return -1;
	}//ef
	public SubInfo GetSubset(String strSubName){
		
		for(int i=0; i<arrSubInfo.size(); i++){
			if((arrSubInfo.get(i).strSubsetName).trim().equals(strSubName))
				return arrSubInfo.get(i);
		}//end for
		return null;
	}//ef
	
	public SubInfo GetSubset(int index){
		if(index< arrSubInfo.size()){
			return arrSubInfo.get(index);
		}else{
			Log.e("ERR", "in MeshInfo::GetSubset");
			return null;
		}
		
	}//ef
	/**
	 * 生成MESH INFO后最后一步 检测并完成整个MESH INFO
	 * @return 总顶点数
	 */
	public int CommitMeshInfo(){
		iSubsets = arrSubInfo.size();
		int iVertCount = 0;
		for (int i=0; i<iSubsets; i++){
			int iSubVert = arrSubInfo.get(i).CommitSubInfo();
			if(iSubVert<0)
			{
				//扔个
				throw new RuntimeException("Mesh的subset有问题");
			}				
			iVertCount += iSubVert;
		}//end for
		return iVertCount;
	}//ef
}//EC
