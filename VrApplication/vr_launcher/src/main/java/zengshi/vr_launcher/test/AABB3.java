package zengshi.vr_launcher.test;
import android.opengl.Matrix;
//�����AABB��Χ��
public class AABB3
{
	Vector3f min;
	Vector3f max;
	//�ղι�����
	AABB3(){
		min = new Vector3f();
		max = new Vector3f();
		empty();
	}
	//����Ϊ��������Ĺ�����
	AABB3(float[] vertices){
		min = new Vector3f();
		max = new Vector3f();
		empty();
		//�����еĵ�����Χ��
		for(int i=0; i<vertices.length; i+=3){
			this.add(vertices[i], vertices[i+1], vertices[i+2]);
		}
	}
	//���AABB
	public void empty(){
		min.x = min.y = min.z = Float.POSITIVE_INFINITY;//����С����Ϊ���ֵ
		max.x = max.y = max.z = Float.NEGATIVE_INFINITY;//��������Ϊ��Сֵ
	}
	//����������뵽AABB�У����ڱ�Ҫ��ʱ����չAABB�԰�ÿ����
	public void add(Vector3f p){
		if (p.x < min.x) { min.x = p.x; }
		if (p.x > max.x) { max.x = p.x; }
		if (p.y < min.y) { min.y = p.y; }
		if (p.y > max.y) { max.y = p.y; }
		if (p.z < min.z) { min.z = p.z; }
		if (p.z > max.z) { max.z = p.z; }
	}
	public void add(float x, float y, float z){
		if (x < min.x) { min.x = x; }
		if (x > max.x) { max.x = x; }
		if (y < min.y) { min.y = y; }
		if (y > max.y) { max.y = y; }
		if (z < min.z) { min.z = z; }
		if (z > max.z) { max.z = z; }
	}
	//��ȡAABB���ж������ķ���
	public Vector3f[] getAllCorners(){
		Vector3f[] result = new Vector3f[8];
		for(int i=0; i<8; i++){
			result[i] = getCorner(i);
		}
		return result;
	}
	//��ȡAABB��i���������ķ���
	public Vector3f getCorner(int i){		
		if(i<0||i>7){//���i�Ƿ�Ϸ�
			return null;
		}
		return new Vector3f(
				((i & 1) == 0) ? max.x : min.x,
				((i & 2) == 0) ? max.y : min.y, 
				((i & 4) == 0) ? max.z : min.z
				);
	}
	//ͨ��ǰ����任������÷���任���AABB��Χ�еķ���
	public AABB3 setToTransformedBox(float[] m)
	{
		//��ȡ���ж�������
		Vector3f[] va = this.getAllCorners();
		//���ڴ�ŷ���任��Ķ�������
	    float[] transformedCorners=new float[24];
	    //���任ǰ��AABB��Χ�е�8�����������任����m��ˣ��õ�����任���OBB��Χ�е����ж���
		float[] tmpResult=new float[4];
	    int count=0;
		for(int i=0;i<va.length;i++){
			float[] point=new float[]{va[i].x,va[i].y,va[i].z,1};//������ת����������
			Matrix.multiplyMV(tmpResult, 0, m, 0, point, 0);
			transformedCorners[count++]=tmpResult[0];
			transformedCorners[count++]=tmpResult[1];
			transformedCorners[count++]=tmpResult[2];
		}
		//ͨ��������OBB��Χ��ת����AABB��Χ�У�������
		return new AABB3(transformedCorners);
	}
	public float getXSize(){//��ȡx�����С
		return max.x - min.x;
	}
	public float getYSize(){//��ȡy�����С
		return max.y - min.y;
	}
	public float getZSize(){//��ȡz�����С
		return max.z - min.z;
	}
	public Vector3f getSize(){//��ȡ�Խ�������
		return max.minus(min);
	}
	//��ȡ��Χ�е����ĵ����ķ���
	public Vector3f getCenter(){
		return (min.add(max)).multiK(0.5f);
	}
	
	/*
	 * Woo����ķ��������жϾ��α߽����ĸ�����ཻ��
	 * �ټ���������������ƽ����ཻ�ԡ�
	 * �����ں����У���ô��������α߽���ཻ��
	 * ���򲻴����ཻ 
	 */
	//�Ͳ������ߵ��ཻ�Բ��ԣ�����ཻ�򷵻�ֵ��һ���ǳ������(����1)
	//����ཻ�������ཻʱ��t
	//tΪ0-1֮���ֵ
	public float rayIntersect(
			Vector3f rayStart,//�������
			Vector3f rayDir,//���߳��Ⱥͷ���
			Vector3f returnNormal//��ѡ�ģ��ཻ�㴦������
			){
		//���δ�ཻ�򷵻��������
		final float kNoIntersection = Float.POSITIVE_INFINITY;
		//�����ھ��α߽��ڵ�����������㵽ÿ����ľ���
		boolean inside = true;
		float xt, xn = 0.0f;
		if(rayStart.x<min.x){
			xt = min.x - rayStart.x;
			if(xt>rayDir.x){ return kNoIntersection; }
			xt /= rayDir.x;
			inside = false;
			xn = -1.0f;
		}
		else if(rayStart.x>max.x){
			xt = max.x - rayStart.x;
			if(xt<rayDir.x){ return kNoIntersection; }
			xt /= rayDir.x;
			inside = false;
			xn = 1.0f;
		}
		else{
			xt = -1.0f;
		}
		
		float yt, yn = 0.0f;
		if(rayStart.y<min.y){
			yt = min.y - rayStart.y;
			if(yt>rayDir.y){ return kNoIntersection; }
			yt /= rayDir.y;
			inside = false;
			yn = -1.0f;
		}
		else if(rayStart.y>max.y){
			yt = max.y - rayStart.y;
			if(yt<rayDir.y){ return kNoIntersection; }
			yt /= rayDir.y;
			inside = false;
			yn = 1.0f;
		}
		else{
			yt = -1.0f;
		}
		
		float zt, zn = 0.0f;
		if(rayStart.z<min.z){
			zt = min.z - rayStart.z;
			if(zt>rayDir.z){ return kNoIntersection; }
			zt /= rayDir.z;
			inside = false;
			zn = -1.0f;
		}
		else if(rayStart.z>max.z){
			zt = max.z - rayStart.z;
			if(zt<rayDir.z){ return kNoIntersection; }
			zt /= rayDir.z;
			inside = false;
			zn = 1.0f;
		}
		else{
			zt = -1.0f;
		}
		//�Ƿ��ھ��α߽���ڣ�
		if(inside){
			if(returnNormal != null){
				returnNormal = rayDir.multiK(-1);
				returnNormal.normalize();
			}
			return 0.0f;
		}
		//ѡ����Զ��ƽ�桪�����������ཻ�ĵط�
		int which = 0;
		float t = xt;
		if(yt>t){
			which = 1;
			t=yt;
		}
		if(zt>t){
			which = 2;
			t=zt;
		}
		switch(which){
			case 0://��yzƽ���ཻ
			{
				float y=rayStart.y+rayDir.y*t;
				if(y<min.y||y>max.y){return kNoIntersection;}
				float z=rayStart.z+rayDir.z*t;
				if(z<min.z||z>max.z){return kNoIntersection;}
				if(returnNormal != null){
					returnNormal.x = xn;
					returnNormal.y = 0.0f;
					returnNormal.z = 0.0f;
				}				
			}
			break;
			case 1://��xzƽ���ཻ
			{
				float x=rayStart.x+rayDir.x*t;
				if(x<min.x||x>max.x){return kNoIntersection;}
				float z=rayStart.z+rayDir.z*t;
				if(z<min.z||z>max.z){return kNoIntersection;}
				if(returnNormal != null){
					returnNormal.x = 0.0f;
					returnNormal.y = yn;
					returnNormal.z = 0.0f;
				}				
			}
			break;
			case 2://��xyƽ���ཻ
			{
				float x=rayStart.x+rayDir.x*t;
				if(x<min.x||x>max.x){return kNoIntersection;}
				float y=rayStart.y+rayDir.y*t;
				if(y<min.y||y>max.y){return kNoIntersection;}
				if(returnNormal != null){
					returnNormal.x = 0.0f;
					returnNormal.y = 0.0f;
					returnNormal.z = zn;
				}				
			}
			break;
		}
		return t;//�����ཻ�����ֵ
	}
}
