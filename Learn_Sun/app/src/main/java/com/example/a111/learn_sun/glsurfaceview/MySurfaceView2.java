package com.example.a111.learn_sun.glsurfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.a111.learn_sun.R;
import com.example.a111.learn_sun.cardboard2.sensor.HeadTracker;
import com.example.a111.learn_sun.util.Constant;
import com.example.a111.learn_sun.util.MatrixState;
import com.example.a111.learn_sun.util.MemUtil;
import com.example.a111.learn_sun.util.ShaderUtil;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.Matrix.perspectiveM;

public class MySurfaceView2 extends GLSurfaceView {

    private float[] projectionMatrix = new float[16];
    private MyRenderer mRenderer;
    private float mPreviousY;
    private float mPreviousX;
    public final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private Ball ball;
    private HeadTracker mHeadTracker;
    private float[] headView = new float[16];
    private int textureId;
    private int wallTextureId, roofTextureId, floorTextureId;

    public MySurfaceView2(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        mRenderer = new MyRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        mHeadTracker = HeadTracker.createFromContext(context);
        Matrix.setIdentityM(headView, 0);
    }

    public MySurfaceView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHeadTracker.startTracking();
    }

    @Override
    public void onPause() {
        super.onPause();
        mHeadTracker.stopTracking();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;
                float dx = x - mPreviousX;
                ball.yAngle += dx * TOUCH_SCALE_FACTOR;
                ball.xAngle += dy * TOUCH_SCALE_FACTOR;
        }
        mPreviousY = y;
        mPreviousX = x;
        return true;
    }

    public int initTexture(int drawableId)//textureId
    {
        //生成纹理ID
        int[] textures = new int[1];
        GLES20.glGenTextures
                (
                        1,          //产生的纹理id的数量
                        textures,   //纹理id的数组
                        0           //偏移量
                );
        textureId = textures[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);  //绑定纹理

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        Bitmap bitmapTmp = BitmapFactory.decodeResource(getResources(), drawableId);

        //实际加载纹理
        GLUtils.texImage2D
                (
                        GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
                        0,                      //纹理的层次，0表示基本图像层，可以理解为直接贴图
                        bitmapTmp,              //纹理图像
                        0                       //纹理边框尺寸
                );
        bitmapTmp.recycle();          //纹理加载成功后释放图片
        return textureId;
    }

    class MyRenderer implements Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0f, 0f, 0f, 1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            ball = new Ball(MySurfaceView2.this);
            floorTextureId = initTexture(R.drawable.robot);
            wallTextureId = initTexture(R.drawable.wall);
            roofTextureId = initTexture(R.drawable.roof);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            Constant.ratio = (float) width / height;
            perspectiveM(projectionMatrix, 0, 75f, width / height, 0.1f, 300.0f);
            MatrixState.setProject(projectionMatrix);
            //MatrixState.setProjectOrtho(-Constant.ratio,Constant.ratio,-1,1,0.1f,100f);
            //MatrixState.setProjectFrustum(-Constant.ratio, Constant.ratio, -1, 1, 0.1f, 100f);
            MatrixState.setCamera(0f, 0f, 2f, 0f, 0f, 0.0f, 0.0f, 0.1f, 0.0f);
            MatrixState.setInitStack();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //mHeadTracker.getLastHeadView(headView, 0);
            MatrixState.setHeadView(headView);
            MatrixState.pushMatrix();
            ball.drawSelf(floorTextureId, wallTextureId, roofTextureId);
            MatrixState.popMatrix();
        }
    }

    public class Ball {

        final float SCALSIZE = 100.0f;


        float vertices2[] = new float[]{
                -400.0f / SCALSIZE, -300.0f / SCALSIZE, -150.0f / SCALSIZE,  //0
                410.0f / SCALSIZE, -300.0f / SCALSIZE, -150.0f / SCALSIZE,    //1
                410.0f / SCALSIZE, 100.0f / SCALSIZE, -150.0f / SCALSIZE,     //2
                215.0f / SCALSIZE, 100.0f / SCALSIZE, -150.0f / SCALSIZE, //3
                215.0f / SCALSIZE, 325.0f / SCALSIZE, -150.0f / SCALSIZE, //4
                75.0f / SCALSIZE, 325.0f / SCALSIZE, -150.0f / SCALSIZE, //5
                75.0f / SCALSIZE, 30.0f / SCALSIZE, -150.0f / SCALSIZE, //6
                -400.0f / SCALSIZE, 30.0f / SCALSIZE, -150.0f / SCALSIZE, //7
                410.0f / SCALSIZE, 30.0f / SCALSIZE, -150.0f / SCALSIZE, //8
                75.0f / SCALSIZE, 100.0f / SCALSIZE, -150.0f / SCALSIZE, //9

                -400.0f / SCALSIZE, -300.0f / SCALSIZE, 150.0f / SCALSIZE, //10
                410.0f / SCALSIZE, -300.0f / SCALSIZE, 150.0f / SCALSIZE, //11
                410.0f / SCALSIZE, 100.0f / SCALSIZE, 150.0f / SCALSIZE, //12
                215.0f / SCALSIZE, 100.0f / SCALSIZE, 150.0f / SCALSIZE, //13
                215.0f / SCALSIZE, 325.0f / SCALSIZE, 150.0f / SCALSIZE, //14
                75.0f / SCALSIZE, 325.0f / SCALSIZE, 150.0f / SCALSIZE, //15
                75.0f / SCALSIZE, 30.0f / SCALSIZE, 150.0f / SCALSIZE, //16
                -400.0f / SCALSIZE, 30.0f / SCALSIZE, 150.0f / SCALSIZE, //17
                410.0f / SCALSIZE, 30.0f / SCALSIZE, 150.0f / SCALSIZE, //18
                75.0f / SCALSIZE, 100.0f / SCALSIZE, 150.0f / SCALSIZE, //19
        };

        float texCoor[] = new float[]
                {
                        //地板
                        0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,

                        //墙面

                        0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,

                        0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,

                        0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,

                        0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,

                        0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,

                        0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,

                        0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,

                        0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f,

                        //天花板
                        0.0f, 0.0f, 4.0f, 4.0f, 0.0f, 4.0f,
                        0.0f, 0.0f, 4.0f, 0.0f, 4.0f, 4.0f,
                        1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f,
                };

        short index[] = new short[]{

                //地面
                7, 0, 1,
                1, 8, 7,
                6, 8, 2,
                6, 2, 9,
                9, 3, 4,
                9, 4, 5,

                //墙面
                1, 0, 10,
                1, 10, 11,
                2, 1, 11,
                2, 11, 12,
                3, 2, 12,
                3, 12, 13,
                4, 3, 13,
                4, 13, 14,
                5, 4, 14,
                5, 14, 15,
                6, 5, 15,
                6, 15, 16,
                7, 6, 16,
                7, 16, 17,
                0, 7, 17,
                0, 17, 10,

                //房顶
                17, 11, 10,
                17, 18, 11,
                16, 12, 18,
                16, 19, 12,
                19, 14, 13,
                19, 15, 14,
        };

        int mProgram;// 自定义渲染管线着色器程序id
        int muMVPMatrixHandle;// 总变换矩阵引用
        int maPositionHandle; // 顶点位置属性引用
        int maTexCoorHandle;
        int mFloorHandle, mRoofHandle, mWallHandle;
        String mVertexShader;// 顶点着色器
        String mFragmentShader;// 片元着色器

        FloatBuffer mVertexBuffer2;
        ShortBuffer mIndexBuffer;
        FloatBuffer mTexCoorBuffer;

        float yAngle = 0;// 绕y轴旋转的角度
        float xAngle = 0;// 绕x轴旋转的角度
        float zAngle = 0;// 绕z轴旋转的角度
        float r = 0.8f;

        public Ball(MySurfaceView2 mv) {
            initVertexData();
            initShader(mv);
        }

        // 初始化顶点坐标数据的方法
        public void initVertexData() {

            float vertices[] = new float[index.length];

            for (int i = 0; i < index.length*3; i++) {
                short j = (short) (index[i]*3);

                vertices[i] = vertices2[j*3];
                //vertices[i+1] =vertices2[]
            }

            for (int i = 0; i < index.length; i++) {
                Log.i("abc", "initVertexData: "+i+":"+vertices[i]);
            }
            mIndexBuffer = MemUtil.makeShortBuffer(index);
            mVertexBuffer2 = MemUtil.makeFloatBuffer(vertices);
            mTexCoorBuffer = MemUtil.makeFloatBuffer(texCoor);
        }

        // 初始化shader
        public void initShader(MySurfaceView2 mv) {
            mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
            mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
            mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
            maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
            maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
            muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

            mRoofHandle = GLES20.glGetUniformLocation(mProgram, "sTextureRoof");
            mWallHandle = GLES20.glGetUniformLocation(mProgram, "sTextureWall");
            mFloorHandle = GLES20.glGetUniformLocation(mProgram, "sTextureFloor");
        }

        public void drawSelf(int floorTextureId, int wallTextureId, int roofTextureId) {

            MatrixState.rotate(xAngle, 1, 0, 0);//绕X轴转动
            MatrixState.rotate(yAngle, 0, 1, 0);//绕Y轴转动
            MatrixState.rotate(zAngle, 0, 0, 1);//绕Z轴转动
            MatrixState.scale(50.0f, 50.0f, 50.0f);
            MatrixState.rotate(90.0f, 1.0f, 0.0f, 0.0f);
            GLES20.glUseProgram(mProgram);
            GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer2);
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mTexCoorBuffer);
            GLES20.glEnableVertexAttribArray(maTexCoorHandle);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, floorTextureId);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, wallTextureId);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, roofTextureId);
            GLES20.glUniform1i(mFloorHandle, 0);
            GLES20.glUniform1i(mWallHandle, 1);
            GLES20.glUniform1i(mRoofHandle, 2);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,index.length);
            //GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
        }
    }
}
