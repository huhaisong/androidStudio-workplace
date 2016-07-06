package org.yanzi.model;

import org.yanzi.util.MemUtil;

import java.nio.FloatBuffer;

/**
 * Created by 111 on 2016/6/6.
 */
public class MyModel {


    private FloatBuffer vertexBuffer;

    static float triangleCoords[] = { // 按逆时针方向顺序:
            0.0f, 0.622008459f, 0.0f,
            -0.5f, -0.311004243f, 0.0f,
            0.5f, -0.311004243f, 0.0f
    };

    // 设置颜色，分别为red, green, blue 和alpha (opacity)
    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    public MyModel() {

        vertexBuffer = MemUtil.makeFloatBuffer(triangleCoords);
    }


    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public int getVertexCount() {
        return triangleCoords.length;
    }

    public float[] getColor() {
        return color;
    }

}
