package dana9919.gles.base;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/*
 * 如果要求安全，应把类元素做成 protected ，然后做一批 get函数
 * 在函数中做null检测后传出  可以很大程序上保证不操作空指针
 * 但暂时没这计划，自己用的，小心点就是了 
 * 2012-8-23 日继续补充,由于建造网格的需要,在这里做几个arraylist<Float>的TEMP用于临存
 * 在临存不需要之后,可以使用 null赋值 来等系统回收
 * */
/**
 * 作用:3D网格中每个组(材质组)的内容信息
 * @author 徐潇
 * mail: dana9919@163.com
 * QQ: 61092517
 */
public class SubInfo {
	public String   strSubsetName = "";//出于读obj文件的理由 ,必须有子项名(与 obj mtl文件中相同)
	public FBInfo	posInfo = null;//注意 FBInfo里的 iStrade使用 const了 
	public FBInfo	normalInfo = null;
	public FBInfo	uvInfo = null;
	public FBInfo	colorInfo = null;
	public FBInfo	tangentInfo = null;
	public FBInfo	binormalInfo = null;
	
	public int idDTex; //关于texture的ID 如果没有 应为-1
	public int idSTex;
	public int idNTex;
	public int iSubVertCount;//这个 subset里的顶点数量 
	
	//下面为临存使用的变量 在最后成形时应抛弃临存变量指针 由系统回收
	public ArrayList<Float>		arrV = new ArrayList<Float>();//临存pos
	public ArrayList<Float> 	arrVT = new ArrayList<Float>();//临存texcoord
	public ArrayList<Float>    	arrVN  = new ArrayList<Float>();//临存NORMAL
	
	
	
	public SubInfo(String strSubName){
		//注意 即使使用了构造 ，里面的 FBInfo量还是空的
		strSubsetName = strSubName;
		idDTex = -1;
		idSTex = -1;
		idNTex = -1;
		iSubVertCount = 0;
	}//ef constructor



	public void PutPosFB(FloatBuffer fbuf)//stride 不作参数了，自动
	{
		posInfo = new FBInfo();
		posInfo._fbuf = fbuf;
		posInfo._iStride = 3*4;
	}//ef
	public void PutNormalFB(FloatBuffer fbuf){
		normalInfo = new FBInfo();
		normalInfo._fbuf = fbuf;
		normalInfo._iStride = 3*4;
		
	}//ef
	public void PutUvFB(FloatBuffer fbuf){
		uvInfo = new FBInfo();
		uvInfo._fbuf = fbuf;
		uvInfo._iStride = 2*4;
	}//ef
	public void PutColorFB(FloatBuffer fbuf){
		colorInfo = new FBInfo();
		colorInfo._fbuf = fbuf;
		colorInfo._iStride = 4*4;
	}//ef
	public void PutTangentFB(FloatBuffer fbuf){
		tangentInfo = new FBInfo();
		tangentInfo._fbuf = fbuf;
		tangentInfo._iStride = 3*4;
	}//ef
	public void PutBinormalFB(FloatBuffer fbuf){
		binormalInfo = new FBInfo();
		binormalInfo._fbuf = fbuf;
		binormalInfo._iStride = 3*4;
	}//ef
	/**
	 * 由临存的信息来生成 最后FBInfo格式,喵,Commit是不是难听了点,有机会换个气派点的名字
	 * @return 本SUBSET的顶点数量(,出错返回-1)
	 */
	public int CommitSubInfo(){
		PutPosFB(G9Function.PushIntoFLoatBuffer(arrV));
		PutUvFB(G9Function.PushIntoFLoatBuffer(arrVT));
		PutNormalFB(G9Function.PushIntoFLoatBuffer(arrVN));
		iSubVertCount = arrV.size()/3;
		//自检
		if((arrVT.size()/2 != iSubVertCount)||(arrVN.size()/3 != iSubVertCount))
		{
			iSubVertCount = -1;			
		}
		//扔掉临存数据的指针
		arrV = null;
		arrVN = null;
		arrVT = null;
		return iSubVertCount;
		
	}//ef
}//EC
