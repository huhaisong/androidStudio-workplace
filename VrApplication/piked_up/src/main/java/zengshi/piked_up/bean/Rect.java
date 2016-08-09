package zengshi.piked_up.bean;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;

import zengshi.piked_up.MySurfaceView;
import zengshi.piked_up.util.MatrixState;
import zengshi.piked_up.util.MemUtil;
import zengshi.piked_up.util.ShaderUtil;

/**
 * Created by 111 on 2016/8/5.
 */
public class Rect extends TouchableObject {

    int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maTexCoorHandle;
    int textureId;

    String mVertexShader;//顶点着色器代码脚本
    String mFragmentShader;//片元着色器代码脚本

    FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer   mTextureBuffer;//顶点法向量数据缓冲
    int vCount=0;

    float z = -100f;




    public Rect(MySurfaceView mv, Bitmap bitmap, float aX, float aY, float bX, float bY ) {

        textureId  = initTexture1(bitmap);
        initDate(aX,aY,bX,bY);
        initShader(mv);
    }

    private void initDate(float aX,float aY,float bX,float bY) {

        float vertices[] = new float[]{

                aX,aY,z,
                aX,bY,z,
                bX,bY,z,

                bX,bY,z,
                bX,aY,z,
                aX,aY,z,
        };
        float texCoords[] = new float[]{

                0,0,
                0,1,
                1,1,

                1,1,
                1,0,
                0,0,
        };

        vCount=vertices.length/3;
        mVertexBuffer = MemUtil.makeFloatBuffer(vertices);
        mTextureBuffer = MemUtil.makeFloatBuffer(texCoords);
        preBox = new AABB3(vertices);
    }

    private void initShader(MySurfaceView mv) {

        //加载顶点着色器的脚本内容
        mVertexShader= ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中纹理坐标属性引用
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

    }
    public void draw(){



        //制定使用某套着色器程序
        GLES20.glUseProgram(mProgram);
        //将最终变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //将顶点位置数据传入渲染管线
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3*4, mVertexBuffer);
        GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        //启用顶点位置、纹理坐标数据
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        //绘制被加载的物体
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }

    public int initTexture1(Bitmap bitmap)//textureId
    {
        //生成纹理ID
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int textureId = textures[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);  //绑定纹理

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        //实际加载纹理
        GLUtils.texImage2D
                (
                        GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
                        0,                      //纹理的层次，0表示基本图像层，可以理解为直接贴图
                        bitmap,              //纹理图像
                        0                       //纹理边框尺寸
                );
        if (bitmap != null) {
            bitmap.recycle();          //纹理加载成功后释放图片
        }
        return textureId;
    }
}
