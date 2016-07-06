package org.yanzi.glsurfaceview;

import android.opengl.GLES20;

import org.yanzi.model.MyModel2;
import org.yanzi.util.ShaderUtil;

/**
 * Created by 111 on 2016/6/7.
 */
public class DirectDrawer2 {
    final String vertexShader =
            "uniform mat4 u_MVPMatrix;                \n"        // A constant representing the combined model/view/projection matrix.
                    + "attribute vec4 a_Position;     \n"        // Per-vertex position information we will pass in.
                    + "attribute vec4 a_Color;        \n"        // Per-vertex color information we will pass in.
                    + "varying vec4 v_Color;          \n"        // This will be passed into the fragment shader.
                    + "void main()                    \n"        // The entry point for our vertex shader.
                    + "{                              \n"
                    + "   v_Color = a_Color;          \n"        // Pass the color through to the fragment shader.
                    // It will be interpolated across the triangle.
                    + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
                    + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                    + "}                              \n";    // normalized screen coordinates.

    final String fragmentShader =
            "precision mediump float;                 \n"        // Set the default precision to medium. We don't need as high of a
                    + "varying vec4 v_Color;          \n"        // This is the color from the vertex shader interpolated across the
                    + "void main()                    \n"        // The entry point for our fragment shader.
                    + "{                              \n"
                    + "   gl_FragColor = v_Color;     \n"        // Pass the color directly through the pipeline.
                    + "}                              \n";

    private MyModel2 myModel2;

    private float[] mMVPMatrix = new float[16];          //总变换矩阵

    private int mMVPMatrixHandle;

    private int mPositionHandle;                         //顶点位置属性引用
    private int mColorHandle;                            //顶点颜色属性引用
    private int mProgramHandle;

    public DirectDrawer2() {

        myModel2 = new MyModel2();
        //基于顶点着色器与片元着色器创建程序
        mProgramHandle =  ShaderUtil.createProgram(vertexShader,fragmentShader);
        //从着色器源程序中的总变换矩阵一致变量MVPMatrix的引用（获取程序中矩阵属性引用id）
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        //从着色器源程序中的顶点着色器中获取Position属性（获取程序中顶点位置属性引用id）
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        //从着色器源程序中的顶点着色器中获取Color属性（获取程序中顶点颜色属性引用id）
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");

        //为画笔指定顶点位置数据
        GLES20.glBindAttribLocation(mProgramHandle, 0, "a_Position");
        //为画笔指定顶点颜色数据
        GLES20.glBindAttribLocation(mProgramHandle, 1, "a_Color");
    }

    public void draw() {
        GLES20.glUseProgram(mProgramHandle);

        //开启顶点属性数组   批量传入管线数据
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        //将顶点位置信息传送进渲染管线  （false为是否规格化）
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3*4, myModel2.getmCubePositions());
        //将顶点颜色信息传送进渲染管线
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 4*4, myModel2.getmCubeColors());
        //通过一致变量引用将一直变量传送进渲染管线
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }
}
