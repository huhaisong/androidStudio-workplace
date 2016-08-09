package zengshi.vr_launcher.test;
import java.util.ArrayList;

import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//�Ƕ����ű���
    private SceneRenderer mRenderer;//������Ⱦ��    
    
    private float mPreviousY;//�ϴεĴ���λ��Y���
    private float mPreviousX;//�ϴεĴ���λ��X���
	//���������ı���
	float cx=0;//�����xλ��
	float cy=0;//�����yλ��
	float cz=60;//�����zλ��
	
	float tx=0;//Ŀ���xλ��
	float ty=0;//Ŀ���yλ��
	float tz=0;//Ŀ���zλ��
	public float currSightDis=100;//������Ŀ��ľ���
	float angdegElevation=30;//����
	public float angdegAzimuth=180;//��λ��	
	float left;
    float right;
	float top;
	float bottom;
	float near;
	float far;
	
	//�ɴ��������б�
	ArrayList<TouchableObject> lovnList=new ArrayList<TouchableObject>();
	//��ѡ�����������ֵ����id��û�б�ѡ��ʱ����ֵΪ-1
	int checkedIndex=-1;
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //����ʹ��OPENGL ES2.0
        mRenderer = new SceneRenderer();	//����������Ⱦ��
        setRenderer(mRenderer);				//������Ⱦ��		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ   
    }
	
	//�����¼��ص�����
    @Override 
    public boolean onTouchEvent(MotionEvent e) 
    {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_DOWN:
			//�������任��AB�����λ��
			float[] AB=IntersectantUtil.calculateABPosition
			(
				x, //���ص�X���
				y, //���ص�Y���
				Sample19_1_Activity.screenWidth, //��Ļ���
				Sample19_1_Activity.screenHeight, //��Ļ����
				left, //�ӽ�left��topֵ
				top,
				near, //�ӽ�near��farֵ
				far
			);
			//����AB
			Vector3f start = new Vector3f(AB[0], AB[1], AB[2]);//���
			Vector3f end = new Vector3f(AB[3], AB[4], AB[5]);//�յ�
			Vector3f dir = end.minus(start);//���Ⱥͷ���
			/*
			 * ����AB�߶���ÿ�������Χ�е���ѽ���(��A�����Ľ���)��
			 * ����¼����ѽ�����������б��е�����ֵ
			 */
			//��¼�б���ʱ����С������ֵ
    		checkedIndex = -1;//���Ϊû��ѡ���κ�����
    		int tmpIndex=-1;//��¼��A����������������ʱֵ
    		float minTime=1;//��¼�б�������������AB�ཻ�����ʱ��
    		for(int i=0;i<lovnList.size();i++){//�����б��е�����
    			AABB3 box = lovnList.get(i).getCurrBox(); //�������AABB��Χ��   
				float t = box.rayIntersect(start, dir, null);//�����ཻʱ��
    			if (t <= minTime) {
					minTime = t;//��¼��Сֵ
					tmpIndex = i;//��¼��Сֵ����
				}
    		}
    		checkedIndex=tmpIndex;//���������checkedIndex��    		
    		changeObj(checkedIndex);//�ı䱻ѡ������	
       	break;
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//���㴥�ر�Yλ��
            float dx = x - mPreviousX;//���㴥�ر�Xλ��
            //��������ֵ���ƶ������
            if(Math.abs(dx)<7f && Math.abs(dy)<7f){
            	break;
            }            
            angdegAzimuth += dx * TOUCH_SCALE_FACTOR;//������x����ת�Ƕ�
            angdegElevation += dy * TOUCH_SCALE_FACTOR;//������z����ת�Ƕ�
            //������������5��90�ȷ�Χ��
            angdegElevation = Math.max(angdegElevation, 5);
            angdegElevation = Math.min(angdegElevation, 90);
            //����������λ��
            setCameraPostion();
        break;
        }
        mPreviousY = y;//��¼���ر�λ��
        mPreviousX = x;//��¼���ر�λ��
        return true;
    }
    //���������λ�õķ���
	public void setCameraPostion() {
		//����������λ��
		double angradElevation = Math.toRadians(angdegElevation);//���ǣ����ȣ�
		double angradAzimuth = Math.toRadians(angdegAzimuth);//��λ��
		cx = (float) (tx - currSightDis * Math.cos(angradElevation)	* Math.sin(angradAzimuth));
		cy = (float) (ty + currSightDis * Math.sin(angradElevation));
		cz = (float) (tz - currSightDis * Math.cos(angradElevation) * Math.cos(angradAzimuth));
	}
	//�ı��б����±�Ϊindex������
	public void changeObj(int index){
		if(index != -1){//��������屻ѡ��
    		for(int i=0;i<lovnList.size();i++){
    			if(i==index){//�ı�ѡ�е�����
    				lovnList.get(i).changeOnTouch(true);
    			}
    			else{//�ָ���������
    				lovnList.get(i).changeOnTouch(false);
    			}
    		}
        }
    	else{//���û�����屻ѡ��
    		for(int i=0;i<lovnList.size();i++){//�ָ���������			
    			lovnList.get(i).changeOnTouch(false);
    		}
    	}
	}
	private class SceneRenderer implements Renderer
    {
    	//��ָ����obj�ļ��м��ض���
		LoadedObjectVertexNormalFace pm;
		LoadedObjectVertexNormalFace cft;
		LoadedObjectVertexNormalAverage qt;
		LoadedObjectVertexNormalAverage yh;
		LoadedObjectVertexNormalAverage ch;
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//�����Ȼ�������ɫ����
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
			//����cameraλ��
			MatrixState.setCamera(cx, cy, cz, tx, ty, tz, 0, 1, 0);
            //��ʼ����Դλ��
            MatrixState.setLightLocation(100, 100, 100);                    
            //��������            
            pm.drawSelf();//ƽ��
        	
            //���Ƴ�����
            MatrixState.pushMatrix();
            MatrixState.translate(-30f, 0f, 0);
            MatrixState.scale(cft.size, cft.size, cft.size);
            cft.drawSelf();
            MatrixState.popMatrix();   
            //��������
            MatrixState.pushMatrix();
            MatrixState.translate(30f, 0f, 0);
            MatrixState.scale(qt.size, qt.size, qt.size);
            qt.drawSelf();
            MatrixState.popMatrix();  
            //����Բ��
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -30f);
            MatrixState.scale(yh.size, yh.size, yh.size);
            MatrixState.rotate(45, 0, 1, 0);
            yh.drawSelf();
            MatrixState.popMatrix();  
            //���Ʋ��
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, 30f);
            MatrixState.scale(ch.size, ch.size, ch.size);
            MatrixState.rotate(30, 0, 1, 0);
            ch.drawSelf();
            MatrixState.popMatrix(); 
        } 

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //�����Ӵ���С��λ�� 
        	GLES20.glViewport(0, 0, width, height); 
        	//����GLSurfaceView�Ŀ�߱�
            float ratio = (float) width / height;
            //���ô˷����������͸��ͶӰ����
            left=right=ratio;
            top=bottom=1;
            near=2;
            far=500;
            MatrixState.setProjectFrustum(-left, right, -bottom, top, near, far);
            //����������λ��
            setCameraPostion();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //������Ļ����ɫRGBA
            GLES20.glClearColor(0.3f,0.3f,0.3f,1.0f);    
            //����ȼ��
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);  
            //�رձ������
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            //��ʼ���任����
            MatrixState.setInitStack();
            //����Ҫ���Ƶ�����
            pm=LoadUtil.loadFromFileVertexOnlyFace("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
                        
            ch=LoadUtil.loadFromFileVertexOnlyAverage("ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		cft=LoadUtil.loadFromFileVertexOnlyFace("cft.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		qt=LoadUtil.loadFromFileVertexOnlyAverage("qt.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		yh=LoadUtil.loadFromFileVertexOnlyAverage("yh.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		lovnList.add(ch);
            lovnList.add(cft);
            lovnList.add(qt);
            lovnList.add(yh);
        }
    }
}
