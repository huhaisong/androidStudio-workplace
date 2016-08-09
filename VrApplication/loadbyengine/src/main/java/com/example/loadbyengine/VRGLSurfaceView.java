package com.example.loadbyengine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.AttributeSet;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VRGLSurfaceView extends MyGLSurfaceView {
    private Context mContext;

    public VRGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public VRGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        MyRenderer myRenderer = new MyRenderer();
        setRenderer(myRenderer);
    }

    class MyRenderer implements GLSurfaceView.Renderer {


        int textureId;
        private float[] mHeadView = new float[16];
        private int mWidth, mHeight;
        float[] projectionMatrix = new float[16];   //投影矩阵
        float[] modelViewMatrix = new float[16];    //变换矩阵
        float[] mVMatrix = new float[16];           //摄像机矩阵
        final float[] temp = new float[16];         //总矩阵
        private String mVertexShader, mFragmentShader;
        private int mProgram;
        private int maPositionHandle, maTexCoorHandle;
        private int mMVPMatrixHandle;

        //模型
        private FrameBuffer fb = null;
        private World world = null;
        private RGBColor back = new RGBColor(50, 50, 100);
        private Object3D model = null;
        private Light sun;
        private Matrix mMatrix;
        //球
        int count ;
        private FloatBuffer vertexBuffer, textureBuffer;
        private ShortBuffer IndicesBuffer;

        //矩形
        private FloatBuffer vertexBuffer1, textureBuffer1;

        private int esGenSphere(int numSlices, float d) {

            int numVertices ;
            int numIndices ;
            int i;
            int j;
            int iidex = 0;
            int numParallels = numSlices / 2;
            numVertices = (numParallels + 1) * (numSlices + 1);
            numIndices = numParallels * numSlices * 6;
            float angleStep = (float) ((2.0f * Math.PI) / ((float) numSlices));
            float vertices[] = new float[numVertices * 3];
            float texCoords[] = new float[numVertices * 2];
            //float texRightCoords[] = new float[numVertices * 2];

            short indices[] = new short[numIndices];
            for (i = 0; i < numParallels + 1; i++) {
                for (j = 0; j < numSlices + 1; j++) {
                    int vertex = (i * (numSlices + 1) + j) * 3;
                    vertices[vertex] = (float) (d * Math.sin(angleStep * (float) i) * Math.sin(angleStep * (float) j));
                    vertices[vertex + 1] = (float) (d * Math.cos(angleStep * (float) i));
                    vertices[vertex + 2] = (float) (d * Math.sin(angleStep * (float) i) * Math.cos(angleStep * (float) j));

                    int texIndex = (i * (numSlices + 1) + j) * 2;
                    texCoords[texIndex] = 1.0f - (float) j / (float) numSlices;
                    texCoords[texIndex + 1] = ((float) i / (float) numParallels);
                }
            }

            for (i = 0; i < numParallels; i++) {
                for (j = 0; j < numSlices; j++) {
                    //一个正方形
                    indices[iidex++] = (short) (i * (numSlices + 1) + j);
                    indices[iidex++] = (short) ((i + 1) * (numSlices + 1) + j);
                    indices[iidex++] = (short) ((i + 1) * (numSlices + 1) + (j + 1));

                    indices[iidex++] = (short) (i * (numSlices + 1) + j);
                    indices[iidex++] = (short) ((i + 1) * (numSlices + 1) + (j + 1));
                    indices[iidex++] = (short) (i * (numSlices + 1) + (j + 1));
                }
            }
            vertexBuffer = MemUtil.makeFloatBuffer(vertices);
            textureBuffer = MemUtil.makeFloatBuffer(texCoords);
            IndicesBuffer = MemUtil.makeShortBuffer(indices);
            return numIndices;
        }

        private void esGenRect(){

            float size = 50f;
            float vertices[] = new float[]{

                    -size,size,-100,
                    -size,-size,-100,
                    size,-size,-100,

                    size,-size,-100,
                    size,size,-100,
                    -size,size,-100,

            };
            float texCoords[] = new float[]{

                    0,0,
                    0,1,
                    1,1,

                    1,1,
                    1,0,
                    0,0,
            };

            vertexBuffer1 = MemUtil.makeFloatBuffer(vertices);
            textureBuffer1 = MemUtil.makeFloatBuffer(texCoords);

        }

        private void initModel() {

            //初始化
            if (fb != null) {
                fb.dispose();
                fb = null;
            }
            fb = new FrameBuffer(mWidth / 2, mHeight);
            world = new World();
            world.setObjectsVisibility(true);
            //  world.setAmbientLight(200, 200, 200);
            // sun = new Light(world);
            //  sun.setIntensity(250, 250, 250);

            //加载模型
            Object3D[] objs = null;
            InputStream objStream = null;
            InputStream mtlStream = null;
            try {
                objStream = mContext.getAssets().open("st1.obj");
                mtlStream = mContext.getAssets().open("st1.mtl");
                loadTexture("rock_001_c.jpg");
                loadTexture("rock_002_c.jpg");
                loadTexture("rock_003_c.jpg");
                loadTexture("rock_004_c.jpg");
                loadTexture("rock_005_c.jpg");
                loadTexture("rock_010_c.jpg");
                loadTexture("sphere.jpg");
                objs = Loader.loadOBJ(objStream, mtlStream, 2);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (objStream != null) {
                    try {
                        objStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (mtlStream != null) {
                    try {
                        mtlStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (objs != null) {
                model = Object3D.mergeAll(objs);
            }
            model.setCulling(false);
            //model.scale(2.0f);
            model.strip();
            model.build();
            model.rotateX(2.9f);
            model.rotateY(0.5f);
            //model.rotateZ(1.5f);
            world.addObject(model);

            Camera cam = world.getCamera();
            cam.moveCamera(Camera.CAMERA_MOVEIN, 2f);
            SimpleVector lookVector = new SimpleVector(0, 0, 0);
            cam.lookAt(lookVector);
            mMatrix = cam.getBack();

            MemoryHelper.compact();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

            count = esGenSphere(200, 1000.0f);
            esGenRect();

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
            android.opengl.Matrix.setLookAtM(mVMatrix, 0, 0, 0, 4, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        }

        public void onSurfaceChanged(GL10 gl, int w, int h) {

            mWidth = w;
            mHeight = h;
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            initModel();
        }

        public void onDrawFrame(GL10 gl) {

            update();

            //画左边
            GLES20.glViewport(0, 0, mWidth / 2, mHeight);
            //画模型
            world.draw(fb);
            //画矩形
            GLES20.glUseProgram(mProgram);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, temp, 0);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer1);
            GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer1);
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            GLES20.glEnableVertexAttribArray(maTexCoorHandle);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,6);
            //画球
            GLES20.glUseProgram(mProgram);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, temp, 0);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
            GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            GLES20.glEnableVertexAttribArray(maTexCoorHandle);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, count, GLES20.GL_UNSIGNED_SHORT, IndicesBuffer);

            //画右边
            GLES20.glViewport(mWidth / 2, 0, mWidth / 2, mHeight);
            //画模型
            world.draw(fb);
            //画球
            GLES20.glUseProgram(mProgram);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, temp, 0);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
            GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            GLES20.glEnableVertexAttribArray(maTexCoorHandle);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, count, GLES20.GL_UNSIGNED_SHORT, IndicesBuffer);
            fb.display();

        }

        public void loadTexture(String name) throws Exception {
            if (TextureManager.getInstance().getTextureID(name) < 0) {
                Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper
                        .loadImage(mContext.getAssets().open(name)), 512, 512));
                TextureManager.getInstance().addTexture(name, texture);
            }
        }

        public void update() {
            //总矩阵
            mHeadTracker.getLastHeadView(mHeadView, 0);
            android.opengl.Matrix.perspectiveM(projectionMatrix, 0, 75.0f, mWidth / mHeight / 2.0f, 0.1f, 10000.0f);
            android.opengl.Matrix.setIdentityM(modelViewMatrix, 0);
            android.opengl.Matrix.multiplyMM(temp, 0, projectionMatrix, 0, mHeadView, 0);
            android.opengl.Matrix.multiplyMM(temp, 0, temp, 0, mVMatrix, 0);

            //设置model的headView
            float[] t;
            Camera cam = world.getCamera();
            Matrix m = mMatrix.cloneMatrix();
            Matrix mheadm = new Matrix();
            t = mHeadView;
            mheadm.setRow(0, t[0], -1.0f * t[1], -1.0f * t[2], t[3]);
            mheadm.setRow(1, -1.0f * t[4], t[5], t[6], t[7]);
            mheadm.setRow(2, -1.0f * t[8], t[9], t[10], t[11]);
            mheadm.setRow(3, t[12], t[13], t[14], t[15]);
            m.matMul(mheadm);
            cam.setBack(m);
            //初始化model
            fb.clear(back);
            world.renderScene(fb);
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
    }
}

