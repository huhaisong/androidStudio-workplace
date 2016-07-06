package com.example.a111.opengl_fbo2;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.example.a111.opengl_fbo2.model.Model;
import com.example.a111.opengl_fbo2.model.Rect;
import com.example.a111.opengl_fbo2.util.MatrixState;


/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class Test7Renderer implements GLSurfaceView.Renderer {

    private static final String TAG = "Test7Renderer";

    private final Context mActivityContext;

    int mTextureDataHandle;

    private Model myModel;
    private Rect myRect;

    IntBuffer texture = IntBuffer.allocate(1);

    public Test7Renderer(final Context activityContext) {
        mActivityContext = activityContext;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        myModel = new Model(mActivityContext);
        myRect = new Rect(mActivityContext);

       /* mTextureDataHandle = loadTexture(mActivityContext, R.drawable.aa);

        IntBuffer framebuffer = IntBuffer.allocate(1);
        IntBuffer depthRenderbuffer = IntBuffer.allocate(1);
        int texWidth = 2000, texHeight = 2000;
        IntBuffer maxRenderbufferSize = IntBuffer.allocate(1);
        GLES20.glGetIntegerv(GLES20.GL_MAX_RENDERBUFFER_SIZE, maxRenderbufferSize);
        //Log.i(TAG, "onDrawFrame: "+maxRenderbufferSize.get(0) == 4096);

        // generate the framebuffer, renderbuffer, and texture object names
        GLES20.glGenFramebuffers(1, framebuffer);
        GLES20.glGenRenderbuffers(1, depthRenderbuffer);
        GLES20.glGenTextures(1, texture);
        // bind texture and load the texture mip-level 0 texels are RGB565
        // no texels need to be specified as we are going to draw into the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.get(0));
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, texWidth, texHeight, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        // bind renderbuffer and create a 16-bit depth bufferwidth and height of renderbuffer = width and height of the texture
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderbuffer.get(0));
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, texWidth, texHeight);
        // bind the framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.get(0));
        // specify texture as color attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture.get(0), 0);
        // specify depth_renderbufer as depth attachment
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRenderbuffer.get(0));
   */ }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        final float ratio = (float) width / height;
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        // check for framebuffer complete
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status == GLES20.GL_FRAMEBUFFER_COMPLETE) {

            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            //myModel.drawSelf(mTextureDataHandle);
            // render to window system provided framebuffer
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            GLES20.glClearColor(1.0f, 1.0f,1.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            myRect.drawSelf(0);
            //myModel.drawSelf(0);
           // myModel.drawSelf(texture.get(0));
           // GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
    }

    public  int loadTexture(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("failed to load texture");
        }

        return textureHandle[0];
    }

}