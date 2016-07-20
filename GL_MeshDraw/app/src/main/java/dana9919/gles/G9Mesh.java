package dana9919.gles;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.content.res.Resources;
import dana9919.gles.base.G9ENUM;
import dana9919.gles.base.G9Function;
import dana9919.gles.base.MeshDrawable;
import dana9919.gles.base.MeshInfo;
import dana9919.gles.base.SubInfo;
/**
 * 3D网格
 *  暂时只能由  *.obj文件提取,并且要求 *.obj *.mtl 及纹理文件放在 assets里
 *  暂时本类提取的3D网格只支持  位置,纹理座标,法线三个量
 * @author 徐潇
 * mail: dana9919@163.com
 * QQ 61092517
 */
public class G9Mesh implements MeshDrawable{
	//------------------------------------------------常数	
	//------------------------------------------------元素
	MeshInfo _info = null;
	//------------------------------------------------构造（不提供无参构造）
	public G9Mesh(Resources resource,String strObjFileName) {
		// 按文件名生成网格，并填充至 _info 这个函数会很长 并且原则上不应分成小函数
		ArrayList<Float> arr_v = new ArrayList<Float>();//用于临存 pos 索引元
		ArrayList<Float> arr_vt = new ArrayList<Float>();//临存 texcoord 索引元
		ArrayList<Float> arr_vn = new ArrayList<Float>();//临存 normal 索引元
		try{
			InputStream ins = resource.getAssets().open(strObjFileName) ;//喵的，只提供inputstream的返回
			InputStreamReader inReader = new InputStreamReader(ins);
			BufferedReader br = new BufferedReader(inReader);
			String strTemp = null;//临存每行字符			
			
			SubInfo  curSub = null;//当前操作的SubInfo 用于填充临存数据;
			_info = new MeshInfo();
			
			while((strTemp = br.readLine())!=null)//喵，空行也不为null???
			{
				String[] arrStr = strTemp.split("[ ]+");//按空格分割
				if(arrStr[0].trim().equals("o"))//取文件名
				{
					_info.strMeshName = arrStr[1];
				}else if(arrStr[0].trim().equals("mtllib") )//材质所在文件
				{
					//打开mtl文件,初始图片(时机在sufaceview建立之后),并返回subset数量
					String strMtlFilename = arrStr[1]; 
					_info.iSubsets = SetupMaterial(resource,strMtlFilename);
					
				}else if(arrStr[0].trim().equals( "v"))//POS数据
				{
					arr_v.add(Float.parseFloat(arrStr[1]));
					arr_v.add(Float.parseFloat(arrStr[2]));
					arr_v.add(Float.parseFloat(arrStr[3]));
					
				}else if(arrStr[0] .trim().equals("vt"))//texcoord数据
				{
					arr_vt.add(Float.parseFloat(arrStr[1]));
					arr_vt.add(1.0f-Float.parseFloat(arrStr[2]));
					
				}else if(arrStr[0] .trim().equals( "vn"))//normal 数据
				{
					arr_vn.add(Float.parseFloat(arrStr[1]));
					arr_vn.add(Float.parseFloat(arrStr[2]));
					arr_vn.add(Float.parseFloat(arrStr[3]));
				}/////////////接下去读取面信息并填充到 SubTemp结构
				else if(arrStr[0] .trim().equals( "usemtl"))//以下的面属于哪个SUBSET
				{
					curSub = _info.GetSubset(arrStr[1]);//变更当前操作的subinfo
				}				
				else if(arrStr[0] .trim().equals( "f"))
				{
					//首先这个时候 curSub应不为空 暂时省去检测了
					String[] arrVertex = null;
					int index = -1;					 
					for(int i =1; i<=3; i++)
					{
						arrVertex = arrStr[i].split("/");//本面第 i 个点的信息
						index= Integer.parseInt(arrVertex[0])-1;//POS的索引
						curSub.arrV.add(arr_v.get(3*index+0));
						curSub.arrV.add(arr_v.get(3*index+1));
						curSub.arrV.add(arr_v.get(3*index+2));
						
						index = Integer.parseInt(arrVertex[1])-1;//UV的索引
						curSub.arrVT.add(arr_vt.get(2*index));
						curSub.arrVT.add(arr_vt.get(2*index+1));
						
						
						index = Integer.parseInt(arrVertex[2])-1;//NORMAL 的索引
						curSub.arrVN.add(arr_vn.get(3*index));
						curSub.arrVN.add(arr_vn.get(3*index+1));
						curSub.arrVN.add(arr_vn.get(3*index+2));
					}//end for					
				}//"f"开头段结束				
			}//end while			
			//调用MeshInfo的自操作函数 CommitMeshInfo()来进行最后的调拨
			_info.CommitMeshInfo();
		}catch (Exception e) {
			// 
			e.printStackTrace();
		}//end try
		//初始RENDER_TECH 以phong技术打底
		_info.enumRENDER_TECH = G9ENUM.RENDER_TECH_G9PHONG;
		
	}//ef constructor
	/**
	 * 初始化此MESH的材质信息
	 * @param resource 系统资源
	 * @param strMtlFilename 材质文件
	 * @return SUBSET数量
	 * @throws IOException 
	 */
	private int SetupMaterial(Resources resource, String strMtlFilename) throws IOException {
		//Ka Kd Ks暂时不做,现在连 subinfo里没都没做进去
		InputStream ins = resource.getAssets().open(strMtlFilename);
		InputStreamReader insReader = new InputStreamReader(ins);
		BufferedReader br = new BufferedReader(insReader);
		String strTemp = null;
		int iSubCount = 0;
		SubInfo curSub = null;//当前的SUBSET INFO
		while((strTemp = br.readLine())!= null){
			String[] arrStr = strTemp.split("[ ]+");
			if(arrStr[0] .trim().equals( "newmtl"))//标示材质
			{
				//暂时不作其它初始
				iSubCount++;
				curSub = new SubInfo(arrStr[1]);
				_info.arrSubInfo.add(curSub);
			}else if(arrStr[0] .trim().equals( "map_Kd"))//纹理名
			{
				//加个空检,有点多余
				if(curSub == null)
					break;								
				curSub.idDTex = G9Function.CreateTexture(resource,arrStr[1]);				
			}
		}//end while		
		return iSubCount;
	}//ef
	//------------------------------------------------函数
	@Override
	public MeshInfo GetMeshInfo() {
		// 必要时加入NULL检测
		return _info;
	}//ef
	public void SetRenderTech(int enumRENDER_TECH){
		_info.enumRENDER_TECH = enumRENDER_TECH;
	}//ef

	
	//------------------------------------------------监听
	//------------------------------------------------内类
	//------------------------------------------------test

	


}//EC
