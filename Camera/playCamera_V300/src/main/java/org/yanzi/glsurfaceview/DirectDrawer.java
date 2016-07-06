package org.yanzi.glsurfaceview;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import org.yanzi.camera.MyCamera;
import org.yanzi.model.MyModel;
import org.yanzi.util.MatrixState;
import org.yanzi.util.ShaderUtil;

import java.nio.FloatBuffer;

public class DirectDrawer {
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec3 vPosition;" +
                    "attribute vec2 inputTextureCoordinate;" +
                    "varying vec2 textureCoordinate;" +
                    "void main()" +
                    "{" +
                    "gl_Position = uMVPMatrix*vec4(vPosition,1);" +
                    "textureCoordinate = inputTextureCoordinate;" +
                    "}";

    private final String fragmentShaderCode =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;" +
                    "varying vec2 textureCoordinate;\n" +
                    "uniform samplerExternalOES s_texture;\n" +
                    "void main() {" +
                    "  gl_FragColor = texture2D( s_texture, textureCoordinate );\n" +
                    "}";

    private final int mProgram;
    private int muMVPMatrixHandle;  //矩阵引用
    private int mPositionHandle;    //位置引用
    private int mTextureCoordHandle;//纹理引用
    private MyCamera myCamera;

    private int texture;

    public DirectDrawer(int texture) {
        this.texture = texture;
        /**将着色器添加到一个空的OpenGLES program对象然后链接这个program**/
        mProgram = ShaderUtil.createProgram(vertexShaderCode, fragmentShaderCode);

        //获取指向vertex shader的成员vPosition的 handle
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void draw() {

        myCamera = new MyCamera();
        GLES20.glUseProgram(mProgram); //将program加入OpenGL ES环境中 绘制时使用mProgram程序
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);  //绑定纹理
        /**画相机**/
        //启用指向三角形的顶点数组
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
        //指定positionHandle的数据值可以在什么地方访问。 vertexBuffer在内部（NDK）是个指针，指向数组的第一组值的内存
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3*4, myCamera.getVertexBuffer());   //设置矩形坐标
        GLES20.glVertexAttribPointer(mTextureCoordHandle, 3, GLES20.GL_FLOAT, false, 3*4, myCamera.getTextureVerticesBuffer());  //设置纹理坐标
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,6);
    }
}























