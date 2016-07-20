package com.example.game;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import android.opengl.GLES20;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class BasketBallForDraw 
{
	BasketBallTextureByVertex ball;//������
	RigidBody body;//��Ӧ�ĸ������
	float cx,cy,cz;//��������λ��
	//�����״̬��־λ
	int ball_State=0;//1��ʾ���������ϲ��������˶�.���������Ϊ0
	//�����X�ٶ�״̬
	public  int isnoLanBan=0;//�Ƿ��������ǰһ����ײ�ˣ�0��ʾû����ײ
	public  int isnoLanQuan=0;//�Ƿ����Ȧ��ǰһ����ײ�ˣ�0��ʾû����ײ��1��ʾ��ײ��
	public BasketBallForDraw
	(
		BasketBallTextureByVertex ball,CollisionShape colShape,
		DiscreteDynamicsWorld dynamicsWorld,float mass,float cx,float cy,float cz,short group,short mask
	)
	{
		this.cx=cx;this.cy=cy;this.cz=cz;
		this.ball=ball;
		//���ø�����ܶ�
		boolean isDynamic = (mass != 0f);
		Vector3f localInertia = new Vector3f(0, 0, 0);//�������
		if (isDynamic) 
		{
			colShape.calculateLocalInertia(mass, localInertia);//�����ܶ�
		}
		//��������ĳ�ʼ�任����
		Transform startTransform = new Transform();
		startTransform.setIdentity();
		startTransform.origin.set(new Vector3f(cx, cy, cz));//λ��
		//����������˶�״̬����
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		//����������Ϣ����
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo
		(
			mass, myMotionState, colShape, localInertia
		);
		//��������
		body = new RigidBody(rbInfo);
		//���÷���ϵ��
		body.setRestitution(0.4f);
		//����Ħ��ϵ��
		body.setFriction(0.8f);
		//�״��Ǿ�ֹ��
		body.setActivationState(CollisionObject.WANTS_DEACTIVATION);
		dynamicsWorld.addRigidBody(body,group,mask);
	}
	public void drawSelf(int ballTexId,int isShadow,int planeId,int isLanbanYy)
	{		
		try
		{
			//��ȡ������ӵı任��Ϣ����
			Transform trans = body.getMotionState().getWorldTransform(new Transform());
			Quat4f ro=trans.getRotation(new Quat4f());
			//�����ֳ�
	        MatrixState.pushMatrix();
			//������λ�任
			//������ת�任    		
			cx=trans.origin.x;
			cy=trans.origin.y;    
			cz=trans.origin.z;
			
			if(isShadow==1)
			{
				GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			}
			else if(cz>3*Constant.QIU_R&&cy<Constant.QIU_R*2.5f)
			{
				GLES20.glDisable(GLES20.GL_DEPTH_TEST);
			}
			else
			{
				GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			}
     
	        MatrixState.translate(cx,cy,cz);
			if(ro.x!=0||ro.y!=0||ro.z!=0)
			{
				float[] fa=SYSUtil.fromSYStoAXYZ(ro);
				MatrixState.rotate(fa[0], fa[1], fa[2], fa[3]);
			}   
			ball.drawSelf(ballTexId, isShadow,planeId,isLanbanYy);
			MatrixState.popMatrix();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
