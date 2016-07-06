package com.example.fbotest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

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

    public static IntBuffer makeIntBuffer(int[] colors) {
        IntBuffer mIntBuffer;
        ByteBuffer qbb = ByteBuffer.allocateDirect(colors.length * 4);
        qbb.order(ByteOrder.nativeOrder());
        mIntBuffer = qbb.asIntBuffer();
        mIntBuffer.put(colors);
        mIntBuffer.position(0);
        return mIntBuffer;
    }


    //将short[]数组转换成shortBuffer；
    public static ShortBuffer makeShortBuffer(short[] shorts) {
        ShortBuffer mShortBuffer;
        ByteBuffer dlb = ByteBuffer.allocateDirect(shorts.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        mShortBuffer = dlb.asShortBuffer();
        mShortBuffer.put(shorts);
        mShortBuffer.position(0);
        //返回ShortBuffer
        return mShortBuffer;
    }
}
