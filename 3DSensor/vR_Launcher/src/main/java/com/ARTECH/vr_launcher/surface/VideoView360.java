package com.ARTECH.vr_launcher.surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;

import com.ARTECH.vr_launcher.R;
import com.ARTECH.vr_launcher.surface.VRGLSurfaceView;

public class VideoView360 extends VRGLSurfaceView {
    public static final int PLAYPAUSE = 3001;
    public static final int PLAYERROR = 3002;
    public static final int PLAYEND = 3003;
    public static final int MSGSTOP = 4066;
    public static final int Voiceplus = 4067;
    public static final int Voiceminus = 4068;
    public static final int HIDEMSG = 4060;
    public static final int SEEKLEFT = 4061;
    public static final int SEEKRIGHT = 4062;
    public static final int SEEKLEFTAdd = 4063;
    public static final int SEEKRIGHTAdd = 4064;

    private static final String TAG = "VideoView";
    VideoRender mRenderer;

    public VideoView360(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mRenderer = new VideoRender(context);
        setRenderer(mRenderer);
    }

    public VideoView360(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        mRenderer = new VideoRender(context);
        setRenderer(mRenderer);
    }

    public void SetPara(Handler handel, ArrayList<String> playlist, int index) {
        mRenderer.SetPara(handel, playlist, index);
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    public void pause() {
        mRenderer.pause();
    }

    public void seekTo(int seek) {

        mRenderer.seekTo(seek);
    }

    public void stop() {

        mRenderer.stopPlayback();
    }

    public long getDuration() {
        return mRenderer.getDuration();

    }

    public boolean GetInited() {
        return mRenderer.GetInited();
    }

    public long getCurrentPosition() {
        return mRenderer.getCurrentPosition();
    }

    @SuppressLint("NewApi")
    private static class VideoRender
            implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

        private final String mVertexShader =
                "uniform mat4 uMVPMatrix;\n" +
                        "uniform mat4 uSTMatrix;\n" +
                        "attribute vec4 aPosition;\n" +
                        "attribute vec4 aTextureCoord;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "void main() {\n" +
                        "  gl_Position = uMVPMatrix * aPosition;\n" +
                        "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                        "}\n";

        private final String mFragmentShader =
                "#extension GL_OES_EGL_image_external : require\n" +
                        "precision mediump float;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "uniform samplerExternalOES sTexture;\n" +
                        "void main() {\n" +
                        "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                        "}\n";

        private final String vertexShaderCode =
                "uniform mat4 u_MVPMatrix;" +
                        "attribute vec4 a_position;" +
                        "attribute vec2 a_texCoord;" +
                        "varying vec2 v_texCoord;" +
                        "void main()" +
                        "{" +
                        "    gl_Position = u_MVPMatrix * a_position;" +//"   gl_Position = modelViewProjectionMatrix * position;
                        "    v_texCoord = a_texCoord;" +
                        "}";


        private final String fragmentShaderCode =
                "precision lowp float;" +
                        "varying vec2 v_texCoord;" +
                        "uniform sampler2D u_samplerTexture;" +
                        "void main()" +
                        "{" +
                        "    gl_FragColor = texture2D(u_samplerTexture, v_texCoord);" +
                        "}";

        //   private float[] mMVPMatrix = new float[16];
        private float[] mSTMatrix = new float[16];
        private int mProgram2d;
        private int mProgram;
        private int mTextureID;
        private int muMVPMatrixHandle;
        private int muSTMatrixHandle;
        private int maPositionHandle;
        private int maTextureHandle;
        private FloatBuffer textureBuffer;
        private FloatBuffer vertexBuffer;
        private ShortBuffer IndicesBuffer;

        private SurfaceTexture mSurface;
        private boolean updateSurface = false;

        // Magic key
        private static int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
        private boolean Inited = false;
        private boolean mIsPause = false;
        private Handler mHandler;
        private ArrayList<String> mPlayList = null;
        private int mCurrentIndex = -1;

        private FloatBuffer bakvertexBuffer;
        private FloatBuffer baktextureBuffer;
        private int baktextureId = -1;
        static final float backx = -0.9f;
        static final float backy = 0.1f;
        float triangleCoords[] = {
                -backy, backx, backy,
                backy, backx, backy,
                backy, backx, -backy,
                -backy, backx, -backy,
        };

        float textureCoords[] =
                {
                        1.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 0.0f,
                        1.0f, 0.0f,
                };

        /**
         * Fields that reads video source and dumps to file.
         */
        // The media player that loads and decodes the video.
        // Not owned by this class.
        private MediaPlayer mMediaPlayer = null;


        public boolean GetInited() {
            return Inited;
        }

        public void seekTo(int seek) {
            if (mMediaPlayer != null)
                mMediaPlayer.seekTo(seek);
        }

        public int getDuration() {
            if (mMediaPlayer != null)
                return mMediaPlayer.getDuration();
            else
                return 0;
        }

        public int getCurrentPosition() {
            if (mMediaPlayer != null)
                return mMediaPlayer.getCurrentPosition();
            else
                return 0;

        }

        public void SetPara(Handler handel, ArrayList<String> playlist, int index) {
            mHandler = handel;
            mPlayList = playlist;
            mCurrentIndex = index;
        }

        int numVertices = 0;
        int numIndices = 0;

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
            short indices[] = new short[numIndices];
            for (i = 0; i < numParallels + 1; i++) {
                for (j = 0; j < numSlices + 1; j++) {
                    int vertex = (i * (numSlices + 1) + j) * 3;
                    vertices[vertex + 0] = (float) (d * Math.sin(angleStep * (float) i) * Math.sin(angleStep * (float) j));
                    vertices[vertex + 1] = (float) (d * Math.cos(angleStep * (float) i));
                    vertices[vertex + 2] = (float) (d * Math.sin(angleStep * (float) i) * Math.cos(angleStep * (float) j));

                    int texIndex = (i * (numSlices + 1) + j) * 2;
                    texCoords[texIndex + 0] = 1.0f - (float) j / (float) numSlices;
                    texCoords[texIndex + 1] = 1.0f - ((float) i / (float) numParallels);//((float)i/(float)numParallels);//
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


            ByteBuffer bb = ByteBuffer.allocateDirect(
                    vertices.length * 4);
            bb.order(ByteOrder.nativeOrder());

            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(vertices);
            vertexBuffer.position(0);

            ByteBuffer cc = ByteBuffer.allocateDirect(
                    texCoords.length * 4);
            cc.order(ByteOrder.nativeOrder());

            textureBuffer = cc.asFloatBuffer();
            textureBuffer.put(texCoords);
            textureBuffer.position(0);

            ByteBuffer dd = ByteBuffer.allocateDirect(
                    indices.length * 2);
            dd.order(ByteOrder.nativeOrder());

            IndicesBuffer = dd.asShortBuffer();
            IndicesBuffer.put(indices);
            IndicesBuffer.position(0);

            return numIndices;
        }

        //int[] _vertexBufferID;
        //int[] _vertexTexCoordID;
        //int[] _vertexIndicesBufferID;
        public void loadVertex() {

            esGenSphere(200, 1.0f);

            ByteBuffer bb = ByteBuffer.allocateDirect(
                    triangleCoords.length * 4);
            bb.order(ByteOrder.nativeOrder());

            bakvertexBuffer = bb.asFloatBuffer();
            bakvertexBuffer.put(triangleCoords);
            bakvertexBuffer.position(0);

            ByteBuffer cc = ByteBuffer.allocateDirect(
                    textureCoords.length * 4);
            cc.order(ByteOrder.nativeOrder());

            baktextureBuffer = cc.asFloatBuffer();
            baktextureBuffer.put(textureCoords);
            baktextureBuffer.position(0);

            //	_vertexBufferID = new int[1];

            GLES20.glEnableVertexAttribArray(maPositionHandle);
            GLES20.glVertexAttribPointer(maPositionHandle, 3,
                    GLES20.GL_FLOAT, false,
                    12, vertexBuffer);

            //_vertexTexCoordID = new int[1];

            GLES20.glEnableVertexAttribArray(maTextureHandle);
            GLES20.glVertexAttribPointer(maTextureHandle, 2,
                    GLES20.GL_FLOAT, false,
                    8, textureBuffer);

        }

        int loadTexture() {

            int[] textureId = new int[1];
            // Generate a texture object
            GLES20.glGenTextures(1, textureId, 0);

            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.br);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);
            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();

            return textureId[0];
        }

        private Context mContext = null;

        public VideoRender(Context context) {
            mContext = context;
            Matrix.setIdentityM(mSTMatrix, 0);
        }


        void perspectiveM(float[] m, float yFovInDegress, float aspect, float n, float f) {
            final float angleInRadians = (float) (yFovInDegress * Math.PI / 180.0);
            final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));
            m[0] = a / aspect;
            m[1] = 0f;
            m[2] = 0f;
            m[3] = 0f;

            m[4] = 0f;
            m[5] = a;
            m[6] = 0f;
            m[7] = 0f;

            m[8] = 0f;
            m[9] = 0f;
            m[10] = -((f + n) / (f - n));
            m[11] = -1f;

            m[12] = 0f;
            m[13] = 0f;
            m[14] = -((2f * f * n) / (f - n));
            m[15] = 0f;
        }

        float[] projectionMatrix = new float[16];
        float[] modelViewMatrix = new float[16];
        final float[] temp = new float[16];
        float rotatex = 0.0f;

        public void update() {

            perspectiveM(projectionMatrix, 85.0f, 640.0f / 720.0f, 0.1f, 400.0f);
            //Matrix.rotateM(projectionMatrix, 0, (float) Math.PI, 1.0f, 0.0f, 0.0f);

            Matrix.rotateM(projectionMatrix, 0, rotatex, 0.0f, 1.0f, 0.0f);

            Matrix.setIdentityM(modelViewMatrix, 0);
            Matrix.scaleM(modelViewMatrix, 0, 300.0f, 300.0f, 300.0f);
            float[] t = new float[16];
            Matrix.multiplyMM(t, 0, projectionMatrix, 0, modelViewMatrix, 0);
            Matrix.multiplyMM(temp, 0, t, 0, mHeadTransform.getHeadView(), 0);
            //System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
            GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, temp, 0);
        }


        private boolean lastshow = false;
        private boolean lastexit = false;


        /**
         * Called to draw the current frame.
         * This method is responsible for drawing the current frame.
         */
        public void onDrawFrame(GL10 glUnused) {
            // boolean isNewFrame = false;


            synchronized (this) {
                if (updateSurface) {
                    //       isNewFrame = true;
                    mSurface.updateTexImage();
                    mSurface.getTransformMatrix(mSTMatrix);
                    updateSurface = false;
                }
            }

            boolean isshow = false;
            boolean isexit = false;

            float[] EulerAngles = new float[3];
            //float[] ForwardVector = new float[3];
            mHeadTracker.getLastHeadView(mHeadTransform.getHeadView(), 0);
            mHeadTransform.getEulerAngles(EulerAngles, 0);
            if (EulerAngles[0] < -1.3)
                isshow = true;
            else
                isshow = false;

            if (lastshow != isshow) {
                int show = 0;
                if (isshow) show = 1;
                Message message = new Message();
                message.what = 3333;
                message.arg1 = show;
                mHandler.sendMessageDelayed(message, 0);
                lastshow = isshow;
            }

            if (EulerAngles[0] < -1.4)
                isexit = true;
            else
                isexit = false;
            if (lastexit != isexit) {
                mHandler.removeMessages(8888);
                if (isexit) {
                    Message messages = new Message();
                    messages.what = 8888;
                    messages.arg1 = 1;
                    mHandler.sendMessageDelayed(messages, 2000);
                }
                lastexit = isexit;
            }
            //Log.e("Ar110","EulerAngles="+EulerAngles[0]+","+EulerAngles[1]+","+EulerAngles[2]);
            // Initial clear.
            GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            // Load the program, which is the basics rules to draw the vertexes and textures.
            GLES20.glUseProgram(mProgram);
            checkGlError("glUseProgram");

            // Activate the texture.
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glVertexAttribPointer(maPositionHandle, 3,
                    GLES20.GL_FLOAT, false,
                    12, vertexBuffer);
            GLES20.glVertexAttribPointer(maTextureHandle, 2,
                    GLES20.GL_FLOAT, false,
                    8, textureBuffer);
            GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);

            GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

            update();
            GLES20.glViewport(0, 0, mWidth / 2, mHeight);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, numIndices, GLES20.GL_UNSIGNED_SHORT, IndicesBuffer);

            if (isshow) {
                GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                GLES20.glEnable(GL10.GL_BLEND);
                GLES20.glUseProgram(mProgram2d);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, baktextureId);
                //  private int mMVPMatrixHandle;
                //private int attribPosition;
                //private int attribTexCoord;
                GLES20.glEnableVertexAttribArray(attribPosition);
                GLES20.glEnableVertexAttribArray(attribTexCoord);
                GLES20.glVertexAttribPointer(attribPosition, 3,
                        GLES20.GL_FLOAT, false,
                        0, bakvertexBuffer);
                GLES20.glVertexAttribPointer(attribTexCoord, 2,
                        GLES20.GL_FLOAT, false,
                        0, baktextureBuffer);

                GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, temp, 0);

                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);

            }

            GLES20.glViewport(mWidth / 2, 0, mWidth / 2, mHeight);
            GLES20.glUseProgram(mProgram);
            GLES20.glVertexAttribPointer(maPositionHandle, 3,
                    GLES20.GL_FLOAT, false,
                    12, vertexBuffer);
            GLES20.glVertexAttribPointer(maTextureHandle, 2,
                    GLES20.GL_FLOAT, false,
                    8, textureBuffer);
            GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, numIndices, GLES20.GL_UNSIGNED_SHORT, IndicesBuffer);

            if (isshow) {
                GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                GLES20.glEnable(GL10.GL_BLEND);
                GLES20.glUseProgram(mProgram2d);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, baktextureId);
                //  private int mMVPMatrixHandle;
                //private int attribPosition;
                //private int attribTexCoord;
                GLES20.glEnableVertexAttribArray(attribPosition);
                GLES20.glEnableVertexAttribArray(attribTexCoord);
                GLES20.glVertexAttribPointer(attribPosition, 3,
                        GLES20.GL_FLOAT, false,
                        0, bakvertexBuffer);
                GLES20.glVertexAttribPointer(attribTexCoord, 2,
                        GLES20.GL_FLOAT, false,
                        0, baktextureBuffer);

                GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, temp, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);

            }

            checkGlError("glDrawArrays");
            GLES20.glFinish();


        }


        private int mIy = -1;


        /**
         * Called when the surface changed size.
         * Called after the surface is created and whenever the OpenGL surface size changes.
         */
        private int mWidth = 0;
        private int mHeight = 0;

        @Override
        public void onSurfaceChanged(GL10 arg0, int width, int height) {
            mWidth = width;
            mHeight = height;
            GLES20.glViewport(0, 0, width / 2, height);

        }

        private void pause() {
            if (mMediaPlayer != null) {
                if (mIsPause) {
                    mMediaPlayer.start();
                    mHandler.sendEmptyMessage(HIDEMSG);
                    mIsPause = false;
                } else {
                    mMediaPlayer.pause();
                    mHandler.sendEmptyMessage(PLAYPAUSE);
                    mIsPause = true;
                }
            }
        }

        public void stopPlayback() {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mIsPause = false;
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }

        MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mHandler.sendEmptyMessage(HIDEMSG);
                mIsPause = false;
            }
        };
        MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mHandler.sendEmptyMessage(PLAYEND);
                playernext();

            }
        };

        MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mHandler.sendEmptyMessage(PLAYERROR);
                playernext();
                return false;
            }

        };

        private void playernext() {
            mCurrentIndex++;
            if (mCurrentIndex >= mPlayList.size()) {
                mCurrentIndex = 0;
            }
            openVideo();
        }

        private void openVideo() {
            stopPlayback();
            if (mPlayList == null || mCurrentIndex < 0)
                return;
            if (mCurrentIndex >= mPlayList.size())
                return;

            mMediaPlayer = new MediaPlayer();

            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            //	mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            //mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            try {
                mMediaPlayer.setDataSource(mPlayList.get(mCurrentIndex));

                Surface surface = new Surface(mSurface);
                mMediaPlayer.setSurface(surface);
                surface.release();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setScreenOnWhilePlaying(true);

                mMediaPlayer.prepareAsync();
                Inited = true;

            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                mHandler.sendEmptyMessage(PLAYERROR);
                e.printStackTrace();
            } catch (SecurityException e) {
                mHandler.sendEmptyMessage(PLAYERROR);
                e.printStackTrace();
            } catch (IllegalStateException e) {
                mHandler.sendEmptyMessage(PLAYERROR);
                e.printStackTrace();
            } catch (IOException e) {
                mHandler.sendEmptyMessage(PLAYERROR);
                e.printStackTrace();
            }

        }

        private int mMVPMatrixHandle;
        private int attribPosition;
        private int attribTexCoord;

        public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {


            /* Set up shaders and handles to their variables */
            mProgram = createProgram(mVertexShader, mFragmentShader);
            if (mProgram == 0) {
                return;
            }

            maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
            checkGlError("glGetAttribLocation aPosition");
            if (maPositionHandle == -1) {
                throw new RuntimeException("Could not get attrib location for aPosition");
            }
            maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
            checkGlError("glGetAttribLocation aTextureCoord");
            if (maTextureHandle == -1) {
                throw new RuntimeException("Could not get attrib location for aTextureCoord");
            }

            muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            checkGlError("glGetUniformLocation uMVPMatrix");
            if (muMVPMatrixHandle == -1) {
                throw new RuntimeException("Could not get attrib location for uMVPMatrix");
            }

            muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
            checkGlError("glGetUniformLocation uSTMatrix");
            if (muSTMatrixHandle == -1) {
                throw new RuntimeException("Could not get attrib location for uSTMatrix");
            }


            // Create our texture. This has to be done each time the surface is created.
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);

            mTextureID = textures[0];
            GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
            checkGlError("glBindTexture mTextureID");

            // Can't do mipmapping with mediaplayer source
            GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR);
            // Clamp to edge is the only option
            GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);
            checkGlError("glTexParameteri mTextureID");
            loadVertex();

            mProgram2d = createProgram(vertexShaderCode, fragmentShaderCode);
            if (mProgram2d == 0) {
                return;
            }

            attribPosition = GLES20.glGetAttribLocation(mProgram2d, "a_position");

            attribTexCoord = GLES20.glGetAttribLocation(mProgram2d, "a_texCoord");

            //    uniformTexture = GLES20.glGetUniformLocation(mProgram, "u_samplerTexture");

            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram2d, "u_MVPMatrix");


            baktextureId = loadTexture();
            /*
             * Create the SurfaceTexture that will feed this textureID,
             * and pass it to the MediaPlayer
             */
            mSurface = new SurfaceTexture(mTextureID);
            mSurface.setOnFrameAvailableListener(this);


            openVideo();

            synchronized (this) {
                updateSurface = false;
            }
        }

        synchronized public void onFrameAvailable(SurfaceTexture surface) {
            /* For simplicity, SurfaceTexture calls here when it has new
             * data available.  Call may come in from some random thread,
             * so let's be safe and use synchronize. No OpenGL calls can be done here.
             */
            // mFrameNumber++;
            updateSurface = true;
        }

        private int loadShader(int shaderType, String source) {
            int shader = GLES20.glCreateShader(shaderType);
            if (shader != 0) {
                GLES20.glShaderSource(shader, source);
                GLES20.glCompileShader(shader);
                int[] compiled = new int[1];
                GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
                if (compiled[0] == 0) {
                    Log.e(TAG, "Could not compile shader " + shaderType + ":");
                    Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                    GLES20.glDeleteShader(shader);
                    shader = 0;
                }
            }
            return shader;
        }

        private int createProgram(String vertexSource, String fragmentSource) {
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
            if (vertexShader == 0) {
                return 0;
            }
            int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
            if (pixelShader == 0) {
                return 0;
            }

            int program = GLES20.glCreateProgram();
            if (program != 0) {
                GLES20.glAttachShader(program, vertexShader);
                checkGlError("glAttachShader");
                GLES20.glAttachShader(program, pixelShader);
                checkGlError("glAttachShader");
                GLES20.glLinkProgram(program);
                int[] linkStatus = new int[1];
                GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
                if (linkStatus[0] != GLES20.GL_TRUE) {
                    Log.e(TAG, "Could not link program: ");
                    Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                    GLES20.glDeleteProgram(program);
                    program = 0;
                }
            }
            return program;
        }

        private void checkGlError(String op) {
            int error;
            while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                Log.e(TAG, op + ": glError " + error);
                throw new RuntimeException(op + ": glError " + error);
            }
        }

    }
}

