package zengshi.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.Surface;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import zengshi.test.cardboard2.sensor.HeadTracker;
import zengshi.test.cardboard2.sensor.HeadTransform;

/**
 * Created by 111 on 2016/8/8.
 */
public class VideoView extends GLSurfaceView {
    private static HeadTransform mHeadTransform;
    private static HeadTracker mHeadTracker;
    private MyRenderer mRenderer;
    private float[] mHeadView = new float[16];
    private boolean isSended = false;

    private String pathString = null;
    private SurfaceTexture mSurface;
    private MediaPlayer mediaPlayer;


    private Object3D mSector;
    private Context mContext;
    private MyRenderer mVrRenderer;


    public VideoView(Context context) {
        super(context);
        init(context);
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new MyRenderer();       //创建场景渲染器
        setRenderer(mRenderer);             //设置渲染器
        mHeadTracker = HeadTracker.createFromContext(context);
        Matrix.setIdentityM(mHeadView, 0);
        setRenderMode(RENDERMODE_CONTINUOUSLY);

        mContext = context;
        mVrRenderer = new MyRenderer();
        setRenderer(mVrRenderer);

    }

    public void setPath(String path) {

        pathString = path;
    }

    public void onPause() {
        super.onPause();
        mHeadTracker.stopTracking();

        try {
            if (null != mediaPlayer && mediaPlayer.isPlaying()) {
                Constants.playPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();
        mHeadTracker.startTracking();

        if (Constants.playPosition >= 0) {

            if (null != mediaPlayer) {
                mediaPlayer.seekTo(Constants.playPosition);
                mediaPlayer.start();
            } else {
                mRenderer.openVideo();
            }
        }
    }

    public void onStop() {
        mRenderer.stopPlayback();
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

    public class MyRenderer implements Renderer, MediaPlayer.OnCompletionListener,
            MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {


        private int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

        private int mTextureID;

        static final float FH = 1.2f;
        static final float MH = 0.8f;
        static final float zz = -3.0f; //1.2f;
        static final float dx = 3.0f;//2.5f;

        private int mWidth, mHeight;
        private FrameBuffer fb = null;
        private World world = null;
        private com.threed.jpct.Matrix mMatrix;
        private RGBColor back = new RGBColor(0, 0, 0);

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
            mSector.build();
        }

        public void openVideo() {

            stopPlayback();
            // 初始化MediaPlayer
            mediaPlayer = new MediaPlayer();
            // 重置mediaPaly,建议在初始滑mediaplay立即调用。
            mediaPlayer.reset();
            // 设置播放完成监听
            mediaPlayer.setOnCompletionListener(this);
            // 设置媒体加载完成以后回调函数。
            mediaPlayer.setOnPreparedListener(this);
            // 错误监听回调函数
            mediaPlayer.setOnErrorListener(this);
            // 设置缓存变化监听
            try {
                mediaPlayer.setDataSource(pathString);
                Surface surface = new Surface(mSurface);

                mediaPlayer.setSurface(surface);
                //surface.release();
                // 设置声音效果
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setScreenOnWhilePlaying(true);
                // mediaPlayer.setDataSource(this, uri);
                // mediaPlayer.setDataSource(SurfaceViewTestActivity.this, uri);
                // 设置异步加载视频，包括两种方式 prepare()同步，prepareAsync()异步
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void stopPlayback() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // Create our texture. This has to be done each time the surface is created.
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);

            mTextureID = textures[0];
            GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
            ShaderUtil.checkGlError("glBindTexture mTextureID");

            // Can't do mipmapping with mediaplayer source
            GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            // Clamp to edge is the only option
            GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            ShaderUtil.checkGlError("glTexParameteri mTextureID");

            /*
             * Create the SurfaceTexture that will feed this textureID,
             * and pass it to the MediaPlayer
             */
            mSurface = new SurfaceTexture(mTextureID);

            openVideo();
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
           // loadtexture("mSector");
            esSector(32);
            world.addObject(mSector);
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
            com.threed.jpct.Matrix m = mMatrix.cloneMatrix();
            mHeadTracker.getLastHeadView(mHeadTransform.getHeadView(), 0);
            fb.clear(back);
            com.threed.jpct.Matrix mheadm = new com.threed.jpct.Matrix();
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
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            // 判断是否有保存的播放位置,防止屏幕旋转时，界面被重新构建，播放位置丢失。
            if (Constants.playPosition >= 0) {
                mediaPlayer.seekTo(Constants.playPosition);
                Constants.playPosition = -1;
            }
            mediaPlayer.start();
        }
    }
}
