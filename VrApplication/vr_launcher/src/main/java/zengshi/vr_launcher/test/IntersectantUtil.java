package zengshi.vr_launcher.test;
public class IntersectantUtil {	
	/*
	 * 1��ͨ������Ļ�ϵĴ���λ�ã������Ӧ�Ľ�ƽ������꣬
	 * �Ա����AB��������������ϵ�е����
	 * 2����AB����������������ϵ�е���������������������
	 * �Ա����AB�������������ϵ�е����
	 */
	public static float[] calculateABPosition
	(
		float x,//����X���
		float y,//����Y���
		float w,// ��Ļ���
		float h,// ��Ļ�߶�
		float left,//�ӽ�leftֵ
		float top,//�ӽ�topֵ
		float near,//�ӽ�nearֵ
		float far//�ӽ�farֵ
	)
	{
		//���ӿڵ����������ԭ��ʱ�����ص�����
		float x0=x-w/2;
		float y0=h/2-y;		
		//�����Ӧ��near���ϵ�x��y���
		float xNear=2*x0*left/w;
		float yNear=2*y0*top/h;
		//�����Ӧ��far���ϵ�x��y���
		float ratio=far/near;
		float xFar=ratio*xNear;
		float yFar=ratio*yNear;
		//��������ϵ��A�����
        float ax=xNear;
        float ay=yNear;
        float az=-near;
        //��������ϵ��B�����
        float bx=xFar;
        float by=yFar;
        float bz=-far; 
        //ͨ����������ϵ��A��B�������꣬���������ϵ��A��B��������
		float[] A = MatrixState.fromPtoPreP(new float[] { ax, ay, az });
		float[] B = MatrixState.fromPtoPreP(new float[] { bx, by, bz });
		return new float[] {//�������յ�AB�������
			A[0],A[1],A[2],
			B[0],B[1],B[2]
		};
	}
}