package com.ARTECH.vr_launcher.surface;


import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.AttributeSet;

import com.ARTECH.vr_launcher.ListItem;
import com.ARTECH.vr_launcher.VRListImg;
import com.ARTECH.vr_launcher.activity.Player360ViedoActivity;
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

@SuppressLint("HandlerLeak")
public class VRView extends VRGLSurfaceView {

    private VRListImg mVrlist = null;
    private List<ListItem> mList360 = new ArrayList<ListItem>();
    private ArrayList<String> mPlayVideoList360 = new ArrayList<String>();
    private List<ListItem> mListgame = new ArrayList<ListItem>();

    private FrameBuffer fb = null;
    private World world = null;
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

    private RGBColor back = new RGBColor(50, 50, 100);
    private Context mContext;

    private int hostid = 0;
    private int showmenu = 0;

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
        VrRenderer mVrRenderer = new VrRenderer();
        setRenderer(mVrRenderer);
        GetList();
        mVrlist = new VRListImg(mContext, "bj_game.png");
        mVrlist.SetList(mList360, 0, "bj_360.png");
        mVrlist.Draw();
    }

    //获取信息列表
    private void GetList(Uri uri, Boolean IsVideo) {
        ContentResolver mContentResolver = mContext.getContentResolver();
        Cursor mCursor = mContentResolver.query(uri, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            int num = mCursor.getCount();
            if (num > 0) {
                do {
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    long id = mCursor.getLong(mCursor.getColumnIndex("_ID"));

                    if (path.indexOf("/VRResources/360/") > 0) {
                        mList360.add(new ListItem(path, id, IsVideo));
                        if (IsVideo) {
                            mPlayVideoList360.add(path);
                        }
                    }
                } while (mCursor.moveToNext());
            }
            mCursor.close();
        }
    }

    public void GetList() {
        mList360.clear();
        mPlayVideoList360.clear();
        mListgame.clear();

        //GetList(MediaStore.Images.Media.INTERNAL_CONTENT_URI,false);
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

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 8888) {
                if (showmenu == 0) {
                    if (mMenuSet.getID() == msg.arg1) {
                        Intent startIntent = new Intent();
                        startIntent.setClassName("com.android.settings", "com.android.settings.Settings");
                        mContext.startActivity(startIntent);
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

                                ArrayList<String> list = mPlayVideoList360;
                                Intent intent = new Intent(mContext, Player360ViedoActivity.class);
                                if (list != null) {
                                    Bundle bundle = new Bundle();
                                    bundle.putStringArrayList("list", list);
                                    bundle.putInt("index", SelId);
                                    bundle.putInt("Vr", showmenu);
                                    intent.putExtra("playlist", bundle);
                                    mContext.startActivity(intent);
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    class VrRenderer implements Renderer {

        private Light sun = null;

        private Matrix mMatrix;

        public void onSurfaceChanged(GL10 gl, int w, int h) {

            if (fb != null) {
                fb.dispose();
            }
            if (mHandler2 != null) {
                Message message = new Message();
                message.what = 7777;
                mHandler2.sendMessageDelayed(message, 0);
            }
            fb = new FrameBuffer(w / 2, h);
            //Log.e("Ar110","w="+w+",h="+h);
            //if (master == null) {
            if (world != null)
                world.removeAllObjects();
            world = new World();
            //设置灯光
            world.setAmbientLight(64, 64, 64);
            //world.setAmbientLight(255, 255, 255);
            sun = new Light(world);
            sun.setIntensity(255, 255, 255);

            //加载模型及纹理
            loadDefault();
            //设置摄像机
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

            if (mHandler2 != null) {//加载完成发送消息
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
                    TextureManager.getInstance().replaceTexture(Name, uv1);
                } else {
                    TextureManager.getInstance().addTexture(Name, uv1);
                }
            }
        }

        private void loadtexture(String Name, int w) {
            try {
                if (TextureManager.getInstance().getTextureID(Name) < 0) {
                    Texture uv1 = new Texture(BitmapHelper.rescale(BitmapHelper
                            .loadImage(mContext.getAssets().open(Name)), w, w));
                    uv1.setClamping(true);
                    TextureManager.getInstance().addTexture(Name, uv1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void loadtexture(String Name) {

            try {
                if (TextureManager.getInstance().getTextureID(Name) < 0) {
                    Texture uv1 = new Texture(BitmapHelper.rescale(BitmapHelper
                            .loadImage(mContext.getAssets().open(Name)), 512, 512));
                    uv1.setClamping(true);
                    TextureManager.getInstance().addTexture(Name, uv1);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                Object3D[] model = Loader.loadOBJ(mContext.getAssets().open("tkl.obj"),
                        mContext.getAssets().open("tkl.mtl"), 0.4f);
                object3d = new Object3D(0);
                Object3D temp;
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
                object3d = null;
                e.printStackTrace();
            }
        }

        public void onDrawFrame(GL10 gl) {
            float[] t;
            Camera cam = world.getCamera();
            Matrix m = mMatrix.cloneMatrix();
            mHeadTracker.getLastHeadView(mHeadTransform.getHeadView(), 0);

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

                    int pagesize = mButItem.size();
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
            Matrix mheadm = new Matrix();
            t = mHeadTransform.getHeadView();
            mheadm.setRow(0, t[0], -1.0f * t[1], -1.0f * t[2], t[3]);
            mheadm.setRow(1, -1.0f * t[4], t[5], t[6], t[7]);
            mheadm.setRow(2, -1.0f * t[8], t[9], t[10], t[11]);
            mheadm.setRow(3, t[12], t[13], t[14], t[15]);
            m.matMul(mheadm);
            cam.setBack(m);
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
            fb.display();
        }

        @Override
        public void onSurfaceCreated(GL10 arg0, javax.microedition.khronos.egl.EGLConfig arg1) {
            // TODO Auto-generated method stub
        }
    }

    private Handler mHandler2;

    public void SetHandler(Handler handler) {
        mHandler2 = handler;
    }
}


