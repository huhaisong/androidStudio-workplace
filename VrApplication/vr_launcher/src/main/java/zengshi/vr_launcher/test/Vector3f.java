package zengshi.vr_launcher.test;

//���ڴ洢�����������
public class Vector3f
{
	float x;
	float y;
	float z;
	public Vector3f(){}
	public Vector3f(float x,float y,float z)
	{
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public Vector3f(float[] v)
	{
		this.x=v[0];
		this.y=v[1];
		this.z=v[2];
	}
	//�ӷ�
	public Vector3f add(Vector3f v){
		return new Vector3f(this.x+v.x,this.y+v.y,this.z+v.z);
	}
	//����
	public Vector3f minus(Vector3f v){
		return new Vector3f(this.x-v.x,this.y-v.y,this.z-v.z);
	}

	//�볣�����
	public Vector3f multiK(float k){
		return new Vector3f(this.x*k,this.y*k,this.z*k);
	}
	//���
	public void normalize(){
		float mod=module();
		x /= mod;
		y /= mod;
		z /= mod;
	}
	//ģ
	public float module(){
		return (float) Math.sqrt(x*x+y*y+z*z);
	}
}
