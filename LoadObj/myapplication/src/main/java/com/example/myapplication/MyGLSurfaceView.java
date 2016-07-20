package com.example.myapplication;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 111 on 2016/7/18.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    public MyGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        MyRenderer myRenderer = new MyRenderer();
        setRenderer(myRenderer);
    }


    private class MyRenderer implements Renderer {

        private int mWidth, mHeight;
        private FloatBuffer vertexBuffer;
        private int mProgram;
        private String mVertexShader = "attribute vec3 aPosition;\n" +
                "void main()     \n" +
                "{\n" +
                "gl_PointSize=10.0;\n" +
                "gl_Position = vec4(aPosition,1.0);\n" +
                "}";
        private String mFragmentShader = "precision mediump float;\n" +
                "void main()                         \n" +
                "{           \n" +
                "   gl_FragColor = vec4(1.0,0.0,0.0,1.0);\n" +
                "}";

        private int maPositionHandle;

        private void initData(int lines) {

            float[] vertices = new float[lines * 3];

            float spacing = 2.0f / (float) lines;

            for (int i = 0; i < lines; i++) {

                vertices[i*3 ] = -1.0f+spacing*i;
                vertices[i*3 + 1] = (float) ( 0.1f*(0.8f*(1.0f - Math.sqrt(0.8f*0.8f+vertices[i*3]*vertices[i*3]))));
                vertices[i*3 + 2] = 0.0f;
            }
            // 初始化字节缓冲  (坐标数 * 4)float占四字节
            ByteBuffer qbb = ByteBuffer.allocateDirect(vertices.length * 4);
            // 设用字节顺序为本地操作系统顺序
            qbb.order(ByteOrder.nativeOrder());
            // 从ByteBuffer创建一个浮点缓冲
            vertexBuffer = qbb.asFloatBuffer();
            // 在缓冲区中写入数据 并设置buffer，从第一个坐标开始读
            vertexBuffer.put(vertices).position(0);
        }

        private void initShader() {

            mProgram = createProgram();

            maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        }

        private int createProgram() {

            //加载顶点着色器
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mVertexShader);
            if (vertexShader == 0) {
                return 0;
            }

            //加载片元着色器
            int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShader);
            if (pixelShader == 0) {
                return 0;
            }

            //创建程序
            int program = GLES20.glCreateProgram();
            //若程序创建成功则向程序中加入顶点着色器与片元着色器
            if (program != 0) {
                //向程序中加入顶点着色器
                GLES20.glAttachShader(program, vertexShader);
                checkGlError("glAttachShader");
                //向程序中加入片元着色器
                GLES20.glAttachShader(program, pixelShader);
                checkGlError("glAttachShader");
                //链接程序
                GLES20.glLinkProgram(program);
                //存放链接成功program数量的数组
                int[] linkStatus = new int[1];
                //获取program的链接情况
                GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
                //若链接失败则报错并删除程序
                if (linkStatus[0] != GLES20.GL_TRUE) {
                    Log.e("ES20_ERROR", "Could not link program: ");
                    Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program));
                    GLES20.glDeleteProgram(program);
                    program = 0;
                }
            }
            return program;
        }

        private int loadShader(int glVertexShader, String mVertexShader) {

            //创建一个新shader
            int shader = GLES20.glCreateShader(glVertexShader);
            //若创建成功则加载shader
            if (shader != 0) {
                //加载shader的源代码
                GLES20.glShaderSource(shader, mVertexShader);
                //编译shader
                GLES20.glCompileShader(shader);
                //存放编译成功shader数量的数组
                int[] compiled = new int[1];
                //获取Shader的编译情况
                GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
                if (compiled[0] == 0) {//若编译失败则显示错误日志并删除此shader
                    Log.e("ES20_ERROR", "Could not compile shader " + glVertexShader + ":");
                    Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
                    GLES20.glDeleteShader(shader);
                    shader = 0;
                }
            }
            return shader;
        }

        public void checkGlError(String op) {
            int error;
            if ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                Log.e("ES20_ERROR", op + ": glError " + error);
                throw new RuntimeException(op + ": glError " + error);
            }
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            initData(3000);
            initShader();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

            mHeight = height;
            mWidth = width;
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glViewport(0, 0, mWidth / 2, mHeight);
            GLES20.glUseProgram(mProgram);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 3000);

            GLES20.glViewport(mWidth / 2, 0, mWidth / 2, mHeight);
            GLES20.glUseProgram(mProgram);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 3000);
        }
    }
}
