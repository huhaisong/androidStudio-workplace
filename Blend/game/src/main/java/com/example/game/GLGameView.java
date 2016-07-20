package com.example.game;

import static com.example.game.Constant.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import com.bn.tl.R;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CapsuleShapeZ;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

public class GLGameView extends GLSurfaceView {
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320 / 4;//�Ƕ����ű���
    BasketBall_Shot_Activity father;//��Activity������
    SceneRenderer myRenderer;//��Ⱦ��
    float xAngle = 30f;//����
    float yAngle = 0f;
    float upX = 0;
    float upY = 1;
    float upZ = 0;//up��
    boolean isnoCamear = false;//������Ƿ���Ҫ�ع�ԭ�㣬�Ƿ���Ͷ��ȥ���Ѿ���Ͷ��ȥ���򷵻�
    float cx = CAMERA_X; //�����λ��
    float cy = CAMERA_Y;
    float cz = CAMERA_Z;
    float tx = 0;//Ŀ���λ��
    float ty = CAMERA_Y;
    float tz = 0;
    float mPreviousX;//�ϴεĴ���λ��X���
    float mPreviousY;//�ϴεĴ���λ��Y���
    DiscreteDynamicsWorld shijie;//�������
    CollisionDispatcher dispatcher;
    CollisionShape lifangti;//���õ������壬����������
    CollisionShape pingmian[];//���õ�ƽ����״
    CollisionShape basketballShape;//��������
    TianjiaBody planeS[];//����ƽ��
    CollisionShape lanquanjiaonang;//��Ȧ����
    BasketBallTextureByVertex ball;//Բ��
    LanWang lanWuang;
    ArrayList<BasketBallForDraw> ballLst = new ArrayList<BasketBallForDraw>();//װ����������б�
    WenLiJuXing downPanel;//��ƽ��
    WenLiJuXing frontPanel;//ǰƽ��
    WenLiJuXing leftPanel;//��ƽ��
    LanBan backboard;//����
    Yuanzhu zj;//֧��
    MoXing lankuang;//����	
    HuiZhiShuZi shuzi;//����
    WenLiJuXing ybb;//�Ǳ��
    HuanYingJieMianJuXing wr;//��ӭ�������
    HuanYingJieMianJuXing dot;//���������
    WenLiJuXing shipingjimian;//��Ƶ�����µ��ĸ���ť����
    WenLiJuXing wenziJuxing;//���־���
    int curr_process = 0;//��ǰ���
    BasketBallForDraw curr_ball;//��ʱ���������
    float touch_x = 0f;//���崥�����λ��
    float touch_y = 0f;
    Bitmap bm_floor;//------------------���������������bitmap����----------
    Bitmap bm_swall1;
    Bitmap bm_swall3;
    Bitmap bm_swall2;
    Bitmap bm_basketball;
    Bitmap bm_lanban2;
    Bitmap bm_yibiaoban;
    Bitmap bm_number;
    Bitmap bm_basketnet;//����
    Bitmap bm_shou;//��������µ���
    Bitmap bm_stop;//ֹͣ��ť
    Bitmap bm_pause;//��ͣ��ť
    Bitmap bm_play;//���Ű�ť
    float ratio;//�ӿڵ����ű���
    boolean isStart = false;//�Ƿ�ʼ������Ϸ������
    JiaoNangTianjiaBody zjJiaonang1;//����֧��
    JiaoNangTianjiaBody zjJiaonang2;
    int jiaolanggeshu = 8;//�����Ȧ�Ľ��Ҹ���
    JiaoNangTianjiaBody langquanJiaonang[] = new JiaoNangTianjiaBody[jiaolanggeshu];
    public int dibanTexId;//�ذ�����
    public int basketbalolid;//��������
    public int zuobianQiangID;//���ǽ����
    public int youbianQiangID;//�ұ�ǽ����
    public int houmianQiangID;//�������ƽ������
    public int lanbanId;//��������
    public int shijianxiansBeijingId;//����ʱ����ʾ��������
    public int shuziId;//��������
    public int welcomeid;//��������
    public int dotId;//���ؽ����
    public int lanwangId;//��������
    public int wenziId = -1;//�ļ���������
    public int shouId;//ץ
    public int stopId;//ֹͣ��ť
    public int pauseId;//��ͣ��ť
    public int playId;//���Ű�ť
    boolean isFirst = true;//�Ƿ��ǵ�һ֡
    boolean hasLoadOk = false;//�Ƿ��Ѿ��������
    long start;//��¼������ɵ�ʱ��

    public GLGameView(Context context) {
        super(context);
        father = (BasketBall_Shot_Activity) context;//����������λ��
        cx = (float) (tx + Math.cos(Math.toRadians(xAngle)) *
                Math.sin(Math.toRadians(yAngle)) * DISTANCE);//�����x���
        cz = (float) (tz + Math.cos(Math.toRadians(xAngle)) *
                Math.cos(Math.toRadians(yAngle)) * DISTANCE);//�����z���
        cy = (float) (ty + Math.sin(Math.toRadians(xAngle)) * DISTANCE);//�����y���
        deadtimesMS = 0;//��ʼ������ʱ
        flag = true;//����ģ���߳����б�־λ
        Constant.defen = 0;//�÷�����
        this.setEGLContextClientVersion(2);//������ȾģʽΪ2.0
        myRenderer = new SceneRenderer();//������Ⱦ��
        setRenderer(myRenderer);//������Ⱦ��
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ
    }

    //��ʼ����������ķ���
    public void initWorld() {
        //������ײ���������Ϣ����
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        //������ײ����㷨�����߶����书��Ϊɨ�����е���ײ���ԣ���ȷ�����õļ����Զ�Ӧ���㷨
        dispatcher = new CollisionDispatcher(collisionConfiguration);
        BroadphaseInterface overlappingPairCache = new DbvtBroadphase();
        //�����ƶ�Լ�����߶���
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        //���������������
        shijie = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
        //�����������ٶ�
        shijie.setGravity(new Vector3f(0, G, 0));
        //�������õ�������
        lifangti = new BoxShape(new Vector3f(36 * LANBAN_BILIXISHU / 2, 21 * LANBAN_BILIXISHU / 2, LANBAN_BILIXISHU / 2));
        //�������õ�ƽ����״
        pingmian = new CollisionShape[]{
                new StaticPlaneShape(new Vector3f(0, 1f, 0f), 0.05f),//����
                new StaticPlaneShape(new Vector3f(0, 0, -1), 0),//ǰ��
                new StaticPlaneShape(new Vector3f(0, 0, 1), 0),//����
                new StaticPlaneShape(new Vector3f(1, 0, 0), 0),//����
                new StaticPlaneShape(new Vector3f(-1, 0, 0), 0),//����
        };
        //�����������
        basketballShape = new SphereShape(QIU_R);
        lanquanjiaonang = new CapsuleShapeZ(ZJ_R,
                QIU_R * (float) (Math.cos(Math.toRadians((180 - 360 / jiaolanggeshu) / 2))) * 2 - ZJ_R * 2);//��Ȧ�Ľ���
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        boolean isDianjibb = false;//�Ƿ���������
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isnoHelpView) {//�������Ƶ���Ž���
                    if (x > 0 && x < 96 * ratio_width && y > 704 * ratio_height) {//���ֹͣ������Ƶ��������Ϸ����
                        for (int i = 0; i < 3; i++) {
                            Transform tt = new Transform();
                            tt.origin.set(new Vector3f(STARTBALL[(i) % 3][0], STARTBALL[(i) % 3][1], STARTBALL[(i) % 3][2]));//����λ��
                            ballLst.get((i) % 3).body.setCenterOfMassTransform(tt);
                        }
                        isnoHelpView = false;//��������Ϊ��Ϸ����
                        isnoPlay = true;
                        defen = 1;//�÷ֹ���
                    } else if (x > 384 * ratio_width && y > 704 * ratio_height) {
                        isnoPlay = !isnoPlay;//���Ż�����ͣ��ť��
                    }
                    return true;
                }
                ArrayList<BasketBallForDraw> ballLstt = new ArrayList<BasketBallForDraw>();//װ����������б�
                for (BasketBallForDraw ball : ballLst) {
                    ballLstt.add(ball);
                }
                //��¼���µ�λ��
                touch_x = x;
                touch_y = y;
                float x3d = CHANGJING_WIDTH * touch_x / SCREEN_WIDHT - 0.5f * CHANGJING_WIDTH;
                float y3d = CHANGJING_HEIGHT * (SCREEN_HEIGHT - touch_y) / SCREEN_HEIGHT;
                for (BasketBallForDraw ball : ballLstt) {
                    //��ǰ���λ��
                    float ball_x = ball.body.getWorldTransform(new Transform()).origin.x;
                    float ball_y = ball.body.getWorldTransform(new Transform()).origin.y;
                    float ball_z = ball.body.getWorldTransform(new Transform()).origin.z;
                    float ball_scale = 1.5f * QIU_R;//����İ뾶
                    if (x3d < ball_x + ball_scale && x3d > ball_x - ball_scale &&
                            y3d < ball_y + ball_scale && y3d > ball_y - ball_scale &&
                            ball_z > 1.55f) {
                        curr_ball = ball;
                        break;
                    }
                }
                if (curr_ball == null) {//����������������λ��
                    for (BasketBallForDraw ball : ballLst) {
                        if (!ball.body.wantsSleeping()) {//ֻҪ��һ�������Ǿ�ֹ��
                            isDianjibb = true;//˵����ҾͲ��ܱ䶯�����λ��
                        }
                    }
                }
                if (!isDianjibb && curr_ball == null) {
                    isnoCamear = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                float dx = x - touch_x;//X�����ϵ��ƶ�����
                float max_fingerTouch = 110 * ratio_height;//����������ָ���Ĵ�������
                float dy = (y - touch_y) > 0 ? 0 : ((y - touch_y) < -max_fingerTouch ? -max_fingerTouch : (y - touch_y));//Y�����ϵ��ƶ�����
                isnoCamear = false;//�ӽǿ�ʼ��ع�
                isDianjibb = false;
                if (curr_ball != null)//&&curr_ball.body.wantsSleeping())
                {
                    float vTZ = 1f;
                    Vector3f linearVelocity = curr_ball.body.getLinearVelocity(new Vector3f());
                    if (linearVelocity.x > -vTZ && linearVelocity.x < vTZ &&
                            linearVelocity.y < vTZ && linearVelocity.y > -vTZ &&
                            linearVelocity.z > -vTZ && linearVelocity.z < vTZ) {
                        float vx = dx * 10 / SCREEN_WIDHT;
                        float vy = -dy * 76 * vFactor / SCREEN_HEIGHT;
                        float vz = dy * 28 / SCREEN_HEIGHT;
                        curr_ball.body.activate();
                        curr_ball.body.setLinearVelocity(new Vector3f(vx, vy, vz));//�������ٶ�
                        curr_ball.body.setAngularVelocity(new Vector3f(5, 0, 0));//���ý��ٶ�
                        curr_ball = null;
                    }
                }
                curr_ball = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isnoCamear)//�����Ϊ���ʱ������ֹͣ������Ϊ�����ƶ���Ļ�ı��ӽ�
                {
                    float ddy = y - mPreviousY;//���㴥�ر�Yλ��
                    xAngle += ddy * TOUCH_SCALE_FACTOR;//��λ�Ǹı�
                    if (xAngle < 0)//���ǰ���������С��0,��������ǿ��Ϊ0
                    {
                        xAngle = 0;
                    }
                    if (xAngle > 35)//����������Ǵ���35,����ǿ��Ϊ35;
                    {
                        xAngle = 35;
                    }
                    cx = (float) (tx + Math.cos(Math.toRadians(xAngle)) * Math.sin(Math.toRadians(yAngle)) * DISTANCE);//�����x���
                    cz = (float) (tz + Math.cos(Math.toRadians(xAngle)) * Math.cos(Math.toRadians(yAngle)) * DISTANCE);//�����z���
                    cy = (float) (ty + Math.sin(Math.toRadians(xAngle)) * DISTANCE);//�����y���
                }
                break;
        }
        mPreviousY = y;//��¼���ر�λ��
        mPreviousX = x;//��¼���ر�λ��
        return true;
    }

    public class SceneRenderer implements Renderer {


        //��ʼ��3D�����shader
        public void initShader() {
            ball.initShader(ShaderManager.getShadowshaderProgram());//��
            lanWuang.initShader(ShaderManager.getBasketNetShaderProgram());//����getBasketNetShaderProgram
            backboard.initShader(ShaderManager.getCommTextureShaderProgram());//����
            zj.initShader(ShaderManager.getLigntAndTexturehaderProgram());//֧��
            lankuang.initShader(ShaderManager.getLigntAndTexturehaderProgram());//����
            downPanel.initShader(ShaderManager.getCommTextureShaderProgram());//����
            frontPanel.initShader(ShaderManager.getCommTextureShaderProgram());//����
            leftPanel.initShader(ShaderManager.getCommTextureShaderProgram());//����
            ybb.initShader(ShaderManager.getCommTextureShaderProgram());//�Ǳ��
            shuzi.intShader(ShaderManager.getBlackgroundShaderProgram());//����
            if (isnoHelpView) {
                shipingjimian.initShader(ShaderManager.getCommTextureShaderProgram());//��Ƶ���Ž���
                wenziJuxing.initShader(ShaderManager.getCommTextureShaderProgram());//���־���getBlackgroundShaderProgram
            }

        }

        //��ʼ��3D��ӭ�����shader
        public void initShaderWelcome() {
            wr.intShader(ShaderManager.getCommTextureShaderProgram());
            dot.intShader(ShaderManager.getCommTextureShaderProgram());
        }

        @Override
        public void onDrawFrame(GL10 gl) {

            //�����Ȼ�������ɫ����
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            //����ȼ��
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            if (!hasLoadOk) {
                //���ƻ�ӭ����
                MatrixState.pushMatrix();
                MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 10);
                MatrixState.setCamera(0, 0, 0, 0, 0, -1, 0, 1, 0);

                MatrixState.pushMatrix();
                MatrixState.translate(0, 0, -2);
                MatrixState.rotate(90, 1, 0, 0);
                wr.drawSelf(welcomeid);//���ƻ�ӭ����
                MatrixState.popMatrix();
                drawProcessBar();
                MatrixState.popMatrix();
                if (isFirst) {
                    isFirst = false;
                } else {
                    initTaskReal();
                    curr_process++;
                    if (curr_process > 7) {
                        hasLoadOk = true;
                        start = System.currentTimeMillis();
                    }

                }
            } else if (System.currentTimeMillis() - start > 7000) {
                //���ô˷����������͸��ͶӰ����
                MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
                //���ô˷������������9����λ�þ���
                MatrixState.setCamera(cx, cy, cz, tx, ty, tz, 0, 1, 0);
                MatrixState.pushMatrix();
                drawHouse();//�����������
                //��ʼ����Դλ��
                MatrixState.setLightLocation(3, CHANGJING_HEIGHT * 1.7f, 5);
                drawBasketboard();//��������
                //�������
                GLES20.glEnable(GLES20.GL_BLEND);
                //���û������
                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
                ArrayList<BasketBallForDraw> ballLstt = new ArrayList<BasketBallForDraw>();//װ����������б�
                for (BasketBallForDraw ball : ballLst) {
                    ballLstt.add(ball);
                }
                for (BasketBallForDraw bf : ballLstt) {
                    bf.drawSelf(basketbalolid, 1, 0, 0);//�ذ�Ӱ��
                    bf.drawSelf(basketbalolid, 1, 3, 0);//����Ӱ��
                }
                //�رջ��
                GLES20.glDisable(GLES20.GL_BLEND);
                for (BasketBallForDraw bf : ballLstt) {
                    bf.drawSelf(basketbalolid, 0, 0, 0);//����������
                }
                GLES20.glEnable(GLES20.GL_DEPTH_TEST);


                //�������
                GLES20.glEnable(GLES20.GL_BLEND);
                //���û������
                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
                for (BasketBallForDraw bf : ballLst) {
                    bf.drawSelf(basketbalolid, 1, 4, 1);//������Ӱ��
                }


                //��������
                MatrixState.pushMatrix();
                MatrixState.translate(LANBAN_X, LANBAN_Y - LANQIU_HEIGHT / 6, LANBAN_Z + ZJ_LENGTH + LANKUANG_R);
                MatrixState.translate(0, -LANWANG_H - LANKUANG_JM_R, 0);
                lanWuang.drawSelf(lanwangId,
                        lanWangRaodon
                );
                MatrixState.popMatrix();

                //�رջ��
                GLES20.glDisable(GLES20.GL_BLEND);
                drawDeshBoard();//�����Ǳ���

                MatrixState.popMatrix();
                onDrawShiping();//���Ʋ��Ž���

            } else {
                curr_process++;
                MatrixState.pushMatrix();
                MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 10);
                MatrixState.setCamera(0, 0, 0, 0, 0, -1, 0, 1, 0);
                MatrixState.pushMatrix();
                MatrixState.translate(0, 0, -2);
                MatrixState.rotate(90, 1, 0, 0);
                wr.drawSelf(welcomeid);
                MatrixState.popMatrix();
                drawProcessBar();
                MatrixState.popMatrix();
                //���﷢���һ����
                if (!isStart && System.currentTimeMillis() - start > 6890) {
                    isStart = true;

                    father.curr = WhichView.GAME_VIEW;
                    ArrayList<BasketBallForDraw> ballLstt = new ArrayList<BasketBallForDraw>();//װ����������б�
                    for (BasketBallForDraw ball : ballLst) {
                        ballLstt.add(ball);
                    }
                    if (isnoHelpView) {
                        float vx = STARTBALL_V[0][0];//*10/SCREEN_WIDHT;
                        float vy = STARTBALL_V[0][1];
                        float vz = STARTBALL_V[0][2];
                        ballLstt.get(0).body.activate();
                        ballLstt.get(0).body.setLinearVelocity(new Vector3f(vx, vy, vz));//�������ٶ�
                        ballLstt.get(0).body.setAngularVelocity(new Vector3f(5, 0, 0));//���ý��ٶ�
                        startY = 0;
                        new Thread() {
                            int array_id = 1;//����ڼ�����
                            boolean isnoFashe = false;//�Ƿ���Խ����ֵĶ���

                            @Override
                            public void run() {
                                while (isnoHelpView) {
                                    if (!isnoPlay) {//�������ͣ����
                                        continue;
                                    }
                                    ArrayList<BasketBallForDraw> ballLstt = new ArrayList<BasketBallForDraw>();//װ����������б�
                                    for (BasketBallForDraw ball : ballLst) {
                                        ballLstt.add(ball);
                                    }
                                    startY -= 2;
                                    startY %= 1100;//����y���
                                    array_id %= 3;
                                    shipingJs += 100;
                                    if (!isnoFashe && (shipingJs) % 5000 == 4000) {
                                        Transform tt = new Transform();
                                        tt.origin.set(new Vector3f(STARTBALL[(array_id + 2) % 3][0], STARTBALL[(array_id + 2) % 3][1], STARTBALL[(array_id + 2) % 3][2]));//����λ��
                                        ballLstt.get((array_id + 2) % 3).body.setCenterOfMassTransform(tt);
                                        shouX = STARTBALL[array_id][0] / 2;//ballLst.get(array_id).body.getMotionState().getWorldTransform(new Transform()).origin.x/2;
                                        shouY = -0.9f;
                                        isnoFashe = true;
                                    }
                                    if (isnoFashe) {
                                        if (array_id % 3 == 0) {
                                            shouX += 0.03f;
                                        } else if (array_id % 3 == 2) {
                                            shouX -= 0.03f;
                                        }
                                        shouY += 0.1f;
                                        if (shouY > 1.2f) {
                                            shouY = 5f;
                                        }
                                    } else {
                                        shouY = 5f;
                                    }
                                    if ((shipingJs) % 5000 == 0) {
                                        isnoFashe = false;//������Ϻ�
                                        shouY = 4;
                                        float vx = STARTBALL_V[array_id][0];//*10/SCREEN_WIDHT;
                                        float vy = STARTBALL_V[array_id][1];
                                        float vz = STARTBALL_V[array_id][2];
                                        ballLstt.get(array_id).body.activate();
                                        ballLstt.get(array_id).body.setLinearVelocity(new Vector3f(vx, vy, vz));//�������ٶ�
                                        ballLstt.get(array_id).body.setAngularVelocity(new Vector3f(5, 0, 0));//���ý��ٶ�
                                        array_id++;
                                    }
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }.start();
                    } else {
                        float vx = 0;//*10/SCREEN_WIDHT;
                        float vy = 10.1f * vFactor;
                        float vz = -3.0f;
                        ballLstt.get(1).body.activate();
                        ballLstt.get(1).body.setLinearVelocity(new Vector3f(vx, vy, vz));//�������ٶ�
                        ballLstt.get(1).body.setAngularVelocity(new Vector3f(5, 0, 0));//���ý��ٶ�
                    }
                }
            }
        }

        //������Ƶ�����ϵĸ�����ť
        public void onDrawShiping() {
            if (!isnoHelpView) {//�������Ƶ���Ž���Ͳ�����
                return;
            }

            if (wenziId != -1) {
                GLES20.glDeleteTextures(1, new int[]{wenziId}, 0);
            }

            //�����������
            Bitmap bm = Constant.generateWLT(Constant.content, wenziwidth, wenziHeight);
            wenziId = initTexture(bm, true);


            MatrixState.pushMatrix();//���Ʊ���
            MatrixState.setProjectOrtho(-1, 1, -1, 1, 1, 10);
            MatrixState.setCamera(0, 0, 0, 0, 0, -1, 0, 1, 0);//�ָ�����

            //�������
            GLES20.glEnable(GLES20.GL_BLEND);
            //���û������
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);


            MatrixState.pushMatrix();
            MatrixState.translate(0, -0.05f, -2f);
            MatrixState.rotate(90, 1, 0, 0);
            //���ƾ���
            wenziJuxing.drawSelf(wenziId);  //�������ְ�ť
            MatrixState.popMatrix();


            MatrixState.pushMatrix();
            MatrixState.translate(-0.8f, -0.88f, -2f);
            MatrixState.rotate(90, 1, 0, 0);
            //���ƾ���
            shipingjimian.drawSelf(stopId);  //����ֹͣ��ť 
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.8f, -0.88f, -2f);
            MatrixState.rotate(90, 1, 0, 0);
            if (isnoPlay) {//����ڲ����л�����ͣ��ť
                shipingjimian.drawSelf(pauseId);  //������ͣ��ť
            } else {
                shipingjimian.drawSelf(playId);  //���Ʋ��Ű�ť
            }
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(shouX, shouY, -1.8f);
            MatrixState.rotate(90, 1, 0, 0);
            //���ƾ���
            shipingjimian.drawSelf(shouId);  //�����ְ�ť 
            MatrixState.popMatrix();

            //�رջ��
            GLES20.glDisable(GLES20.GL_BLEND);

            MatrixState.popMatrix();

        }

        //���ƽ����
        public void drawProcessBar() {
            float height = 0f;
            GLES20.glEnable(GLES20.GL_BLEND);
            //���û������
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            MatrixState.pushMatrix();
            MatrixState.translate(0f, height, -1.8f);
            MatrixState.rotate(90, 1, 0, 0);
            MatrixState.rotate(-curr_process * 10, 0, 1, 0);
            dot.drawSelf(dotId);
            MatrixState.popMatrix();
            GLES20.glDisable(GLES20.GL_BLEND);
        }

        //��������
        public void drawBasketboard() {
            //�������
            GLES20.glEnable(GLES20.GL_BLEND);
            //���û������
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            MatrixState.pushMatrix();
            MatrixState.translate(LANBAN_X, LANBAN_Y, LANBAN_Z - 0.03f);
            backboard.drawSelf(lanbanId);//��������       
            MatrixState.popMatrix();
            //�رջ��
            GLES20.glDisable(GLES20.GL_BLEND);

            MatrixState.pushMatrix();
            MatrixState.translate(LANBAN_X - LANBAN_BILIXISHU, LANBAN_Y - LANQIU_HEIGHT / 6, LANBAN_Z + ZJ_LENGTH / 2);
            MatrixState.rotate(90, 0, 1, 0);
            zj.drawSelf();//�������֧��
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(LANBAN_X + LANBAN_BILIXISHU, LANBAN_Y - LANQIU_HEIGHT / 6, LANBAN_Z + ZJ_LENGTH / 2);
            MatrixState.rotate(90, 0, 1, 0);
            zj.drawSelf();//�����ұ�֧��
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(LANBAN_X, LANBAN_Y - LANQIU_HEIGHT / 6, LANBAN_Z + ZJ_LENGTH + LANKUANG_R);
            lankuang.drawSelf();//����

            MatrixState.popMatrix();
        }

        //�����Ǳ��
        public void drawDeshBoard() {
            MatrixState.pushMatrix();//���Ʊ���
            MatrixState.setProjectOrtho(-1, 1, -1, 1, 1, 10);
            MatrixState.setCamera(0, 0, 0, 0, 0, -1, 0, 1, 0);//�ָ�����

            MatrixState.pushMatrix();
            MatrixState.translate(0, 1 - YBB_HEIGHT / 2, -2f);
            MatrixState.rotate(90, 1, 0, 0);
            //���ƾ���
            ybb.drawSelf(shijianxiansBeijingId);  //���Ʊ���   
            MatrixState.popMatrix();
            //�������
            GLES20.glEnable(GLES20.GL_BLEND);
            //���û������
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

            MatrixState.pushMatrix();
            MatrixState.translate(-0.65f, 1 - YBB_HEIGHT / 2 - 0.03f, -1.5f);
            //���ƾ���
            shuzi.drawSelf(defen, shuziId);     //���ƽ������
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0.6f, 1 - YBB_HEIGHT / 2 - 0.03f, -1.5f);
            //���ƾ���
            shuzi.drawSelf(daojishi - (int) (deadtimesMS / 1000), shuziId);  //����ʱ��
            MatrixState.popMatrix();
            //�رջ��
            GLES20.glDisable(GLES20.GL_BLEND);
            MatrixState.popMatrix();
        }

        //�����������
        public void drawHouse() {
            //�����ֳ�
            MatrixState.pushMatrix();
            //���ƾ���
            downPanel.drawSelf(dibanTexId);  //���Ƶذ�        
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(0, CHANGJING_HEIGHT, 0);
            //����ת��
            MatrixState.rotate(180, 0, 0, 1);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            //����ת��
            MatrixState.rotate(90, 1, 0, 0);
            MatrixState.rotate(-90, 0, 0, 1);
            MatrixState.translate(0, -CHANGJING_WIDTH / 2, -CHANGJING_HEIGHT / 2);
            //���ƾ���
            leftPanel.drawSelf(zuobianQiangID);  //�������ǽ     
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            //����ת��
            MatrixState.rotate(90, 1, 0, 0);
            MatrixState.rotate(90, 0, 0, 1);
            MatrixState.translate(0, -CHANGJING_WIDTH / 2, -CHANGJING_HEIGHT / 2);
            //���ƾ���
            leftPanel.drawSelf(youbianQiangID);  //�����ұ�ǽ     
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            //����ת��
            MatrixState.rotate(90, 1, 0, 0);
            MatrixState.translate(0, -CHANGJING_LENGTH / 2, -CHANGJING_HEIGHT / 2);
            //���ƾ���
            frontPanel.drawSelf(houmianQiangID);  //�����ұ�ǽ     
            MatrixState.popMatrix();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //�����Ӵ���С��λ��
            GLES20.glViewport(0, 0, width, height);
            //����GLSurfaceView�Ŀ�߱�
            ratio = (float) width / height;
            //�򿪱������
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            //��ʼ������
            if (isnoHelpView) {
                welcomeid = initTexture(Constant.welcome2, false);//���ؽ��������
            } else {
                welcomeid = initTexture(Constant.welcome, false);//���ؽ��������
            }

            dotId = initTexture(Constant.dot, false);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //������Ļ����ɫRGBA
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            //��ʼ����Դλ��
            MatrixState.setLightLocation(3, 7, 5);
            //����ȼ��
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //��ʼ���任����
            MatrixState.setInitStack();
            ShaderManager.compileShader();//�������3D�����л�ӭ��ӭ�����е�shader
            initShaderWelcome();//��ʼ����ӭ�����shader
        }

        public void initTaskReal() {
            initBitmap();
            if (curr_process == 2) {
                defen = 0;//��ԭ�÷�
                daojishi = 60;//��ԭ����ʱ
                SHENGYING_FLAG = SOUND_MEMORY;//��ԭ����ѡ��
                DEADTIME_FLAG = true;//��������ʱ
                initObject(GLGameView.this.getResources());
                ShaderManager.compileShaderReal();
            }
            if (curr_process == 3) {
                initShader();
            }
            if (curr_process == 4) {
                dibanTexId = initTexture(bm_floor, true);//�ذ�����
                zuobianQiangID = initTexture(bm_swall1, true);//��ǽ����
                youbianQiangID = initTexture(bm_swall3, true);//��ǽ����
                houmianQiangID = initTexture(bm_swall2, true);//��ǽ����
                basketbalolid = initTexture(bm_basketball, true);//��������
                lanbanId = initTexture(bm_lanban2, true);//��������
                shijianxiansBeijingId = initTexture(bm_yibiaoban, true);//����ʱ�����ʾ����
                shuziId = initTexture(bm_number, true);    //����bm_basketnet
                lanwangId = initTexture(bm_basketnet, true);//��������

                if (isnoHelpView) {
                    shouId = initTexture(bm_shou, true);
                    ;//ץ
                    stopId = initTexture(bm_stop, true);
                    ;//ֹͣ��ť
                    pauseId = initTexture(bm_pause, true);
                    ;//��ͣ��ť
                    playId = initTexture(bm_play, true);
                    ;//���Ű�ť
                }


            }


        }
    }

    public void initObjectWelcome(Resources r)//������ӭ������������
    {
        wr = new HuanYingJieMianJuXing(SCREEN_WIDHT / SCREEN_HEIGHT * 2, 2);
        dot = new HuanYingJieMianJuXing(0.4f, 0.4f);
    }

    public void initObject(Resources r)//��ʼ�����󷽷�
    {
        initWorld();//��ʼ����������
        lankuang = MoXingJiaZai.loadFromFileVertexOnly("lankuang.obj", r);//����
        ball = new BasketBallTextureByVertex(QIU_R);//������
        lanWuang = new LanWang(LANKUANG_R, 0.55f * LANKUANG_R, LANWANG_H, 8);//��������
        downPanel = new WenLiJuXing(CHANGJING_WIDTH, CHANGJING_LENGTH + 1);//��
        frontPanel = new WenLiJuXing(CHANGJING_WIDTH, CHANGJING_HEIGHT);//ǰ
        leftPanel = new WenLiJuXing(CHANGJING_LENGTH, CHANGJING_HEIGHT);//��
        backboard = new LanBan//��������
                (
                        LANBAN_BILIXISHU / 2,
                        36 * LANBAN_BILIXISHU, 21 * LANBAN_BILIXISHU, r
                );
        zj = new Yuanzhu(ZJ_LENGTH, ZJ_R, 30, 1, r);//����֧��
        shuzi = new HuiZhiShuZi(r);//��������
        ybb = new WenLiJuXing(YBB_WIDTH, YBB_HEIGHT);//�����Ǳ��
        ballLst.clear();
        ballLst.add
                (
                        new BasketBallForDraw(ball, basketballShape, shijie, 1,
                                STARTBALL_1[0], STARTBALL_1[1], STARTBALL_1[2], GROUP_BALL1, MASK_BALL1)
                );
        ballLst.add
                (
                        new BasketBallForDraw(ball, basketballShape, shijie, 1,
                                STARTBALL_2[0], STARTBALL_2[1], STARTBALL_2[2], GROUP_BALL2, MASK_BALL2)
                );
        ballLst.add
                (
                        new BasketBallForDraw(ball, basketballShape, shijie, 1,
                                STARTBALL_3[0], STARTBALL_3[1], STARTBALL_3[2], GROUP_BALL3, MASK_BALL3)
                );
        planeS = new TianjiaBody[]
                {
                        new TianjiaBody(pingmian[0], shijie, 1, 0, 0, 0, 1, 1),//����
//	        new TianjiaBody(pingmian[1], shijie, 1, 0, CHANGJING_HEIGHT*3/2, 0,1,1),//�ݶ�
                        new TianjiaBody(pingmian[1], shijie, 1, 0, CHANGJING_HEIGHT / 2, CHANGJING_LENGTH / 2 + QIU_R * 2, 0, 0),//ǰ��
                        new TianjiaBody(pingmian[2], shijie, 1, 0, CHANGJING_HEIGHT / 2, -CHANGJING_LENGTH / 2, 1, 1),//����
                        new TianjiaBody(pingmian[3], shijie, 1, -CHANGJING_WIDTH / 2, CHANGJING_HEIGHT / 2, 0, 1, 1),//����
                        new TianjiaBody(pingmian[4], shijie, 1, CHANGJING_WIDTH / 2, CHANGJING_HEIGHT / 2, 0, 1, 1),//����

                        new TianjiaBody(lifangti, shijie, 1,
                                LANBAN_X, LANBAN_Y, LANBAN_Z, 1, 1),//������ӽ���������
                };

        for (int i = 0; i < jiaolanggeshu; i++) {//����������
            langquanJiaonang[i] = new JiaoNangTianjiaBody(lanquanjiaonang, shijie, 0,
                    LANBAN_X + LANKUANG_R * (float) (Math.cos(Math.toRadians(i * 360 / jiaolanggeshu))),
                    LANBAN_Y - LANQIU_HEIGHT / 6,
                    LANBAN_Z + ZJ_LENGTH + LANKUANG_R + LANKUANG_R * (float) (Math.sin(Math.toRadians(i * 360 / jiaolanggeshu))) + ZJ_R,
                    1, 1, 0, -(360 / jiaolanggeshu / 2) - 360 / jiaolanggeshu * i, 0);
        }
        if (isnoHelpView) {
            shipingjimian = new WenLiJuXing(0.3f, 0.18f);//������Ƶ���Ž��水ť����
            wenziJuxing = new WenLiJuXing(1.8f, 1.45f);//�������־���
        }


        new Thread() {
            public void run() {
                while (flag) {
                    try {
                        if (isnoHelpView && !isnoPlay) {//����ǲ�����Ƶ���棬Ϊֹͣ�����򲻽�������ģ��
                            continue;
                        }
                        shijie.stepSimulation(1f / 60.f, 5);
                        ballControlUtil();//�ж������Ƿ����
                        if (!isnoHelpView) {//�������Ƶ���Ž��棬ʱ��Ÿı�
                            deadtimesMS += 10;
                        }

                        if (deadtimesMS >= daojishi * 1000) {
                            SQLiteUtil.insertTime(Constant.defen);//�������¼����ݿ�
                            flag = false;//�ֳ�ֹͣ
                            father.shengyinBoFang(2, 0);//������Ϸ��������
                            father.xiaoxichuli.sendEmptyMessage(JIESHU_JIEMIAN);
                        }

                        if (xAngle > 0 && !isnoCamear) {
                            xAngle -= CAMERA_Y_SK_FH;
                            cx = (float) (tx + Math.cos(Math.toRadians(xAngle)) * Math.sin(Math.toRadians(yAngle)) * DISTANCE);//�����x���
                            cz = (float) (tz + Math.cos(Math.toRadians(xAngle)) * Math.cos(Math.toRadians(yAngle)) * DISTANCE);//�����z���
                            cy = (float) (ty + Math.sin(Math.toRadians(xAngle)) * DISTANCE);//�����y���
                        }
                        if (xAngle < 0 && !isnoCamear) {
                            xAngle += CAMERA_Y_SK_FH;
                            cx = (float) (tx + Math.cos(Math.toRadians(xAngle)) * Math.sin(Math.toRadians(yAngle)) * DISTANCE);//�����x���
                            cz = (float) (tz + Math.cos(Math.toRadians(xAngle)) * Math.cos(Math.toRadians(yAngle)) * DISTANCE);//�����z���
                            cy = (float) (ty + Math.sin(Math.toRadians(xAngle)) * DISTANCE);//�����y���
                        }
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void ballControlUtil()//������Ҫ�����ڶ�����������沿�ֽ��в���
    {
        for (BasketBallForDraw bf : ballLst) {
            //����������˶���,��ôÿһ�θ�ذ���ײ��ʱ�����������һ����Z������ĳ���
            if (bf.body.isActive() && SYSUtil.isCollided(shijie, bf.body, planeS[0].gangti)) {
                bf.body.applyForce(new Vector3f(0, 0, 60), new Vector3f(0, 0, 0));
            }
            //��ȡ������˶����
            Transform transform = bf.body.getMotionState().getWorldTransform(new Transform());
            //��ȡ��������������е�λ��
            float position_Y = transform.origin.y;
            float position_X = transform.origin.x;
            float position_Z = transform.origin.z;
            //��ȡ������ٶȺ���ת
            Vector3f linearVelocity = bf.body.getLinearVelocity(new Vector3f());
            Vector3f angularVelocity = bf.body.getAngularVelocity(new Vector3f());
            float linearVelocityDomain = 0.08f;//�����ٶ���ֵ
            float angularVelocityDomain = 1.45f;//���ٶ���ֵ
            //�ܵ����ٶȺ��ܵĽ��ٶ�
            float allLinearV = Math.abs(linearVelocity.x) + Math.abs(linearVelocity.y) + Math.abs(linearVelocity.z);
            float allAngularV = Math.abs(angularVelocity.x) + Math.abs(angularVelocity.y) + Math.abs(angularVelocity.z);
            //������λ����ǰ��ʱ,�����侲ֹ����ֵ
            if (bf.body.isActive() && position_Y < STARTBALL_3[1] + 0.05) {
                bf.body.setLinearVelocity(new Vector3f(0, linearVelocity.y, linearVelocity.z));
            }
            if (bf.body.isActive() && allLinearV < linearVelocityDomain && allAngularV < angularVelocityDomain && position_Y < STARTBALL_3[1] + 0.05) {
                bf.body.setActivationState(CollisionObject.WANTS_DEACTIVATION);
            }
            //���������Ľ����д���
            //�����ȡ���������λ�����ֵ
            float lankuang_X = LANBAN_X;
            float lankuang_Y = LANBAN_Y - LANQIU_HEIGHT / 4;
            float lankuang_Z = LANBAN_Z + ZJ_LENGTH + LANKUANG_R;
            float lankuang_Radius = LANKUANG_R;
            float ball_Radius = QIU_R;
            //�������պ�λ��������
            float temp_distance = (float) Math.sqrt((position_X - lankuang_X) * (position_X - lankuang_X) + (position_Z - lankuang_Z) * (position_Z - lankuang_Z));
            if (linearVelocity.y < 0 && temp_distance < (lankuang_Radius - ball_Radius) && position_Y > lankuang_Y) {
                bf.ball_State = 1;
            }
            if (bf.ball_State == 1 && position_Y < lankuang_Y &&
                    position_X > lankuang_X - LANKUANG_R && position_X < lankuang_X + LANKUANG_R &&
                    position_Z > lankuang_Z - LANKUANG_R && position_Z < lankuang_Z + LANKUANG_R
                    ) {
                Constant.defen++;//�÷����һŶ
                new Thread() {
                    @Override
                    public void run() {
                        int lanWang = 0;
                        int zTime = 0;
                        while (zTime < 360) {
                            lanWang++;
                            lanWangRaodon = lanWang % 4;
                            zTime += 60;
                            try {
                                Thread.sleep(60);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        lanWangRaodon = 0;
                    }
                }.start();
                bf.ball_State = 0;
                father.shengyinBoFang(3, 1);//���Ž�������
            }
            Vector3f vt;
            vt = bf.body.getLinearVelocity(new Vector3f());
            if ((vt.y > 1f && vt.y < 6.5f)//�ж��Ƿ�͵���ײ
                    && SYSUtil.isCollided(shijie, bf.body, planeS[0].gangti)) {
                father.shengyinBoFang(1, 0);
            } else if
                    (
                    vt.x > 1f &&
                            SYSUtil.isCollided(shijie, bf.body, planeS[3].gangti)
                    ) {//����
                father.shengyinBoFang(1, 0);
            } else if
                    (vt.x < -1 &&
                            SYSUtil.isCollided(shijie, bf.body, planeS[4].gangti)
                    ) {//����
                father.shengyinBoFang(1, 0);
            } else if
                    (
                    vt.z > 1 &&
                            SYSUtil.isCollided(shijie, bf.body, planeS[2].gangti)
                    ) {//����
                father.shengyinBoFang(1, 0);
            }
            if (SYSUtil.isCollided(shijie, bf.body, planeS[5].gangti))//����
            {
                if ((Math.abs(vt.x) + Math.abs(vt.y) + Math.abs(vt.z)) > 2) {
                    father.shengyinBoFang(1, 0);
                }
                bf.isnoLanBan = 1;
            } else {
                bf.isnoLanBan = 0;
            }

        }
    }

    public void initBitmap()//������Ҫ�����ڽ�ͼƬ���س�Bitmap
    {
        if (curr_process == 0) {
            bm_floor = loadTexture(R.drawable.floor);
            bm_swall1 = loadTexture(R.drawable.swall1);
            bm_swall3 = loadTexture(R.drawable.swall3);
            bm_swall2 = loadTexture(R.drawable.swall2);
            bm_basketball = loadTexture(R.drawable.basketball);
            bm_lanban2 = loadTexture(R.drawable.lanban);
        }
        if (curr_process == 1) {

            bm_yibiaoban = loadTexture(R.drawable.yibiaoban);
            bm_number = loadTexture(R.drawable.number);
            bm_basketnet = loadTexture(R.drawable.basketnet);
            if (isnoHelpView) {
                bm_shou = loadTexture(R.drawable.shou);//ץ
                bm_stop = loadTexture(R.drawable.stop);//ֹͣ������Ƶ��ť
                bm_pause = loadTexture(R.drawable.pause);//��ͣ��ť
                bm_play = loadTexture(R.drawable.play);//���Ű�ť

            }
        }
    }

    //ͨ��IO����ͼƬ
    public Bitmap loadTexture(int drawableId) {
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmapTmp;
    }

    public int initTexture(Bitmap bitmapTmp, boolean needRrelease) {
        //�������ID
        int[] textures = new int[1];
        GLES20.glGenTextures
                (
                        1,          //���������id������
                        textures,   //����id������
                        0           //ƫ����
                );
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D
                (
                        GLES20.GL_TEXTURE_2D, //��������
                        0,
                        GLUtils.getInternalFormat(bitmapTmp),
                        bitmapTmp, //����ͼ��
                        GLUtils.getType(bitmapTmp),
                        0 //����߿�ߴ�
                );

        if (needRrelease) {
            bitmapTmp.recycle(); //������سɹ����ͷ�ͼƬ
        }
        return textureId;
    }
}
