package com.example.game;
import static com.example.game.Constant.*;


import javax.vecmath.Vector3f;


import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class JiaoNangTianjiaBody
{
	float yAngle;
	RigidBody gangti;
	public int isnoLanBan=0;//�Ƿ��������ǰһ����ײ�ˣ�0��ʾû����ײ
	public JiaoNangTianjiaBody(CollisionShape colShape,
			DiscreteDynamicsWorld dynamicsWorld,float mass,
			float cx,float cy,float cz,float restitution,float friction,float xAngle,float yAngle,float zAngle){
		this.yAngle=yAngle;
		//���ø�����ܶ�
		boolean isDynamic = (mass != 0f);
		Vector3f localInertia = new Vector3f(0, 0, ZJ_LENGTH/2);//�������
		if (isDynamic) 
		{
			colShape.calculateLocalInertia(mass, localInertia);//�����ܶ�
		}
		//��������ĳ�ʼ�任����
		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(cx, cy, cz));	//����λ��	
		groundTransform.basis.rotX((float)Math.toRadians(xAngle));
		groundTransform.basis.rotY((float)Math.toRadians(yAngle));
		groundTransform.basis.rotZ((float)Math.toRadians(zAngle));
		//����������˶�״̬����
		DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
		//����������Ϣ����
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo
		(
			mass, myMotionState, colShape, localInertia
		);
		
		
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
