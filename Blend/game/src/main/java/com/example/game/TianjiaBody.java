package com.example.game;
import javax.vecmath.Vector3f;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class TianjiaBody
{
	RigidBody gangti;
	public int isnoLanBan=0;//�Ƿ��������ǰһ����ײ�ˣ�0��ʾû����ײ
	public TianjiaBody(CollisionShape colShape,
			DiscreteDynamicsWorld dynamicsWorld,float mass,
			float cx,float cy,float cz,float restitution,float friction){
		
		//��������ĳ�ʼ�任����
		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(cx, cy, cz));	//����λ��	
		Vector3f localInertia = new Vector3f(0, 0, 0);	//��������Ϊ��	
		//����������˶�״̬����
		DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
		//����������Ϣ����
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(0, myMotionState, colShape, localInertia);
		//��������
		gangti = new RigidBody(rbInfo);
		//���÷���ϵ��
		gangti.setRestitution(restitution);
		//����Ħ��ϵ��
		gangti.setFriction(1f);
		//��������ӽ���������
		dynamicsWorld.addRigidBody(gangti, Constant.GROUP_HOUSE, Constant.MASK_HOUSE);
	}
}
