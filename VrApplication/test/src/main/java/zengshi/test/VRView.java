package zengshi.test;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 111 on 2016/8/5.
 */
public class VRView extends MyGLSurfaceView {

    private static final String TAG = "MyVRView";

    private static final int Image = 0;
    private static final int Video = 1;

    private int mType = -1;
    private int msid = 0;
    private Boolean updateHots = false;

    private MyRenderer mVrRenderer;
    private Context mContext;
    private VRListImg mVrlist = null;
    private List<ListItem> mImageList360 = new ArrayList<ListItem>();
    private ArrayList<ListItem> mPlayVideoList360 = new ArrayList<ListItem>();

    private int hostid = 0;
    private int showmenu = 0;
    private RGBColor back = new RGBColor(50, 50, 100);

    private FrameBuffer fb = null;
    private World world = null;
    private Object3D mMenuSet;
    private Object3D mMenuVid;
    private Object3D mMenuImage;
    private Object3D menubox;

    private Object3D mButRetu;
    private Object3D mButUP;
    private Object3D mButDown;
    private Object3D mButItem00;
    private Object3D mButItem01;
    private Object3D mButItem02;
    private Object3D mButItem03;
    private Object3D mButItem10;
    private Object3D mButItem11;
    private Object3D mButItem12;
    private Object3D mButItem13;


    private String getImagePath(int number) {
        int num = mVrlist.getPageSize() * mVrlist.getPage() + number;
        ListItem listItem = mImageList360.get(num);
        return listItem.Path;
    }

    private String getVideoPath(int number) {
        int num = mVrlist.getPageSize() * mVrlist.getPage() + number;
        if (mPlayVideoList360.size()>=number){

            ListItem listItem = mPlayVideoList360.get(num);
            return listItem.Path;
        }else {
            return null;
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 8888:
                    if (showmenu == 0) {
                        if (mMenuImage.getID() == msg.arg1) {
                            mType = Image;
                            showmenu = 2;
                            mVrlist.SetList(mImageList360, showmenu, "bj_360.png");
                            mVrlist.Draw();
                            updateHots = true;
                        } else if (mMenuVid.getID() == msg.arg1) {
                            mType = Video;
                            showmenu = 2;
                            mVrlist.SetList(mPlayVideoList360, showmenu, "bj_360.png");
                            mVrlist.Draw();
                            updateHots = true;
                        } else {
                            showmenu = 0;
                        }
                    } else if (showmenu == 2) {
                        if (mButRetu.getID() == msg.arg1) {
                            showmenu = 0;
                        } else if (mButUP.getID() == msg.arg1) {
                            int page = mVrlist.Page;
                            mVrlist.CutPage();
                            if (page != mVrlist.Page) {
                                mVrlist.Draw();
                                updateHots = true;
                            }
                        } else if (mButDown.getID() == msg.arg1) {
                            int page = mVrlist.Page;
                            mVrlist.AddPage();
                            if (page != mVrlist.Page) {
                                mVrlist.Draw();
                                updateHots = true;
                            }
                        } else {
                            String path = null;
                            showmenu = 3;
                            if (mType == Video) {
                                if (mButItem00.getID() == msg.arg1) {
                                    path = getVideoPath(0);

                                } else if (mButItem01.getID() == msg.arg1) {
                                    path = getVideoPath(1);
                                } else if (mButItem02.getID() == msg.arg1) {
                                    path = getVideoPath(2);
                                } else if (mButItem03.getID() == msg.arg1) {
                                    path = getVideoPath(3);
                                } else if (mButItem10.getID() == msg.arg1) {
                                    path = getVideoPath(4);
                                } else if (mButItem11.getID() == msg.arg1) {
                                    path = getVideoPath(5);
                                } else if (mButItem12.getID() == msg.arg1) {
                                    path = getVideoPath(6);
                                } else if (mButItem13.getID() == msg.arg1) {
                                    path = getVideoPath(7);
                                }
                                if (path != null) {
                                    startVideoActivity(path);
                                }
                            } else if (mType == Image) {
                                if (mButItem00.getID() == msg.arg1) {
                                    path = getImagePath(0);
                                } else if (mButItem01.getID() == msg.arg1) {
                                    path = getImagePath(1);
                                } else if (mButItem02.getID() == msg.arg1) {
                                    path = getImagePath(2);
                                } else if (mButItem03.getID() == msg.arg1) {
                                    path = getImagePath(3);
                                } else if (mButItem10.getID() == msg.arg1) {
                                    path = getImagePath(4);
                                } else if (mButItem11.getID() == msg.arg1) {
                                    path = getImagePath(5);
                                } else if (mButItem12.getID() == msg.arg1) {
                                    path = getImagePath(6);
                                } else if (mButItem13.getID() == msg.arg1) {
                                    path = getImagePath(7);
                                }
                                if (path != null) {
                                    startImageActivity(path);
                                }
                            }
                        }
                    }
                    break;
            }
        }
    };

    public VRView(Context context) {
        super(context);
        Init(context);
    }


    public VRView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    private void startVideoActivity(String path) {
        Log.i(TAG, "startVideoActivity:+---------- ");
        Intent intent = new Intent(mContext, VRVideo360Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        intent.putExtra("content", bundle);
        mContext.startActivity(intent);
    }

    public void startImageActivity(String path) {

        Intent intent = new Intent(mContext, ImageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        intent.putExtra("content", bundle);
        mContext.startActivity(intent);
    }


    public void Init(Context context) {
        mContext = context;
        mVrRenderer = new MyRenderer();
        setRenderer(mVrRenderer);
        GetList();
        mVrlist = new VRListImg(mContext, "bj_360.png");
        mVrlist.SetList(mPlayVideoList360, 0, "bj_360.png");
        mVrlist.Draw();
    }

    private void GetList(boolean isVideo) {
        Uri uri;
        if (isVideo) {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        ContentResolver mContentResolver = mContext.getContentResolver();
        Cursor mCursor = mContentResolver.query(uri, null, null, null, null);
        mCursor.moveToFirst();
        int num = mCursor.getCount();
        if (num > 0) {
            do {
                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                File f = new File(path);
                path = f.getPath();
                long id = mCursor.getLong(mCursor.getColumnIndex("_ID"));
                if (isVideo) {
                    mPlayVideoList360.add(new ListItem(path, id, isVideo));
                } else {
                    mImageList360.add(new ListItem(path, id, isVideo));
                }
            } while (mCursor.moveToNext());
        }
        mCursor.close();
    }

    public void GetList() {
        mImageList360.clear();
        mPlayVideoList360.clear();
        GetList(true);
        GetList(false);
    }


    private void loadtexture(String Name, Bitmap bmp) {

        if (bmp != null) {
            if (TextureManager.getInstance().getTextureID(Name) > -1) {
                TextureManager.getInstance().removeTexture(Name);
            }
            Texture uv1 = new Texture(BitmapHelper.rescale(bmp, 512, 512));
            uv1.setClamping(true);
            TextureManager.getInstance().addTexture(Name, uv1);
        }
    }

    private void loadtexture(String Name, int w) {

        try {
            if (TextureManager.getInstance().getTextureID(Name) < 0) {
                Texture uv1 = new Texture(BitmapHelper.rescale(BitmapHelper.loadImage(mContext.getAssets().open(Name)), w, w));
                uv1.setClamping(true);
                TextureManager.getInstance().addTexture(
                        Name, uv1
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadTexture(String name) throws Exception {
        if (TextureManager.getInstance().getTextureID(name) < 0) {
            Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper
                    .loadImage(mContext.getAssets().open(name)), 512, 512));
            TextureManager.getInstance().addTexture(name, texture);
        }
    }

    public class MyRenderer implements Renderer {

        //球
        int count;
        private FloatBuffer vertexBuffer, textureBuffer;
        private ShortBuffer IndicesBuffer;
        int textureId;
        private float[] mHeadView = new float[16];
        float[] projectionMatrix = new float[16];   //投影矩阵
        float[] modelViewMatrix = new float[16];    //变换矩阵
        float[] mVMatrix = new float[16];           //摄像机矩阵
        final float[] temp = new float[16];         //总矩阵
        private String mVertexShader, mFragmentShader;
        private int mProgram;
        private int maPositionHandle, maTexCoorHandle;
        private int mMVPMatrixHandle;

        private int esGenSphere(int numSlices, float d) {

            int numVertices;
            int numIndices;
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

        private void initModel() {

            //初始化
            if (fb != null) {
                fb.dispose();
                fb = null;
            }
            fb = new FrameBuffer(mWidth / 2, mHeight);
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
            model.strip();
            model.build();
            model.rotateX(2.9f);
            model.rotateY(0.5f);
            world.addObject(model);

            MemoryHelper.compact();
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

        //模型
        private FrameBuffer fb = null;
        private World world = null;
        private RGBColor back = new RGBColor(50, 50, 100);
        private Object3D model = null;
        private Light sun;

        int mWidth, mHeight;

        static final float FH = 1.2f;
        static final float MH = 0.2f;
        static final float zz = -3.0f; //1.2f;
        static final float dx = 3.0f;//2.5f;

        private Object3D esSectorButton(String name, int m, int x, int y, int w, int h) {
            int bjw = 640;
            int bjh = 360;
            int ex = bjw - x;
            int fx = ex - w;
            float anglefist = -2.0f * (float) Math.PI * 75.0f / 360f / 2 + 2.0f * (float) Math.PI * 75.0f / 360f * fx / bjw;
            float angleStep = 2.0f * (float) Math.PI * 75.0f / 360f * w / bjw / m;
            float texStepU = 1.0f * w / bjw / m;
            float texfistU = 1.0f * fx / bjw;

            float fisth = -FH + MH + 2 * FH * y / bjh;
            float endh = -FH + MH + 2 * FH * (y + h) / bjh;

            float textfistV = 1.0f * y / bjh;
            float textendV = 1.0f * (y + h) / bjh;

            Object3D button = new Object3D(2 * m);
            for (int i = 0; i < m; i++) {
                float onetx = 1.0f - i * texStepU - texfistU;
                float zeotx = 1.0f - (i + 1) * texStepU - texfistU;
                float pl = (dx * (float) Math.sin(anglefist + i * angleStep));
                float pr = (dx * (float) Math.sin(anglefist + (i + 1) * angleStep));
                float maxz = 3.6f + (zz * (float) Math.cos(anglefist + i * angleStep));
                float minz = 3.6f + (zz * (float) Math.cos(anglefist + (i + 1) * angleStep));

                button.addTriangle(
                        new SimpleVector(pl, endh, maxz), onetx, textendV,
                        new SimpleVector(pl, fisth, maxz), onetx, textfistV,
                        new SimpleVector(pr, fisth, minz), zeotx, textfistV,
                        TextureManager.getInstance().getTextureID("bj_hot.png"));

                button.addTriangle(new SimpleVector(pl, endh, maxz), onetx, textendV,
                        new SimpleVector(pr, fisth, minz), zeotx, textfistV,
                        new SimpleVector(pr, endh, minz), zeotx, textendV,
                        TextureManager.getInstance().getTextureID("bj_hot.png"));
            }
            button.build();
            button.setCulling(false);
            button.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
            return button;
        }

        private void esSector(int m) {

            float anglefist = -2.0f * (float) Math.PI * 75.0f / 360f / 2;
            float angleStep = 2.0f * (float) Math.PI * 75.0f / 360f / m;
            float texStep = 1.0f / m;

            if (menubox != null)
                menubox.clearObject();
            menubox = new Object3D(2 * m);
            for (int i = 0; i < m; i++) {
                float onetx = 1.0f - i * texStep;
                float zeotx = 1.0f - (i + 1) * texStep;
                float pl = (dx * (float) Math.sin(anglefist + i * angleStep));
                float pr = (dx * (float) Math.sin(anglefist + (i + 1) * angleStep));
                float maxz = 3.6f + (zz * (float) Math.cos(anglefist + i * angleStep));
                float minz = 3.6f + (zz * (float) Math.cos(anglefist + (i + 1) * angleStep));

                menubox.addTriangle(
                        new SimpleVector(pl, FH + MH, maxz), onetx, 1.0f,
                        new SimpleVector(pl, -FH + MH, maxz), onetx, 0.0f,
                        new SimpleVector(pr, -FH + MH, minz), zeotx, 0.0f,
                        TextureManager.getInstance().getTextureID("Menubox"));

                menubox.addTriangle(
                        new SimpleVector(pl, FH + MH, maxz), onetx, 1.0f,
                        new SimpleVector(pr, -FH + MH, minz), zeotx, 0.0f,
                        new SimpleVector(pr, FH + MH, minz), zeotx, 1.0f,
                        TextureManager.getInstance().getTextureID("Menubox"));
            }
            menubox.setCulling(false);
            menubox.build();
            if (mButRetu != null)
                mButRetu.clearObject();
            if (mButUP != null)
                mButUP.clearObject();
            if (mButDown != null)
                mButDown.clearObject();
            if (mButItem00 != null)
                mButItem00.clearObject();
            if (mButItem01 != null)
                mButItem01.clearObject();
            if (mButItem02 != null)
                mButItem02.clearObject();
            if (mButItem03 != null)
                mButItem03.clearObject();
            if (mButItem10 != null)
                mButItem10.clearObject();
            if (mButItem11 != null)
                mButItem11.clearObject();
            if (mButItem12 != null)
                mButItem12.clearObject();
            if (mButItem13 != null)
                mButItem13.clearObject();
            mButRetu = esSectorButton("bun_retun", 1, 484, 16, 64, 64);
            mButUP = esSectorButton("mButUP", 1, 191, 16, 64, 64);
            mButDown = esSectorButton("mButDown", 1, 385, 16, 64, 64);
            mButItem00 = esSectorButton("mButItem00", 1, 4, 98, 152, 100);
            mButItem01 = esSectorButton("mButItem01", 1, 164, 98, 152, 100);
            mButItem02 = esSectorButton("mButItem02", 1, 324, 98, 152, 100);
            mButItem03 = esSectorButton("mButItem03", 1, 484, 98, 152, 100);
            mButItem10 = esSectorButton("mButItem10", 1, 4, 210, 152, 100);
            mButItem11 = esSectorButton("mButItem11", 1, 164, 210, 152, 100);
            mButItem12 = esSectorButton("mButItem12", 1, 324, 210, 152, 100);
            mButItem13 = esSectorButton("mButItem13", 1, 484, 210, 152, 100);
        }

        private void loadDefault() {
            if (mMenuSet != null)
                mMenuSet.clearObject();

            if (mMenuVid != null)
                mMenuVid.clearObject();

            if (mMenuImage != null)
                mMenuImage.clearObject();

            loadtexture("vid.png", 256);
            loadtexture("img.png", 256);
            loadtexture("set.png", 256);
            loadtexture("set_hot.png", 256);
            loadtexture("vid_hot.png", 256);
            loadtexture("img_hot.png", 256);
            loadtexture("Menubox", mVrlist.GetBmp());
            loadtexture("bj_hot.png", mVrlist.GetHotBmp());

            mMenuVid = new Object3D(2);
            mMenuSet = new Object3D(2);
            mMenuImage = new Object3D(2);
            float zv = -0.3f;
            float zz = 0.7f;
            float pl = 0.96f;
            float pr = 1.90f;
            float pb = 1.2f;
            float pt = 0.2f;
            mMenuVid.addTriangle(new SimpleVector(pl, pb, zz + zv), 1.0f, 1.0f,
                    new SimpleVector(pl, pt, zz + zv), 1.0f, 0.0f,
                    new SimpleVector(pr, pt, zz), 0.0f, 0.0f,
                    TextureManager.getInstance().getTextureID("vid.png"));

            mMenuVid.addTriangle(new SimpleVector(pl, pb, zz + zv), 1.0f, 1.0f,
                    new SimpleVector(pr, pt, zz), 0.0f, 0.0f,
                    new SimpleVector(pr, pb, zz), 0.0f, 1.0f,
                    TextureManager.getInstance().getTextureID("vid.png"));
            mMenuVid.setCulling(false);
            mMenuVid.build();

            pl = -2.06f + 0.15f;
            pr = -1.1f + 0.15f;
            pb = 1.2f;
            pt = 0.2f;
            zv = -0.3f;
            zz = 0.7f;
            mMenuSet.addTriangle(new SimpleVector(pl, pb, zz), 1.0f, 1.0f,
                    new SimpleVector(pl, pt, zz), 1.0f, 0.0f,
                    new SimpleVector(pr, pt, zz + zv), 0.0f, 0.0f,
                    TextureManager.getInstance().getTextureID("set.png"));

            mMenuSet.addTriangle(new SimpleVector(pl, pb, zz), 1.0f, 1.0f,
                    new SimpleVector(pr, pt, zz + zv), 0.0f, 0.0f,
                    new SimpleVector(pr, pb, zz + zv), 0.0f, 1.0f,
                    TextureManager.getInstance().getTextureID("set.png"));
            mMenuSet.setCulling(false);
            mMenuSet.build();

            pl = -0.56f;
            pr = 0.6f;
            pb = 1.2f;
            pt = 0.2f;
            zz = 0.13f;
            mMenuImage.addTriangle(new SimpleVector(pl, pb, zz), 1.0f, 1.0f,
                    new SimpleVector(pl, pt, zz), 1.0f, 0.0f,
                    new SimpleVector(pr, pt, zz), 0.0f, 0.0f,
                    TextureManager.getInstance().getTextureID("img.png"));

            mMenuImage.addTriangle(new SimpleVector(pl, pb, zz), 1.0f, 1.0f,
                    new SimpleVector(pr, pt, zz), 0.0f, 0.0f,
                    new SimpleVector(pr, pb, zz), 0.0f, 1.0f,
                    TextureManager.getInstance().getTextureID("img.png"));
            mMenuImage.setCulling(false);
            mMenuImage.build();

            esSector(32);

            world.addObject(mMenuSet);
            world.addObject(mMenuVid);
            world.addObject(mMenuImage);
            mMenuVid.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
            mMenuSet.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
            mMenuImage.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
            MemoryHelper.compact();
        }

        private void update() {
            if (msid != showmenu) {
                msid = showmenu;
                if (msid == 2) {
                    if (world.getObject(mMenuSet.getID()) != null) {
                        world.removeObject(mMenuSet);
                        world.removeObject(mMenuVid);
                        world.removeObject(mMenuImage);
                    }
                    world.addObject(menubox);
                    world.addObject(mButRetu);
                    world.addObject(mButUP);
                    world.addObject(mButDown);
                    world.addObject(mButItem00);
                    world.addObject(mButItem01);
                    world.addObject(mButItem02);
                    world.addObject(mButItem03);
                    world.addObject(mButItem10);
                    world.addObject(mButItem11);
                    world.addObject(mButItem12);
                    world.addObject(mButItem13);
                } else if (msid == 0) {
                    if (world.getObject(menubox.getID()) != null) {
                        world.removeObject(menubox);
                        world.removeObject(mButRetu);
                        world.removeObject(mButUP);
                        world.removeObject(mButDown);
                        world.removeObject(mButItem00);
                        world.removeObject(mButItem01);
                        world.removeObject(mButItem02);
                        world.removeObject(mButItem03);
                        world.removeObject(mButItem10);
                        world.removeObject(mButItem11);
                        world.removeObject(mButItem12);
                        world.removeObject(mButItem13);
                    }
                    world.addObject(mMenuSet);
                    world.addObject(mMenuVid);
                    world.addObject(mMenuImage);
                } else if (msid == 3) {

                    if (world.getObject(mButRetu.getID()) != null) {
                        world.removeObject(mButRetu);
                        world.removeObject(mButUP);
                        world.removeObject(mButDown);
                        world.removeObject(mButItem00);
                        world.removeObject(mButItem01);
                        world.removeObject(mButItem02);
                        world.removeObject(mButItem03);
                        world.removeObject(mButItem10);
                        world.removeObject(mButItem11);
                        world.removeObject(mButItem12);
                        world.removeObject(mButItem13);
                    }

                    if (world.getObject(menubox.getID()) == null) {
                        world.addObject(menubox);
                    }
                }
            }
            if (updateHots) {
                updateHots = false;
                loadtexture("Menubox", mVrlist.GetBmp());
                loadtexture("bj_hot.png", mVrlist.GetHotBmp());
            }
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

            count = esGenSphere(200, 1000.0f);

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

        private Matrix mMatrix;

        @Override
        public void onSurfaceChanged(GL10 gl, int w, int h) {

            GLES20.glDisable(GLES20.GL_CULL_FACE);

            mWidth = w;
            mHeight = h;
            if (fb != null) {
                fb.dispose();
            }
            fb = new FrameBuffer(w / 2, h);
            world = new World();
            world.setAmbientLight(255, 255, 255);
            initModel();
            loadDefault();

            Camera cam = world.getCamera();
            cam.moveCamera(Camera.CAMERA_MOVEIN, 4f);

            SimpleVector lookVector = new SimpleVector(0, 0, 0);
            cam.lookAt(lookVector);
            mMatrix = cam.getBack();
            MemoryHelper.compact();
        }

        @Override
        public void onDrawFrame(GL10 gl) {

            mHeadTracker.getLastHeadView(mHeadView, 0);
            android.opengl.Matrix.perspectiveM(projectionMatrix, 0, 75.0f, mWidth / mHeight / 2.0f, 0.1f, 10000.0f);
            android.opengl.Matrix.setIdentityM(modelViewMatrix, 0);
            android.opengl.Matrix.multiplyMM(temp, 0, projectionMatrix, 0, mHeadView, 0);
            android.opengl.Matrix.multiplyMM(temp, 0, temp, 0, mVMatrix, 0);

            float[] t;
            Camera cam = world.getCamera();
            Matrix m = mMatrix.cloneMatrix();
            mHeadTracker.getLastHeadView(mHeadTransform.getHeadView(), 0);

            SimpleVector dir = Interact2D.reproject2D3DWS(cam, fb, mWidth / 4, mHeight / 2).normalize();
            Object[] res = world.calcMinDistanceAndObject3D(cam.getPosition(), dir, 10000);
            Object3D picked = (Object3D) res[1];
            int pickid = -1;
            if (picked != null) {
                pickid = picked.getID();
                if (msid > 0) {
                    if (mButRetu.getID() == pickid)
                        mButRetu.setTexture("bj_hot.png");
                    else
                        mButRetu.setTexture("Menubox");

                    if (mButUP.getID() == pickid)
                        mButUP.setTexture("bj_hot.png");
                    else
                        mButUP.setTexture("Menubox");

                    if (mButDown.getID() == pickid)
                        mButDown.setTexture("bj_hot.png");
                    else
                        mButDown.setTexture("Menubox");

                    if (mButItem00.getID() == pickid)
                        mButItem00.setTexture("bj_hot.png");
                    else
                        mButItem00.setTexture("Menubox");

                    if (mButItem01.getID() == pickid)
                        mButItem01.setTexture("bj_hot.png");
                    else
                        mButItem01.setTexture("Menubox");

                    if (mButItem02.getID() == pickid)
                        mButItem02.setTexture("bj_hot.png");
                    else
                        mButItem02.setTexture("Menubox");

                    if (mButItem03.getID() == pickid)
                        mButItem03.setTexture("bj_hot.png");
                    else
                        mButItem03.setTexture("Menubox");

                    if (mButItem10.getID() == pickid)
                        mButItem10.setTexture("bj_hot.png");
                    else
                        mButItem10.setTexture("Menubox");

                    if (mButItem11.getID() == pickid)
                        mButItem11.setTexture("bj_hot.png");
                    else
                        mButItem11.setTexture("Menubox");

                    if (mButItem12.getID() == pickid)
                        mButItem12.setTexture("bj_hot.png");
                    else
                        mButItem12.setTexture("Menubox");

                    if (mButItem13.getID() == pickid)
                        mButItem13.setTexture("bj_hot.png");
                    else
                        mButItem13.setTexture("Menubox");

                } else if (msid == 0) {
                    if (mMenuSet.getID() == pickid) {
                        mMenuSet.setTexture("set_hot.png");
                    } else {
                        mMenuSet.setTexture("set.png");

                    }
                    if (mMenuVid.getID() == pickid) {
                        mMenuVid.setTexture("vid_hot.png");
                    } else {
                        mMenuVid.setTexture("vid.png");
                    }
                    if (mMenuImage.getID() == pickid) {
                        mMenuImage.setTexture("img_hot.png");
                    } else {
                        mMenuImage.setTexture("img.png");
                    }
                }

            } else {
                if (msid == 2) {
                    mButRetu.setTexture("Menubox");
                    mButUP.setTexture("Menubox");
                    mButDown.setTexture("Menubox");
                    mButItem00.setTexture("Menubox");
                    mButItem01.setTexture("Menubox");
                    mButItem02.setTexture("Menubox");
                    mButItem03.setTexture("Menubox");
                    mButItem10.setTexture("Menubox");
                    mButItem11.setTexture("Menubox");
                    mButItem12.setTexture("Menubox");
                    mButItem13.setTexture("Menubox");
                } else if (msid == 0) {
                    mMenuSet.setTexture("set.png");
                    mMenuVid.setTexture("vid.png");
                    mMenuImage.setTexture("img.png");
                }
            }

            if (hostid != pickid) {
                hostid = pickid;
                mHandler.removeMessages(8888);
                if (hostid > -1) {
                    Message message = new Message();
                    message.what = 8888;
                    message.arg1 = hostid;
                    mHandler.sendMessageDelayed(message, 1000);
                }
            }

            update();
            fb.clear(back);

            Matrix mheadm = new Matrix();
            t = mHeadTransform.getHeadView();
            mheadm.setRow(0, t[0], -1.0f * t[1], -1.0f * t[2], t[3]);
            mheadm.setRow(1, -1.0f * t[4], t[5], t[6], t[7]);
            mheadm.setRow(2, -1.0f * t[8], t[9], t[10], t[11]);
            mheadm.setRow(3, t[12], t[13], t[14], t[15]);
            m.matMul(mheadm);
            cam.setBack(m);

            world.renderScene(fb);
            GLES20.glViewport(0, 0, fb.getWidth(), fb.getHeight());
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

            GLES20.glViewport(fb.getWidth(), 0, fb.getWidth(), fb.getHeight());
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
    }
}
