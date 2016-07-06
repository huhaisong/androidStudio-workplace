package com.example.arplayer;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;


public class VideoView extends GLSurfaceView {
    private static final String TAG = "VideoView";
    VideoRender mRenderer;

    public VideoView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        mRenderer = new VideoRender();
        setRenderer(mRenderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);
        mRenderer = new VideoRender();
        setRenderer(mRenderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void SetPara(Handler handel, ArrayList<String> playlist, int index) {
        mRenderer.SetPara(handel, playlist, index);
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

    public void SetVrMode(boolean istrue) {
        mRenderer.SetVrMode(istrue);
    }

    @SuppressLint("NewApi")
    private class VideoRender
            implements Renderer, SurfaceTexture.OnFrameAvailableListener {

        private boolean isFrameBufferInited = false;
        private int mWidth, mHeight;

        IntBuffer texture = IntBuffer.allocate(1);
        IntBuffer framebuffer = IntBuffer.allocate(1);
        IntBuffer depthRenderbuffer = IntBuffer.allocate(1);

        IntBuffer texture2 = IntBuffer.allocate(1);
        IntBuffer framebuffer2 = IntBuffer.allocate(1);
        IntBuffer depthRenderbuffer2 = IntBuffer.allocate(1);

        IntBuffer texture3 = IntBuffer.allocate(1);
        IntBuffer framebuffer3 = IntBuffer.allocate(1);
        IntBuffer depthRenderbuffer3 = IntBuffer.allocate(1);

        private void initFrameBuffer() {

            int texWidth = mWidth / 2;
            int texHeight = mHeight;

            // generate the framebuffer, renderbuffer, and texture object names
            GLES20.glGenFramebuffers(1, framebuffer);
            GLES20.glGenRenderbuffers(1, depthRenderbuffer);
            GLES20.glGenTextures(1, texture);
            // bind texture and load the texture mip-level 0 texels are RGB565
            // no texels need to be specified as we are going to draw into the texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.get(0));
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, texWidth, texHeight,
                    0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            // bind renderbuffer and create a 16-bit depth buffer
            // width and height of renderbuffer = width and height of the texture
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderbuffer.get(0));
            GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                    texWidth, texHeight);
            // bind the framebuffer
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.get(0));
            // specify texture as color attachment
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, texture.get(0), 0);
            // specify depth_renderbufer as depth attachment
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_RENDERBUFFER, depthRenderbuffer.get(0));


            GLES20.glGenFramebuffers(1, framebuffer2);
            GLES20.glGenRenderbuffers(1, depthRenderbuffer2);
            GLES20.glGenTextures(1, texture2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture2.get(0));
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, texWidth, texHeight,
                    0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderbuffer2.get(0));
            GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                    texWidth, texHeight);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer2.get(0));
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, texture2.get(0), 0);
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_RENDERBUFFER, depthRenderbuffer2.get(0));


            GLES20.glGenFramebuffers(1, framebuffer3);
            GLES20.glGenRenderbuffers(1, depthRenderbuffer3);
            GLES20.glGenTextures(1, texture3);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture3.get(0));
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, texWidth, texHeight,
                    0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderbuffer3.get(0));
            GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                    texWidth, texHeight);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer3.get(0));
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, texture3.get(0), 0);
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_RENDERBUFFER, depthRenderbuffer3.get(0));

            isFrameBufferInited = true;
        }

        private void drawFrameBuffer() {
            GLES20.glViewport(0, 0, mWidth / 2, mHeight);

            GLES20.glUseProgram(mRightProgram);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture2.get(0));

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer3.get(0));

            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
            GLES20.glVertexAttribPointer(maPositionHandle, 2, GLES20.GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);

            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
            GLES20.glVertexAttribPointer(maRightTextureHandle, 2, GLES20.GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
            GLES20.glEnableVertexAttribArray(maRightTextureHandle);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            GLES20.glUseProgram(mRightProgram);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.get(0));

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer2.get(0));

            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
            GLES20.glVertexAttribPointer(maPositionHandle, 2, GLES20.GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);

            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
            GLES20.glVertexAttribPointer(maRightTextureHandle, 2, GLES20.GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
            GLES20.glEnableVertexAttribArray(maRightTextureHandle);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            GLES20.glUseProgram(mProgram);

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.get(0));

            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
            GLES20.glVertexAttribPointer(maPositionHandle, 2, GLES20.GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);

            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
            GLES20.glVertexAttribPointer(maRightTextureHandle, 2, GLES20.GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
            GLES20.glEnableVertexAttribArray(maRightTextureHandle);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            GLES20.glViewport(0, 0, mWidth, mHeight);
        }

        private static final int FLOAT_SIZE_BYTES = 4;
        private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
        private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
        private static final int TRIANGLE_VERTICES_DATA_LEFT_OFFSET = 4;
        private static final int TRIANGLE_VERTICES_DATA_RIGHT_OFFSET = 6;
        private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 2;
        private final float[] mTriangleVerticesData = {
                // X, Y, Z, U, V
                -1.0f, -1.0f, 0.f, 0.f, -1.0f, -1.0f, 0.0f, -1.0f,
                1.0f, -1.0f, 1.f, 0.f, 0.0f, -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 0.f, 1.f, -1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.f, 1.f, 0.0f, 1.0f, 1.0f, 1.0f,
        };

        private FloatBuffer mTriangleVertices;

        private final String mRightVertexShader = "attribute vec3 aPosition;\n" +
                "attribute vec2 aTexCoor;\n" +
                "varying vec2 vTextureCoord;\n" +
                "void main()     \n" +
                "{                            \n" +
                "   gl_Position = vec4(aPosition,1);\n" +
                "   vTextureCoord = aTexCoor;\n" +
                "}\n";
        private final String mRightFragmentShader = "precision mediump float;\n" +
                "varying vec2 vTextureCoord;\n" +
                "uniform sampler2D sTexture;\n" +
                "void main()                         \n" +
                "{           \n" +
                "   gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                "}       ";

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

        private float[] mMVPMatrix = new float[16];
        private float[] mSTMatrix = new float[16];

        private int mProgram, mRightProgram;
        private int mTextureID;
        private int muMVPMatrixHandle;
        private int muSTMatrixHandle;
        private int maPositionHandle, maRightPositionHandle;
        private int maTextureHandle, maRightTextureHandle;

        private SurfaceTexture mSurface;
        private boolean updateSurface = false;

        // Magic key
        private int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
        private boolean Inited = false;
        private boolean mIsPause = false;
        private Handler mHandler;
        private ArrayList<String> mPlayList = null;
        private int mCurrentIndex = -1;
        private boolean Is3d = true;//false;
        //private VsunInterface.VideoInterface mVideoInterface;

        /**
         * Fields that reads video source and dumps to file.
         */
        // The media player that loads and decodes the video.
        // Not owned by this class.
        private MediaPlayer mMediaPlayer = null;

        public void SetVrMode(boolean istrue) {
            Is3d = istrue;
        }

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

        public VideoRender() {

            mTriangleVertices = ByteBuffer.allocateDirect(
                    mTriangleVerticesData.length * FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTriangleVertices.put(mTriangleVerticesData).position(0);

            Matrix.setIdentityM(mSTMatrix, 0);
        }


        /**
         * Called to draw the current frame.
         * This method is responsible for drawing the current frame.
         */
        public void onDrawFrame(GL10 glUnused) {

            // Initial clear.
            GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            if (!Is3d) {
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                GLES20.glUseProgram(mRightProgram);

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture3.get(0));


                mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
                GLES20.glVertexAttribPointer(maRightTextureHandle, 2, GLES20.GL_FLOAT, false,
                        TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
                GLES20.glEnableVertexAttribArray(maRightTextureHandle);

                mTriangleVertices.position(TRIANGLE_VERTICES_DATA_RIGHT_OFFSET);
                GLES20.glVertexAttribPointer(maRightPositionHandle, 2, GLES20.GL_FLOAT, false,
                        TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
                GLES20.glEnableVertexAttribArray(maRightPositionHandle);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

                drawFrameBuffer();
            }

            // Load the program, which is the basics rules to draw the vertexes and textures.
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            GLES20.glUseProgram(mProgram);
            checkGlError("glUseProgram");

            // Activate the texture.
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);

            // Load the vertexes coordinates. Simple here since it only draw a rectangle
            // that fits the whole screen.

            checkGlError("glVertexAttribPointer maPosition");
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            checkGlError("glEnableVertexAttribArray maPositionHandle");

            // Load the texture coordinates, which is essentially a rectangle that fits
            // the whole video frame.
            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
            GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
            checkGlError("glVertexAttribPointer maTextureHandle");
            GLES20.glEnableVertexAttribArray(maTextureHandle);
            checkGlError("glEnableVertexAttribArray maTextureHandle");

            // Set up the GL matrices.
            Matrix.setIdentityM(mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

            // Draw a rectangle and render the video frame as a texture on it.
            synchronized (this) {
                if (updateSurface) {
                    mSurface.updateTexImage();
                    mSurface.getTransformMatrix(mSTMatrix);
                    updateSurface = false;
                }
            }

            if (Is3d)
                mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
            else
                mTriangleVertices.position(TRIANGLE_VERTICES_DATA_LEFT_OFFSET);

            GLES20.glVertexAttribPointer(maPositionHandle, 2, GLES20.GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            checkGlError("glDrawArrays");
            GLES20.glFinish();
        }


        /**
         * Called when the surface changed size.
         * Called after the surface is created and whenever the OpenGL surface size changes.
         */
        public void onSurfaceChanged(GL10 glUnused, int width, int height) {
            // Log.d(TAG, "Surface size: " + width + "x" + height);

            //int video_width = mMediaPlayer.getVideoWidth();
            //int video_height = mMediaPlayer.getVideoHeight();
            //Log.d(TAG, "Video size: " + video_width
            //     + "x" + video_height);

            // TODO: adjust video_width and video_height with the surface size.
            //GLES20.glViewport(0, 0, video_width, video_height);

            //int bpp[] = new int[3];
            //GLES20.glGetIntegerv(GLES20.GL_RED_BITS, bpp, 0);
            //GLES20.glGetIntegerv(GLES20.GL_GREEN_BITS, bpp, 1);
            //GLES20.glGetIntegerv(GLES20.GL_BLUE_BITS, bpp, 2);

            GLES20.glViewport(0, 0, width, height);
            mWidth = width;
            mHeight = height;
            if (!isFrameBufferInited) {
                initFrameBuffer();
            }

            // Create a new perspective projection matrix. The height will stay the same
            // while the width will vary as per aspect ratio.
        }

        private void pause() {
            if (mMediaPlayer != null) {
                if (mIsPause) {
                    mMediaPlayer.start();
                    mHandler.sendEmptyMessage(PlayerActivity.HIDEMSG);
                    mIsPause = false;
                } else {
                    mMediaPlayer.pause();
                    mHandler.sendEmptyMessage(PlayerActivity.PLAYPAUSE);
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
                mHandler.sendEmptyMessage(PlayerActivity.HIDEMSG);
                mIsPause = false;
            }
        };
        MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mHandler.sendEmptyMessage(PlayerActivity.PLAYEND);
                playernext();
            }
        };

        MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mHandler.sendEmptyMessage(PlayerActivity.PLAYERROR);
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

                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("video_path", mPlayList.get(mCurrentIndex));
                msg.setData(data);
                msg.what = PlayerActivity.VIDEO_PTAH;
                mHandler.sendMessage(msg);

            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                mHandler.sendEmptyMessage(PlayerActivity.PLAYERROR);
                e.printStackTrace();
            } catch (SecurityException e) {
                mHandler.sendEmptyMessage(PlayerActivity.PLAYERROR);
                e.printStackTrace();
            } catch (IllegalStateException e) {
                mHandler.sendEmptyMessage(PlayerActivity.PLAYERROR);
                e.printStackTrace();
            } catch (IOException e) {
                mHandler.sendEmptyMessage(PlayerActivity.PLAYERROR);
                e.printStackTrace();
            }

        }

        public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

            mRightProgram = createProgram(mRightVertexShader, mRightFragmentShader);
            maRightPositionHandle = GLES20.glGetAttribLocation(mRightProgram, "aPosition");
            maRightTextureHandle = GLES20.glGetAttribLocation(mRightProgram, "aTexCoor");

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
            GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            // Clamp to edge is the only option
            GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            checkGlError("glTexParameteri mTextureID");

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

            VideoView.this.requestRender();
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
