package com.example.game;

import javax.vecmath.Quat4f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;

public class SYSUtil 
{
	 public static int numManifolds;
	 public static PersistentManifold contactManifold;
	 public static int numContacts;
	//����Ԫ��ת��Ϊ�Ƕȼ�ת������
	public static float[] fromSYStoAXYZ(Quat4f q4)
	{	
		double sitaHalf=Math.acos(q4.w);
		float nx=(float) (q4.x/Math.sin(sitaHalf));
		float ny=(float) (q4.y/Math.sin(sitaHalf));
		float nz=(float) (q4.z/Math.sin(sitaHalf));
		
		return new float[]{(float) Math.toDegrees(sitaHalf*2),nx,ny,nz};
	}
	  //�÷������ڼ�������������ײ
    public static boolean isCollided(DiscreteDynamicsWorld dynamicsWorld,CollisionObject coA,CollisionObject coB)
    {
    	 numManifolds = dynamicsWorld.getDispatcher().getNumManifolds();
    	 for(int i=0;i<numManifolds;i++)
    	 {
    		 contactManifold = dynamicsWorld.getDispatcher().getManifoldByIndexInternal(i);  
    		 numContacts= contactManifold.getNumContacts();  
    		 if(numContacts>0)
    		 {
	    		CollisionObject obA = (CollisionObject)contactManifold.getBody0();  
	    		CollisionObject obB = (CollisionObject)contactManifold.getBody1();  
	    		if((coA==obA&&coB==obB)||(coA==obB&&coB==obA))
	    		{
	    			return true;
	    		}
    		 }
    	 }
    	return false;
    }
}
