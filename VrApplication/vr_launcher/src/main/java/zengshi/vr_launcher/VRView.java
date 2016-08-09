package zengshi.vr_launcher;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

import zengshi.vr_launcher.cardboard2.sensor.HeadTracker;
import zengshi.vr_launcher.cardboard2.sensor.HeadTransform;

public class VRView extends GLSurfaceView {
    private VrRenderer mVrRenderer;
    private VRListImg mVrlist = null;
    private List<ListItem> mList360 = new ArrayList<ListItem>();
    private ArrayList<String> mPlayVideoList360 = new ArrayList<String>();
    private List<ListItem> mListgame = new ArrayList<ListItem>();

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
    private Object3D mButItem00;
    private Object3D mButItem01;
    private Object3D mButItem02;
    private Object3D mButItem03;
    private Object3D mButItem10;
    private Object3D mButItem11;
    private Object3D mButItem12;
    private Object3D mButItem13;

    private int fps = 0;
    private int lfps = 0;
    private Light sun = null;
    private RGBColor back = new RGBColor(50, 50, 100);
    private Texture font = null;
    private Context mContext;
    private HeadTracker mHeadTracker;
    private HeadTransform mHeadTransform;
    private int hostid = 0;
    private int showmenu = 0;
//	private float[] mHeadView;

    public void onPause() {
        super.onPause();
        mHeadTracker.stopTracking();
    }

    public void onResume() {
        super.onResume();
        mHeadTracker.startTracking();
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

        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        mVrRenderer = new VrRenderer();
        setRenderer(mVrRenderer);
        mHeadTracker = HeadTracker.createFromContext(context);
        mHeadTransform = new HeadTransform();
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
                    /*if(path.indexOf("/VRResources/3D/")>0)
                    {
		    			mList3d.add(new ListItem(path,id,IsVideo));
		    			if(IsVideo)
		    			{
		    				mPlayVideoList3d.add(path);
		    			}else
		    			{
		    				mPlayImageList3d.add(path);
		    			}
		    		}
		    		else */
                if (path.indexOf("/VRResources/360/") > 0) {
                    mList360.add(new ListItem(path, id, IsVideo));
                    if (IsVideo) {
                        mPlayVideoList360.add(path);
                    }/*else
		    			{
		    				mPlayImageList360.add(path);
		    			}*/
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
        //mListvr.clear();
        //mList3d.clear();
        mList360.clear();

        //mPlayVideoListvr.clear();
        //mPlayVideoList3d.clear();
        mPlayVideoList360.clear();

        //mPlayImageListvr.clear();
        //mPlayImageList3d.clear();
        //mPlayImageList360.clear();

        mListgame.clear();

        //	MenuHotSet();

        //	GetList(MediaStore.Images.Media.INTERNAL_CONTENT_URI,false);
        //GetList(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,false);

        //GetList(MediaStore.Video.Media.INTERNAL_CONTENT_URI,true);
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
                    world.addObject(mButItem00);
                    world.addObject(mButItem01);
                    world.addObject(mButItem02);
                    world.addObject(mButItem03);
                    world.addObject(mButItem10);
                    world.addObject(mButItem11);
                    world.addObject(mButItem12);
                    world.addObject(mButItem13);

                }
            } else {
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
            loadtexture("Menubox", mVrlist.GetBmp());

            loadtexture("bj_hot.png", mVrlist.GetHotBmp());
        }
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


    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 333:
                    //mVrRenderer.SetTexture(TexPath);
                    break;
                case 8888:
                    if (showmenu == 0) {
                        if (mMenuSet.getID() == msg.arg1) {
                            //showmenu=1;
                        } else if (mMenuVid.getID() == msg.arg1) {
                            showmenu = 2;
                            mVrlist.SetList(mList360, showmenu, "bj_360.png");
                            mVrlist.Draw();
                            updateHots = true;

                        } else if (mMenuGame.getID() == msg.arg1) {
                            showmenu = 3;
                            mVrlist.SetList(mListgame, showmenu, "bj_game.png");
                            mVrlist.Draw();
                            updateHots = true;
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
                        }
                    }
                    break;
            }
        }
    };

    class VrRenderer implements GLSurfaceView.Renderer {

        private long times = System.currentTimeMillis();

        public VrRenderer() {
        }

        private Matrix mMatrix;

        public void onSurfaceChanged(GL10 gl, int w, int h) {
            if (fb != null) {
                fb.dispose();
            }
            //fb = new FrameBuffer(gl, w, h);
            //	fb = new FrameBuffer(gl, w/2, h);
            fb = new FrameBuffer(w / 2, h);
            Log.e("Ar110", "w=" + w + ",h=" + h);
            //if (master == null) {
            world = new World();
            world.setAmbientLight(64, 64, 64);
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
            MemoryHelper.compact();
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

                    loadtexture("uv1.jpg");
                    loadtexture("uv.jpg");
                    loadtexture("uv2.jpg");
                    loadtexture("vid.png", 256);
                    loadtexture("img.png", 256);
                    loadtexture("set.png", 256);
                    loadtexture("pin.jpg");
                    loadtexture("pin1.jpg");
                    loadtexture("co1.jpg");
                    loadtexture("co2.jpg");
                    loadtexture("logo.png");
                    loadtexture("set_hot.png", 256);
                    loadtexture("vid_hot.png", 256);
                    loadtexture("img_hot.png", 256);
                    //loadtexture("bj_game.png");
                    loadtexture("Menubox", mVrlist.GetBmp());

                    loadtexture("bj_hot.png", mVrlist.GetHotBmp());


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            try {
                Object3D[] model = Loader.loadOBJ(
                        mContext.getAssets().open("tkl.obj"), mContext.getAssets().open("tkl.mtl"), 0.4f);
                object3d = new Object3D(0);
                Object3D temp = null;
                //	Log.e("Ar110","model.length="+model.length);
                for (int i = 0; i < model.length; i++) {
                    temp = model[i];
                    if (i > 34 && i < 40) // 35 36 37 38 39
                    {

                    } else {
                        object3d = Object3D.mergeObjects(object3d, temp);
                        object3d.compile();
                    }
                }
                //object3d.setTexture("hlg_03");
                object3d.setCulling(Object3D.CULLING_DISABLED);

                object3d.build();


                object3d.translate(0, -1.5f, 0);

                object3d.rotateZ(3.1415926535897f);

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
                //	//Toast.makeText(mContext, "����3ds����", Toast.LENGTH_SHORT).show();
                Log.e("Ar110", "����3ds����");
                object3d = null;
                e.printStackTrace();
            }
        }

        float lasty = 0;
        float lastx = 0;
        float lastz = 0;

        public void onDrawFrame(GL10 gl) {
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
            SimpleVector dir = Interact2D.reproject2D3DWS(cam, fb, 640, 720).normalize();
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
                    mButItem00.setTexture("Menubox");
                    mButItem01.setTexture("Menubox");
                    mButItem02.setTexture("Menubox");
                    mButItem03.setTexture("Menubox");
                    mButItem10.setTexture("Menubox");
                    mButItem11.setTexture("Menubox");
                    mButItem12.setTexture("Menubox");
                    mButItem13.setTexture("Menubox");
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
            GLES20.glViewport(fb.getWidth(), 0, fb.getWidth(), fb.getHeight());
            //m.rotateY(0.1f);
            float movx = m.get(3, 0);
            m.set(3, 0, movx - 0.25f);
            //m.rotateY(0.1f);
            //cam.rotateCameraY(1);
            cam.setBack(m);
            world.draw(fb);
            //	GLES20.glViewport(0, 0, fb.getWidth(), fb.getHeight());
            blitNumber(lfps, 5, 5);
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
        public void onSurfaceCreated(GL10 arg0,
                                     javax.microedition.khronos.egl.EGLConfig arg1) {
            // TODO Auto-generated method stub
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
}

