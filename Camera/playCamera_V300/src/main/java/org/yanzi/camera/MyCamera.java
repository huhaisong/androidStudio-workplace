package org.yanzi.camera;

import android.opengl.Matrix;

import org.yanzi.util.MemUtil;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by 111 on 2016/6/6.
 */
public class MyCamera {


    private FloatBuffer vertexBuffer, textureVerticesBuffer;
    private ShortBuffer drawListBuffer;

    /**绘制次序**/
    private short drawOrder[] = {0, 1, 2, 0, 2, 3}; // order to draw vertices

    /** 绘制的区域尺寸 **/
    static float squareCoords[] = {
            -1.8f, 1.0f*2.0f, 0.0f,
            -1.8f, -1.0f*2.0f, 0.0f,
            1.8f, -1.0f*2.0f, 0.0f,
            -1.8f, 1.0f*2.0f, 0.0f,
            1.8f, -1.0f*2.0f, 0.0f,
            1.8f, 1.0f*2.0f, 0.0f
    };

    /**纹理坐标**/
    static float textureVertices[] = {
            0.0f, 1.0f, 0.0f,
            1.0f, 1.0f,0.0f,
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            0.0f, 0.0f,0.0f,
    };

    public MyCamera() {
        /**初始化缓冲字节流**/
        vertexBuffer = MemUtil.makeFloatBuffer(squareCoords);  //区域尺寸
        drawListBuffer = MemUtil.makeShortBuffer(drawOrder);   //绘制次序
        textureVerticesBuffer = MemUtil.makeFloatBuffer(textureVertices);  //纹理坐标
    }

    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public FloatBuffer getTextureVerticesBuffer() {
        return textureVerticesBuffer;
    }

    public ShortBuffer getDrawListBuffer() {
        return drawListBuffer;
    }

    public short[] getDrawOrder() {
        return drawOrder;
    }
}
