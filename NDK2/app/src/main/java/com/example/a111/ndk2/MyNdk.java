package com.example.a111.ndk2;

/**
 * Created by 111 on 2016/7/26.
 */
public class MyNdk {


    static {
        System.loadLibrary("JniTest");
    }
    public static native String getFromNDK();
}
