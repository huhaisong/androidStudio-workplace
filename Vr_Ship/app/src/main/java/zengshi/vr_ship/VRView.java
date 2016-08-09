package zengshi.vr_ship;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

@SuppressLint("HandlerLeak")
public class VRView extends VRGLSurfaceView {
    private int mWidth,mHeight;
    private VrRenderer mVrRenderer;
    private VRListImg mVrlist = null;
    private List<ListItem> mList360 = new ArrayList<ListItem>();
    private List<ListItem> mList3d = new ArrayList<ListItem>();
    private List<ListItem> mListvr = new ArrayList<ListItem>();
    private List<ListItem> mListgame = new ArrayList<ListItem>();

    private ArrayList<String> mPlayVideoList360 = new ArrayList<String>();
    private ArrayList<String> mPlayImageList360 = new ArrayList<String>();
    private ArrayList<String> mPlayImageList3d = new ArrayList<String>();
    private ArrayList<String> mPlayImageListvr = new ArrayList<String>();
    private ArrayList<String> mPlayVideoList3d = new ArrayList<String>();
    private ArrayList<String> mPlayVideoListvr = new ArrayList<String>();
    private FrameBuffer fb = null;
    private World world = null;
    //	private Object3D cube = null;
    private Object3D mMenuSet;
    private Object3D mMenuVid;
    private Object3D mMenuGame;
    private Object3D object3d;
    private Object3D menubox;

    private Object3D mButRetu;
    private Object3D mButUP;
    private Object3D mButDown;

    private List<Object3D> mButItem = new ArrayList<Object3D>();

    private int lastaddsize = 0;

    private int fps = 0;
    private int lfps = 0;
    private Light sun = null;
    private RGBColor back = new RGBColor(50, 50, 100);
    private Texture font = null;
    private Context mContext;

    private int hostid = 0;
    private int showmenu = 0;
//	private float[] mHeadView;

    public void onPause() {
        super.onPause();

    }

    public void onResume() {
        super.onResume();

    }

    public VRView(Context context) {
        super(context);
        Init(context);
    }

    public VRView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public void Init(Context context) {
        mContext = context;
        // 	mHeadView = new float[16];
             /*setEGLContextClientVersion(1);
             setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
				public javax.microedition.khronos.egl.EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
					int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16,
							EGL10.EGL_NONE };
					javax.microedition.khronos.egl.EGLConfig[] configs = new javax.microedition.khronos.egl.EGLConfig[1];
					int[] result = new int[1];
					egl.eglChooseConfig(display, attributes, configs, 1, result);
					return configs[0];
				}
		 	});*/


        mVrRenderer = new VrRenderer();
        setRenderer(mVrRenderer);

        GetList();
        mVrlist = new VRListImg(mContext, "bj_game.png");
        mVrlist.SetList(mList360, 0, "bj_360.png");
        mVrlist.Draw();
    }

    private void GetList(Uri uri, Boolean IsVideo) {
        ContentResolver mContentResolver = mContext.getContentResolver();
        Cursor mCursor = mContentResolver.query(uri, null, null, null, null);
        mCursor.moveToFirst();
        int num = mCursor.getCount();
        //	Log.e("ar110","num:"+num);
        if (num > 0) {
            do {
                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                long id = mCursor.getLong(mCursor.getColumnIndex("_ID"));
                if (path.indexOf("/VRResources/3D/") > 0) {
                    mList3d.add(new ListItem(path, id, IsVideo));
                    if (IsVideo) {
                        mPlayVideoList3d.add(path);
                    } else {
                        mPlayImageList3d.add(path);
                  }
                } else if (path.indexOf("/VRResources/360/") > 0) {
                    mList360.add(new ListItem(path, id, IsVideo));
                    if (IsVideo) {
                        mPlayVideoList360.add(path);
                    } else {
                        mPlayImageList360.add(path);
                    }
                }
                    /*else
                    {
		    			mListvr.add(new ListItem(path,id,IsVideo));
		    			if(IsVideo)
		    			{
		    				mPlayVideoListvr.add(path);
		    			}else
		    			{
		    				mPlayImageListvr.add(path);
		    			}
		    		}*/
            } while (mCursor.moveToNext());
        }
        mCursor.close();
    }

    public void GetList() {
        mListvr.clear();
        mList3d.clear();
        mList360.clear();

        mPlayVideoListvr.clear();
        mPlayVideoList3d.clear();
        mPlayVideoList360.clear();

        mPlayImageListvr.clear();
        mPlayImageList3d.clear();
        mPlayImageList360.clear();

        mListgame.clear();

        //	MenuHotSet();

        GetList(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false);
        GetList(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false);

        GetList(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true);
        GetList(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true);

        String PackName = "com.archiactinteractive.LfGC";

        Intent LaunchIntent = mContext.getPackageManager().getLaunchIntentForPackage(PackName);
        if (LaunchIntent != null)
            mListgame.add(new ListItem("game0.jpg", 0, false));
        mListgame.add(new ListItem("game1.jpg", 0, false));
    }

    private int msid = 0;
    private Boolean updateHots = false;

    private void update() {
        if (msid != showmenu) {
            msid = showmenu;
            if (msid > 0) {
                if (world.getObject(mMenuSet.getID()) != null) {
                    world.removeObject(mMenuSet);
                    world.removeObject(mMenuVid);
                    world.removeObject(mMenuGame);
                }
                //if(world.getObject(menubox.getID())==null)
                {
                    world.addObject(menubox);

                    world.addObject(mButRetu);
                    world.addObject(mButUP);
                    world.addObject(mButDown);

                    int pagesize = mButItem.size();
                    if (pagesize > mVrlist.GetPageSize())
                        pagesize = mVrlist.GetPageSize();
                    for (int i = 0; i < pagesize; i++) {
                        Object3D obj = mButItem.get(i);
                        world.addObject(obj);
                    }
                    lastaddsize = pagesize;


                }
            } else {
                if (world.getObject(menubox.getID()) != null) {
                    world.removeObject(menubox);
                    world.removeObject(mButRetu);
                    world.removeObject(mButUP);
                    world.removeObject(mButDown);

                    for (int i = 0; i < lastaddsize; i++) {
                        Object3D obj = mButItem.get(i);
                        world.removeObject(obj);
                    }

                }

                //if(world.getObject(mMenuSet.getID())==null)
                {
                    world.addObject(mMenuSet);
                    world.addObject(mMenuVid);
                    world.addObject(mMenuGame);
                }
            }
        }
        if (updateHots) {
            updateHots = false;
            int pagesize = mVrlist.GetPageSize();
            if (lastaddsize != pagesize) {
                if (msid > 0) {
                    for (int i = 0; i < lastaddsize; i++) {
                        Object3D obj = mButItem.get(i);
                        world.removeObject(obj);
                    }

                    for (int i = 0; i < pagesize; i++) {
                        Object3D obj = mButItem.get(i);
                        world.addObject(obj);
                    }
                    lastaddsize = pagesize;

                }

            }
            loadtexture("Menubox", mVrlist.GetBmp());
            loadtexture("bj_hot.png", mVrlist.GetHotBmp());
        }
    }

    private void loadtexture(String Name, Bitmap bmp) {

        if (bmp != null) {
            Texture uv1 = new Texture(BitmapHelper.rescale(bmp, 512, 512));
            uv1.setClamping(true);
            if (TextureManager.getInstance().getTextureID(Name) > -1) {
                TextureManager.getInstance().replaceTexture(
                        Name, uv1
                );
            } else {
                TextureManager.getInstance().addTexture(
                        Name, uv1
                );
            }
        }
    }

    private void loadtexture(String Name, int w) {

        try {
            if (TextureManager.getInstance().getTextureID(Name) < 0) {
                Texture uv1 = new Texture(BitmapHelper.rescale(BitmapHelper
                        .loadImage(mContext.getAssets().open(
                                Name)), w, w));
                uv1.setClamping(true);
                TextureManager.getInstance().addTexture(
                        Name, uv1
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadtexture(String Name) {

        try {
            if (TextureManager.getInstance().getTextureID(Name) < 0) {
                Texture uv1 = new Texture(BitmapHelper.rescale(BitmapHelper
                        .loadImage(mContext.getAssets().open(
                                Name)), 512, 512));
                uv1.setClamping(true);
                TextureManager.getInstance().addTexture(
                        Name, uv1
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadtexture(String Name, int w, int h) {

        try {
            if (TextureManager.getInstance().getTextureID(Name) < 0) {
                Texture uv1 = new Texture(BitmapHelper.rescale(BitmapHelper
                        .loadImage(mContext.getAssets().open(
                                Name)), w, h));
                uv1.setClamping(true);
                TextureManager.getInstance().addTexture(
                        Name, uv1
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 333:
                    //mVrRenderer.SetTexture(TexPath);
                    break;
                case 8888:
                    if (showmenu == 0) {
                        if (mMenuSet.getID() == msg.arg1) {
                            Intent startIntent = new Intent();
                            startIntent.setClassName("com.android.settings", "com.android.settings.Settings");
                            mContext.startActivity(startIntent);

                            //showmenu=1;
                        } else if (mMenuVid.getID() == msg.arg1) {

                            mVrlist.SetList(mList360, showmenu, "bj_360.png");
                            mVrlist.Draw();
                            updateHots = true;
                            showmenu = 2;

                        } else if (mMenuGame.getID() == msg.arg1) {

                            mVrlist.SetList(mListgame, 3, "bj_game.png");
                            mVrlist.Draw();
                            updateHots = true;
                            showmenu = 3;
                        } else {
                            showmenu = 0;
                        }
                    } else {
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

                            int pagesize = mButItem.size();
                            if (pagesize > mVrlist.GetPageSize())
                                pagesize = mVrlist.GetPageSize();
                            int SelId = -1;
                            for (int i = 0; i < pagesize; i++) {
                                Object3D obj = mButItem.get(i);
                                if (obj.getID() == msg.arg1) {
                                    SelId = i;
                                    break;
                                }
                            }
                            if (SelId != -1) {
                                SelId = mVrlist.Page * mVrlist.PageSize + SelId;
                                if (showmenu == 3 && SelId < mVrlist.GetPageSize()) {
                                    if (SelId == 0) {
                                        String PackName = "com.archiactinteractive.LfGC";

                                        Intent LaunchIntent = mContext.getPackageManager().getLaunchIntentForPackage(PackName);

                                        if (LaunchIntent != null)
                                            mContext.startActivity(LaunchIntent);
                                        else {
                                            PackName = "com.iphodroid.Rollercoaster";

                                            LaunchIntent = mContext.getPackageManager().getLaunchIntentForPackage(PackName);

                                            if (LaunchIntent != null)
                                                mContext.startActivity(LaunchIntent);

                                        }
                                    } else if (SelId == 1) {
                                        String PackName = "com.iphodroid.Rollercoaster";

                                        Intent LaunchIntent = mContext.getPackageManager().getLaunchIntentForPackage(PackName);

                                        if (LaunchIntent != null)
                                            mContext.startActivity(LaunchIntent);
                                    }
                                } else if (showmenu == 2 && SelId < mPlayVideoList360.size()) {
                                    int in = SelId;

                                    Log.i("aa", "handleMessage: ----------");
                                    ArrayList<String> list = null;
                                    Intent intent = null;
                                    intent = new Intent(mContext, Player360ViedoActivity.class);
                                    list = mPlayVideoList360;
                                    if (list != null && intent != null) {
                                        Bundle bundle = new Bundle();
                                        bundle.putStringArrayList("list", list);
                                        bundle.putInt("index", in);
                                        bundle.putInt("Vr", showmenu);
                                        intent.putExtra("playlist", bundle);
                                        mContext.startActivity(intent);
                                    }
                                }

                            }
                        }
                    }

                    //if(mLikethis!=null && msg.arg1>-1)
                    //{
                    //	mLikethis.OnItem(msg.arg1);
                    //}
                    break;
            }
        }
    };

    class VrRenderer implements GLSurfaceView.Renderer {


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


        private long times = System.currentTimeMillis();


        public VrRenderer() {
        }

        private Matrix mMatrix;

        public void onSurfaceChanged(GL10 gl, int w, int h) {
            mHeight = h;
            mWidth = w;
            if (fb != null) {
                fb.dispose();
            }
            if (mHandler2 != null) {
                Message message = new Message();
                message.what = 7777;

                mHandler2.sendMessageDelayed(message, 0);
            }
            //fb = new FrameBuffer(gl, w, h);
            //	fb = new FrameBuffer(gl, w/2, h);
            fb = new FrameBuffer(w / 2, h);
            //Log.e("Ar110","w="+w+",h="+h);
            //if (master == null) {
            if (world != null)
                world.removeAllObjects();
            world = new World();
            world.setAmbientLight(200, 200, 200);
            //world.setAmbientLight(255, 255, 255);

            sun = new Light(world);
            sun.setIntensity(255, 255, 255);

            // Create a texture out of the icon...:-)
            if (TextureManager.getInstance().getTextureID("texture") < 0) {
                //Log.e("Ar110","TextureManager="+TextureManager.getInstance().getTextureID("texture"));
                Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.ic_launcher)), 64, 64));
                TextureManager.getInstance().addTexture("texture", texture);
                font = new Texture(mContext.getResources().openRawResource(R.raw.numbers));
                font.setMipmap(false);
            }
            loadDefault();
            Camera cam = world.getCamera();
            cam.moveCamera(Camera.CAMERA_MOVEIN, 4f);

            SimpleVector lookVector = new SimpleVector(0, 0, 0);
            cam.lookAt(lookVector);
            mMatrix = cam.getBack();
            SimpleVector sv = new SimpleVector();
            sv.set(object3d.getTransformedCenter());
            //sv.y -= 100;
            //sv.z -= 100;

            sun.setPosition(sv);
            msid = 0;
            updateHots = true;
            MemoryHelper.compact();
            if (mHandler2 != null) {
                Message message = new Message();
                message.what = 8888;

                mHandler2.sendMessageDelayed(message, 0);
            }


        }
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
                ;
                float pr = (dx * (float) Math.sin(anglefist + (i + 1) * angleStep));
                float maxz = 3.6f + (zz * (float) Math.cos(anglefist + i * angleStep));
                float minz = 3.6f + (zz * (float) Math.cos(anglefist + (i + 1) * angleStep));

                button.addTriangle(new SimpleVector(pl, endh, maxz), onetx, textendV,
                        new SimpleVector(pl, fisth, maxz), onetx, textfistV,
                        new SimpleVector(pr, fisth, minz), zeotx, textfistV,
                        TextureManager.getInstance().getTextureID("bj_hot.png"));

                button.addTriangle(new SimpleVector(pl, endh, maxz), onetx, textendV,
                        new SimpleVector(pr, fisth, minz), zeotx, textfistV,
                        new SimpleVector(pr, endh, minz), zeotx, textendV,
                        TextureManager.getInstance().getTextureID("bj_hot.png"));


            }
            button.build();
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
                ;
                float pr = (dx * (float) Math.sin(anglefist + (i + 1) * angleStep));
                float maxz = 3.6f + (zz * (float) Math.cos(anglefist + i * angleStep));
                float minz = 3.6f + (zz * (float) Math.cos(anglefist + (i + 1) * angleStep));

                menubox.addTriangle(new SimpleVector(pl, FH + MH, maxz), onetx, 1.0f,
                        new SimpleVector(pl, -FH + MH, maxz), onetx, 0.0f,
                        new SimpleVector(pr, -FH + MH, minz), zeotx, 0.0f,
                        TextureManager.getInstance().getTextureID("Menubox"));

                menubox.addTriangle(new SimpleVector(pl, FH + MH, maxz), onetx, 1.0f,
                        new SimpleVector(pr, -FH + MH, minz), zeotx, 0.0f,
                        new SimpleVector(pr, FH + MH, minz), zeotx, 1.0f,
                        TextureManager.getInstance().getTextureID("Menubox"));


            }
            menubox.build();

            if (mButRetu != null)
                mButRetu.clearObject();
            if (mButUP != null)
                mButUP.clearObject();
            if (mButDown != null)
                mButDown.clearObject();
            for (int i = 0; i < mButItem.size(); i++) {
                Object3D obj = mButItem.get(i);
                if (obj != null)
                    obj.clearObject();
            }
            mButItem.clear();
            if (mButItem.size() == 0) {
                mButRetu = esSectorButton("bun_retun", 1, 484, 16, 64, 64);


                mButUP = esSectorButton("mButUP", 1, 191, 16, 64, 64);


                mButDown = esSectorButton("mButDown", 1, 385, 16, 64, 64);


                mButItem.add(esSectorButton("mButItem00", 1, 4, 98, 152, 100));


                mButItem.add(esSectorButton("mButItem01", 1, 164, 98, 152, 100));


                mButItem.add(esSectorButton("mButItem02", 1, 324, 98, 152, 100));


                mButItem.add(esSectorButton("mButItem03", 1, 484, 98, 152, 100));


                mButItem.add(esSectorButton("mButItem10", 1, 4, 210, 152, 100));


                mButItem.add(esSectorButton("mButItem11", 1, 164, 210, 152, 100));


                mButItem.add(esSectorButton("mButItem12", 1, 324, 210, 152, 100));


                mButItem.add(esSectorButton("mButItem13", 1, 484, 210, 152, 100));
            }


        }

        private void loadDefault() {

            if (object3d != null)
                object3d.clearObject();
            if (mMenuSet != null)
                mMenuSet.clearObject();

            if (mMenuVid != null)
                mMenuVid.clearObject();

            if (mMenuGame != null)
                mMenuGame.clearObject();


            if (!TextureManager.getInstance().containsTexture("tklc")) {
                try {
                    loadtexture("ball.png", 1024, 512);
                    loadtexture("rock_001_c.png", 1024, 1024);
                    loadtexture("rock_002_c.png", 1024, 1024);
                    loadtexture("rock_003_c.png", 1024, 1024);
                    loadtexture("rock_004_c.png", 1024, 1024);
                    loadtexture("rock_005_c.png", 1024, 1024);
                    loadtexture("rock_010_c.png", 1024, 1024);

                    //loadtexture("uv.jpg");
                    //loadtexture("uv2.jpg");
                    loadtexture("vid.png", 256);
                    loadtexture("img.png", 256);
                    loadtexture("set.png", 256);
                    //loadtexture("pin.jpg");
                    //loadtexture("pin1.jpg");
                    //loadtexture("co1.jpg");
                    //loadtexture("co2.jpg");
                    //loadtexture("logo.png");
                    loadtexture("set_hot.png", 256);
                    loadtexture("vid_hot.png", 256);
                    loadtexture("img_hot.png", 256);
                    loadtexture("bj_game.png");
                    loadtexture("Menubox", mVrlist.GetBmp());
                    loadtexture("bj_hot.png", mVrlist.GetHotBmp());


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            try {
                Object3D[] model = Loader.loadOBJ(
                        mContext.getAssets().open("st.obj"), mContext.getAssets().open("st.mtl"), 2f);
                object3d = new Object3D(0);
                Object3D temp = null;
                //	Log.e("Ar110","model.length="+model.length);
                for (int i = 0; i < model.length; i++) {
                    temp = model[i];
                    //if(i>34 && i<40) // 35 36 37 38 39
                    //{
                    //
                    //}else
                    {
                        object3d = Object3D.mergeObjects(object3d, temp);
                        object3d.compile();
                    }
                }
                //object3d.setTexture("hlg_03");
                object3d.setCulling(Object3D.CULLING_DISABLED);

                object3d.build();
               // object3d.setScale(5.0f);

                //object3d.translate(0,-1.5f,0);
                //object3d.translate(0,20,-38);
                //object3d.translate(0,200,-400);

                //object3d.translate(0,22,-40);
                //object3d.translate(0, 110, -200);
                object3d.rotateX(2.9f);
                object3d.rotateY(0.5f);

               // object3d.rotateZ(3.1415926535897f);
               // object3d.rotateY(3.1415926535897f);

                mMenuVid = new Object3D(2);
                mMenuSet = new Object3D(2);
                mMenuGame = new Object3D(2);
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
                mMenuSet.build();

                pl = -0.56f;
                pr = 0.6f;
                pb = 1.2f;
                pt = 0.2f;
                zz = 0.13f;
                mMenuGame.addTriangle(new SimpleVector(pl, pb, zz), 1.0f, 1.0f,
                        new SimpleVector(pl, pt, zz), 1.0f, 0.0f,
                        new SimpleVector(pr, pt, zz), 0.0f, 0.0f,
                        TextureManager.getInstance().getTextureID("img.png"));

                mMenuGame.addTriangle(new SimpleVector(pl, pb, zz), 1.0f, 1.0f,
                        new SimpleVector(pr, pt, zz), 0.0f, 0.0f,
                        new SimpleVector(pr, pb, zz), 0.0f, 1.0f,
                        TextureManager.getInstance().getTextureID("img.png"));

                mMenuGame.build();

                esSector(32);

                world.addObject(object3d);
                world.addObject(mMenuSet);
                world.addObject(mMenuVid);
                world.addObject(mMenuGame);
                world.addObject(menubox);
                world.removeObject(menubox);
                mMenuVid.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
                mMenuSet.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
                mMenuGame.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);


                MemoryHelper.compact();
            } catch (Exception e) {
                //	//Toast.makeText(mContext, "3ds", Toast.LENGTH_SHORT).show();
                Log.e("Ar110", "3ds");
                object3d = null;
                e.printStackTrace();
            }
        }

        float lasty = 0;
        float lastx = 0;
        float lastz = 0;

        public void onDrawFrame(GL10 gl) {

            mHeadTracker.getLastHeadView(mHeadView, 0);
            android.opengl.Matrix.perspectiveM(projectionMatrix, 0, 75.0f, mWidth / mHeight / 2.0f, 0.1f, 10000.0f);
            android.opengl.Matrix.setIdentityM(modelViewMatrix, 0);
            android.opengl.Matrix.multiplyMM(temp, 0, projectionMatrix, 0, mHeadView, 0);
            android.opengl.Matrix.multiplyMM(temp, 0, temp, 0, mVMatrix, 0);

            float[] EulerAngles = new float[3];
            float[] ForwardVector = new float[3];
            float[] t;
            Camera cam = world.getCamera();
            Matrix m = mMatrix.cloneMatrix();
            mHeadTracker.getLastHeadView(mHeadTransform.getHeadView(), 0);
            mHeadTransform.getEulerAngles(EulerAngles, 0);
            mHeadTransform.getForwardVector(ForwardVector, 0);
            EulerAngles[0] = (EulerAngles[0] + (float) Math.PI / 2.0f);

            //SimpleVector dir = Interact2D.reproject2D3DWS( cam, fb, fX, fY).normalize();
            SimpleVector dir = Interact2D.reproject2D3DWS(cam, fb, mWidth /4, mHeight / 2).normalize();
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

                    int pagesize = mButItem.size();
                    //if(pagesize>mVrlist.GetPageSize())
                    //pagesize=mVrlist.GetPageSize();
                    for (int i = 0; i < pagesize; i++) {
                        Object3D obj = mButItem.get(i);
                        if (obj.getID() == pickid)
                            obj.setTexture("bj_hot.png");
                        else
                            obj.setTexture("Menubox");

                    }


                } else {
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
                    if (mMenuGame.getID() == pickid) {
                        mMenuGame.setTexture("img_hot.png");
                    } else {
                        mMenuGame.setTexture("img.png");
                    }
                }

            } else {
                if (msid > 0) {
                    mButRetu.setTexture("Menubox");
                    mButUP.setTexture("Menubox");
                    mButDown.setTexture("Menubox");
                    int pagesize = mButItem.size();
                    //if(pagesize>mVrlist.GetPageSize())
                    //pagesize=mVrlist.GetPageSize();
                    for (int i = 0; i < pagesize; i++) {
                        Object3D obj = mButItem.get(i);
                        obj.setTexture("Menubox");

                    }
                } else {
                    mMenuSet.setTexture("set.png");
                    mMenuVid.setTexture("vid.png");
                    mMenuGame.setTexture("img.png");
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

            //GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            //GLES20.glEnable(GL10.GL_BLEND);


            if (lasty == 0) {
                ;
            } else {

                Matrix mheadm = new Matrix();
                t = mHeadTransform.getHeadView();
                mheadm.setRow(0, t[0], -1.0f * t[1], -1.0f * t[2], t[3]);
                mheadm.setRow(1, -1.0f * t[4], t[5], t[6], t[7]);
                mheadm.setRow(2, -1.0f * t[8], t[9], t[10], t[11]);
                mheadm.setRow(3, t[12], t[13], t[14], t[15]);

                m.matMul(mheadm);
                cam.setBack(m);
                //cam.rotateCameraX(lastx-EulerAngles[0]);
                //object3d.rotateX(lastx-EulerAngles[0]);
                //object3d.rotateZ(EulerAngles[1]-lasty);
                //object3d.rotateY(EulerAngles[2]-lastz);
                //cam.rotateCameraY(lasty-EulerAngles[1]);
            }
            lastz = EulerAngles[2];
            lasty = EulerAngles[1];
            lastx = EulerAngles[0];
            //object3d.setTranslationMatrix(new Matrix().);

            world.renderScene(fb);
            GLES20.glViewport(0, 0, fb.getWidth(), fb.getHeight());
            world.draw(fb);
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
            //m.rotateY(0.1f);
            //float movx = m.get(3, 0);
            //m.set(3,0,movx-0.25f);

            //	m.set(3,0,movx-2.25f);

           // m.set(3, 0, movx - 2.25f);

           // m.rotateY(0.12f);
            //cam.rotateCameraY(1);
          //  cam.setBack(m);
            world.draw(fb);
            GLES20.glUseProgram(mProgram);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, temp, 0);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
            GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            GLES20.glEnableVertexAttribArray(maTexCoorHandle);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, count, GLES20.GL_UNSIGNED_SHORT, IndicesBuffer);
            //	GLES20.glViewport(0, 0, fb.getWidth(), fb.getHeight());
            //blitNumber(lfps, 5, 5);
            fb.display();


            if (System.currentTimeMillis() - times >= 1000) {
                //	Logger.log(fps + "fps");
                lfps = fps;
                fps = 0;
                times = System.currentTimeMillis();
            }
            fps++;
        }

        @Override
        public void onSurfaceCreated(GL10 arg0, javax.microedition.khronos.egl.EGLConfig arg1) {
            // TODO Auto-generated method stub


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

        private void blitNumber(int number, int x, int y) {
            if (font != null) {
                String sNum = Integer.toString(number);
                for (int i = 0; i < sNum.length(); i++) {
                    char cNum = sNum.charAt(i);
                    int iNum = cNum - 48;
                    fb.blit(font, iNum * 5, 0, x, y, 5, 9, 5 * 4, 9 * 4, 10, true, null);
                    x += 20;
                }
            }
        }
    }

    private Handler mHandler2;

    public void SetHandler(Handler handler) {
        mHandler2 = handler;

    }

}

