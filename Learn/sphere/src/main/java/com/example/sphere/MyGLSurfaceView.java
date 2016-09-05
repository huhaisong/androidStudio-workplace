package com.example.sphere;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.sphere.cardboard2.sensor.HeadTracker;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.Matrix.perspectiveM;
import static com.example.sphere.ShaderUtil.createProgram;

public class MyGLSurfaceView extends GLSurfaceView {
    private HeadTracker mHeadTracker;
    private MyRenderer mRenderer;
    int textureId;  //系统分配的纹理id
    private float[] mHeadView = new float[16];
    private boolean isSended = false;

    public MyGLSurfaceView(Context context) {
        super(context);

        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new MyRenderer();       //创建场景渲染器
        setRenderer(mRenderer);             //设置渲染器
        mHeadTracker = HeadTracker.createFromContext(context);
        Matrix.setIdentityM(mHeadView, 0);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        //setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    public void onPause() {
        super.onPause();
        mHeadTracker.stopTracking();
    }

    public void onResume() {
        super.onResume();
        mHeadTracker.startTracking();
        this.requestRender();

    /*    new Thread(new Runnable() {
            float[] newheadView = new float[16];
            float[] oldheadView = new float[16];
            boolean b = false;
            @Override
            public void run() {

                while (true) {
                    b = false;
                    try {
                        Thread.currentThread().sleep(16);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHeadTracker.getLastHeadView(newheadView, 0);
                    for (int i = 0; i < 16; i++) {
                        float d = newheadView[i] - oldheadView[i];
                        if (Math.abs(d) > 0.01) {
                            Log.i("aaaaa", "run: Math.abs(d):" + Math.abs(d) );
                            b = true;
                            break;
                        }
                    }
                    if (b){
                        MyGLSurfaceView.this.requestRender();
                        for (int i = 0; i < 16; i++) {
                            oldheadView[i] = newheadView[i];
                        }
                    }
                }
            }
        }).start();*/
    }

    public void initTexture()//textureId
    {
        //生成纹理ID
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        textureId = textures[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);  //绑定纹理

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        InputStream inputStream = null;
        Bitmap bitmapTmp = null;
        try {
            inputStream = getResources().getAssets().open("bg.jpg");
            //获得图片的宽、高
            BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
            tmpOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, tmpOptions);
            tmpOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            int width = tmpOptions.outWidth;
            int height = tmpOptions.outHeight;
            BitmapRegionDecoder bitmapRegionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
            bitmapTmp = bitmapRegionDecoder.decodeRegion(new Rect(0, 0, width, height), tmpOptions);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //实际加载纹理
        GLUtils.texImage2D
                (
                        GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
                        0,                      //纹理的层次，0表示基本图像层，可以理解为直接贴图
                        bitmapTmp,              //纹理图像
                        0                       //纹理边框尺寸
                );
        if (bitmapTmp != null) {

            bitmapTmp.recycle();          //纹理加载成功后释放图片
        }
    }

    class MyRenderer implements Renderer {


        int numVertices = 0;
        int numIndices = 0;

        private FloatBuffer vertexBuffer, textureBuffer, texRighttureBuffer;
        private ShortBuffer IndicesBuffer;

        private int mWidth, mHeight;
        private String mVertexShader, mFragmentShader;
        private int maPositionHandle, maTexCoorHandle;
        private int mProgram;
        private int mMVPMatrixHandle;

        /**
         * @param numSlices 切分次数
         * @param d         半径
         */
        private int esGenSphere(int numSlices, float d) {
            int i;
            int j;
            int iidex = 0;
            int numParallels = numSlices / 2;
            numVertices = (numParallels + 1) * (numSlices + 1);
            numIndices = numParallels * numSlices * 6;
            float angleStep = (float) ((2.0f * Math.PI) / ((float) numSlices));
            float vertices[] = new float[numVertices * 3];
            float texCoords[] = new float[numVertices * 2];
            float texRightCoords[] = new float[numVertices * 2];

            short indices[] = new short[numIndices];
            for (i = 0; i < numParallels + 1; i++) {
                for (j = 0; j < numSlices + 1; j++) {
                    int vertex = (i * (numSlices + 1) + j) * 3;
                    vertices[vertex] = (float) (d * Math.sin(angleStep * (float) i) * Math.sin(angleStep * (float) j));
                    vertices[vertex + 1] = (float) (d * Math.cos(angleStep * (float) i));
                    vertices[vertex + 2] = (float) (d * Math.sin(angleStep * (float) i) * Math.cos(angleStep * (float) j));

                    int texIndex = (i * (numSlices + 1) + j) * 2;
                    texCoords[texIndex] = 1.0f - (float) j / (float) numSlices;
                    texCoords[texIndex + 1] = ((float) i / (float) numParallels) ;//((float)i/(float)numParallels);//

                    texRightCoords[texIndex] = 1.0f - (float) j / (float) numSlices;
                    texRightCoords[texIndex + 1] = ((float) i / (float) numParallels) / 2 + 0.5f;
                }
            }

            for (i = 0; i < numParallels; i++) {
                for (j = 0; j < numSlices; j++) {
                    indices[iidex++] = (short) (i * (numSlices + 1) + j);
                    indices[iidex++] = (short) ((i + 1) * (numSlices + 1) + j);
                    indices[iidex++] = (short) ((i + 1) * (numSlices + 1) + (j + 1));

                    indices[iidex++] = (short) (i * (numSlices + 1) + j);
                    indices[iidex++] = (short) ((i + 1) * (numSlices + 1) + (j + 1));
                    indices[iidex++] = (short) (i * (numSlices + 1) + (j + 1));
                }
            }

            texRighttureBuffer = MemUtil.makeFloatBuffer(texRightCoords);
            vertexBuffer = MemUtil.makeFloatBuffer(vertices);
            textureBuffer = MemUtil.makeFloatBuffer(texCoords);
            IndicesBuffer = MemUtil.makeShortBuffer(indices);
            return numIndices;
        }

        float[] projectionMatrix = new float[16];   //投影矩阵
        float[] modelViewMatrix = new float[16];    //变换矩阵
        float[] mVMatrix = new float[16];           //摄像机矩阵
        final float[] temp = new float[16];         //总矩阵

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", getResources());
            mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", getResources());
            mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
            maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
            maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            initTexture();
            //调用此方法产生摄像机9参数位置矩阵
            Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            update();
            int count = esGenSphere(100, 100);
            //清除深度缓冲与颜色缓冲
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glUseProgram(mProgram);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, temp, 0);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
            GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            GLES20.glEnableVertexAttribArray(maTexCoorHandle);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glViewport(0, 0, mWidth , mHeight);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, count, GLES20.GL_UNSIGNED_SHORT, IndicesBuffer);

          /*  //设置视窗大小及位置
            GLES20.glViewport(mWidth / 2, 0, mWidth / 2, mHeight);
            GLES20.glUseProgram(mProgram);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, temp, 0);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
            GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, texRighttureBuffer);
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            GLES20.glEnableVertexAttribArray(maTexCoorHandle);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, count, GLES20.GL_UNSIGNED_SHORT, IndicesBuffer);*/
        }

        public void update() {
            mHeadTracker.getLastHeadView(mHeadView, 0);
            perspectiveM(projectionMatrix, 0, 75.0f, mWidth / mHeight / 2.0f, 0.1f, 400.0f);
            Matrix.setIdentityM(modelViewMatrix, 0);
            Matrix.multiplyMM(temp, 0, projectionMatrix, 0, mHeadView, 0);
            Matrix.multiplyMM(temp, 0, temp, 0, mVMatrix, 0);
        }
    }

    public void show(float[] datas) {

        Log.i("mHeadView", "----show: datas:\n");
        for (int i = 0; i < 4; i++) {
            Log.i("mHeadView", datas[i * 4] + "  " + datas[i * 4 + 1] + "  " + datas[i * 4 + 2] + "  " + datas[i * 4 + 3]);
            Log.i("mHeadView", "\n");
        }
    }
}
