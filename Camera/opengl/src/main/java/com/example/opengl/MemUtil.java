package com.example.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by 111 on 2016/5/31.
 */
public class MemUtil {


    //将float[]数组转换成FloatBuffer；
    public static FloatBuffer makeFloatBuffer(float[] verteices) {

        FloatBuffer mFloatBuffer;
        ByteBuffer qbb = ByteBuffer.allocateDirect(verteices.length * 4);
        qbb.order(ByteOrder.nativeOrder());
        mFloatBuffer = qbb.asFloatBuffer();
        mFloatBuffer.put(verteices);
        mFloatBuffer.position(0);
        return mFloatBuffer;
    }

    //将float[]数组转换成FloatBuffer；
    public static IntBuffer makeIntBuffer(int[] colors) {
        IntBuffer mIntBuffer;
        ByteBuffer qbb = ByteBuffer.allocateDirect(colors.length * 4);
        qbb.order(ByteOrder.nativeOrder());
        mIntBuffer = qbb.asIntBuffer();
        mIntBuffer.put(colors);
        mIntBuffer.position(0);
        return mIntBuffer;
    }
}
