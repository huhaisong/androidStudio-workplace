package zengshi.vr_launcher.test;
/*
 * ���Ա����ص��ĳ����࣬
 * ����̳��˸�����Ա����ص�
 */
public abstract class TouchableObject {
	AABB3 preBox;//����任֮ǰ�İ�Χ��
    float[] m = new float[16];//����任�ľ���    
    //������ɫ
	float[] color=new float[]{1,1,1,1};
	float size = 1.5f;;//�Ŵ�ĳߴ�
	//������ĵ�λ�úͳ���ߵķ���
    public AABB3 getCurrBox(){
    	return preBox.setToTransformedBox(m);//��ȡ�任��İ�Χ��
    
    }
    //���غ�Ķ����������ҪҪ����Ӧ�Ķ�
	public void changeOnTouch(boolean flag){
		if (flag) {
			color = new float[] { 0, 1, 0, 1 };
			size = 3f;
		} else {
			color = new float[] { 1, 1, 1, 1 };
			size = 1.5f;
		}	
	}
    //���Ʊ任����
    public void copyM(){
    	for(int i=0;i<16;i++){
    		m[i]=MatrixState.getMMatrix()[i];
    	}
    }	
}
