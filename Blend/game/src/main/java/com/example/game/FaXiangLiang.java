package com.example.game;

import java.util.Set;
//��ʾ���������࣬�����һ�������ʾһ��������
public class FaXiangLiang 
{
   public static final float FAZHI=0.0000001f;//�ж������������Ƿ���ͬ����ֵ
   //��������XYZ���ϵķ���
   float nx;
   float ny;
   float nz;
   
   public FaXiangLiang(float nx,float ny,float nz)
   {
	   this.nx=nx;
	   this.ny=ny;
	   this.nz=nz;
   }
   
   @Override 
   public boolean equals(Object o)
   {
	   if(o instanceof  FaXiangLiang)
	   {//������������XYZ��������ĲС��ָ������ֵ����Ϊ���������������
		   FaXiangLiang tn=(FaXiangLiang)o;
		   if(Math.abs(nx-tn.nx)<FAZHI&&
			  Math.abs(ny-tn.ny)<FAZHI&&
			  Math.abs(ny-tn.ny)<FAZHI
             )
		   {
			   return true;
		   }
		   else
		   {
			   return false;
		   }
	   }
	   else
	   {
		   return false;
	   }
   }
   
   //����Ҫ�õ�HashSet�����һ��Ҫ��дhashCode����
   @Override
   public int hashCode()
   {
	   return 1;
   }
   
   //������ƽ��ֵ�Ĺ��߷���
   public static float[] getAverage(Set<FaXiangLiang> sn)
   {
	   //��ŷ������͵�����
	   float[] result=new float[3];
	   //�Ѽ��������еķ��������
	   for(FaXiangLiang n:sn)
	   {
		   result[0]+=n.nx;
		   result[1]+=n.ny;
		   result[2]+=n.nz;
	   }	   
	   //����ͺ�ķ��������
	   return MoXingJiaZai.vectorNormal(result);
   }
}
