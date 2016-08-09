package zengshi.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import zengshi.test.cardboard2.sensor.HeadTracker;
import zengshi.test.cardboard2.sensor.HeadTransform;

/**
 * Created by 111 on 2016/8/8.
 */
public class ImageView extends MyGLSurfaceView {

    private boolean isLoad = true;

    private static final String TAG = "VRImageView";
    private Context mContext;
    private MyRenderer mVrRenderer;
    private Object3D mSector;
    private Object3D mSector1;
    private String mPath ;

    public ImageView(Context context) {
        super(context);
        init(context);

    }

    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setPath(String path ){

        mPath = path;
    }

    private void init(Context context) {
        mContext = context;
        mVrRenderer = new MyRenderer();
        setRenderer(mVrRenderer);
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

    private Bitmap getBitmap(String path) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        int inSampleSize = imageWidth / 1280;

        Bitmap bitmap = null;
        BitmapFactory.Options noptions = new BitmapFactory.Options();
        noptions.inSampleSize = inSampleSize;
        bitmap = BitmapFactory.decodeFile(path, noptions);
        return bitmap;
    }

    private Bitmap getBitmap1(){

        Bitmap bitmapTmp = BitmapFactory.decodeResource(getResources(), R.drawable.bitmap1);

        return bitmapTmp;
    }


    public class MyRenderer implements Renderer {

        static final float FH = 1.2f;
        static final float MH = 0.2f;
        static final float zz = -3.0f; //1.2f;
        static final float dx = 3.0f;//2.5f;

        private RGBColor back = new RGBColor(0, 0, 0);
        private int mWidth, mHeight;
        private FrameBuffer fb = null;
        private World world = null;
        private Matrix mMatrix;
        private Bitmap mBitmap;
        private Bitmap mBitmap1;

        private void esSector(int m) {

            float anglefist = -2.0f * (float) Math.PI * 75.0f / 360f / 2;
            float angleStep = 2.0f * (float) Math.PI * 75.0f / 360f / m;
            float texStep = 1.0f / m;

            if (mSector != null)
                mSector.clearObject();
            mSector = new Object3D(2 * m);
            for (int i = 0; i < m; i++) {
                float onetx = 1.0f - i * texStep;
                float zeotx = 1.0f - (i + 1) * texStep;
                float pl = (dx * (float) Math.sin(anglefist + i * angleStep));
                float pr = (dx * (float) Math.sin(anglefist + (i + 1) * angleStep));
                float maxz = 3.6f + (zz * (float) Math.cos(anglefist + i * angleStep));
                float minz = 3.6f + (zz * (float) Math.cos(anglefist + (i + 1) * angleStep));

                mSector.addTriangle(
                        new SimpleVector(pl, FH + MH, maxz), onetx, 1.0f,
                        new SimpleVector(pl, -FH + MH, maxz), onetx, 0.0f,
                        new SimpleVector(pr, -FH + MH, minz), zeotx, 0.0f,
                        TextureManager.getInstance().getTextureID("mSector"));

                mSector.addTriangle(
                        new SimpleVector(pl, FH + MH, maxz), onetx, 1.0f,
                        new SimpleVector(pr, -FH + MH, minz), zeotx, 0.0f,
                        new SimpleVector(pr, FH + MH, minz), zeotx, 1.0f,
                        TextureManager.getInstance().getTextureID("mSector"));
            }
            mSector.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
            mSector.build();


            if (mSector1 != null)
                mSector1.clearObject();
            mSector1 = new Object3D(2 * m);
            for (int i = 0; i < m; i++) {
                float onetx = 1.0f - i * texStep;
                float zeotx = 1.0f - (i + 1) * texStep;
                float pl = (dx * (float) Math.sin(anglefist + i * angleStep))/2.5f;
                float pr = (dx * (float) Math.sin(anglefist + (i + 1) * angleStep))/2.5f;
                float maxz = 3.6f + (zz * (float) Math.cos(anglefist + i * angleStep));
                float minz = 3.6f + (zz * (float) Math.cos(anglefist + (i + 1) * angleStep));

                mSector1.addTriangle(
                        new SimpleVector(pl, FH +2.6f, maxz), onetx, 1.0f,
                        new SimpleVector(pl, -FH +2.6f, maxz), onetx, 0.0f,
                        new SimpleVector(pr, -FH +2.6f, minz), zeotx, 0.0f,
                        TextureManager.getInstance().getTextureID("mSector1"));

                mSector1.addTriangle(
                        new SimpleVector(pl, FH +2.6f, maxz), onetx, 1.0f,
                        new SimpleVector(pr, -FH +2.6f, minz), zeotx, 0.0f,
                        new SimpleVector(pr, FH +2.6f, minz), zeotx, 1.0f,
                        TextureManager.getInstance().getTextureID("mSector1"));
            }
            mSector1.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
            mSector1.build();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            mBitmap = getBitmap(mPath);
            mBitmap1 = getBitmap1();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

            mWidth = width;
            mHeight = height;
            if (fb != null) {
                fb.dispose();
            }
            fb = new FrameBuffer(mWidth / 2, mHeight);
            world = new World();
            world.setAmbientLight(255, 255, 255);
            loadtexture("mSector", mBitmap);
            loadtexture("mSector1", mBitmap1);

            esSector(32);
            world.addObject(mSector);
            world.addObject(mSector1);
            Camera cam = world.getCamera();
            cam.moveCamera(Camera.CAMERA_MOVEIN, 4f);

            SimpleVector lookVector = new SimpleVector(0, 0, 0);
            cam.lookAt(lookVector);
            mMatrix = cam.getBack();
            MemoryHelper.compact();
        }

        @Override
        public void onDrawFrame(GL10 gl) {

            float[] t;
            Camera cam = world.getCamera();
            Matrix m = mMatrix.cloneMatrix();
            mHeadTracker.getLastHeadView(mHeadTransform.getHeadView(), 0);
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
            GLES20.glViewport(fb.getWidth(), 0, fb.getWidth(), fb.getHeight());
            world.draw(fb);
            fb.display();
            if (isLoad){
                isLoad = false;
               // loadtexture("mSector", mBitmap);
               // loadtexture("mSector1", mBitmap1);
            }
        }
    }

}
