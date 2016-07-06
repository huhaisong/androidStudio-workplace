package com.example.learn_sun2;

import android.opengl.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private float[] mMMatrix = new float[16];
    private float[] mCamMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private float[] mtemp = new float[16];
    float ratio = 0.65693432f;
    float[] vec = new float[]{1f,2f,3f,4f};
    float[] matrix = new float[]{
            1f,0f,0f,0f,
            0f,1f,0f,0f,
            0f,0f,1f,0f,
            1f,0f,0f,1f
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        Matrix.setRotateM(mMMatrix,0,0,0,1,0);
        Log.i("aaa", "mMMatrix");
        show(mMMatrix);
        Matrix.setLookAtM(mCamMatrix,0,0,0,6,0,0,0,0,1.0f,0.0f);
        Log.i("aaa", "mCamMatrix");
        show(mCamMatrix);
        Matrix.frustumM(mProjMatrix,0,-ratio*0.4f, ratio*0.4f, -1*0.4f, 1*0.4f, 1, 50);
        Log.i("aaa", "mProjMatrix");
        show(mProjMatrix);

        Matrix.multiplyMM(mtemp,0,mCamMatrix,0,mMMatrix,0);
        Log.i("aaa", "mtemp");
        show(mtemp);
        Matrix.multiplyMM(mMVPMatrix,0,mProjMatrix,0,mtemp,0);
        Log.i("aaa", "mMVPMatrix2");
        show(mMVPMatrix);*/

        Matrix.multiplyMV(vec,0,matrix,0,vec,0);

        show(vec);
    }

    public void show(float[] f ){
      //  for (int i = 0; i <4 ; i++) {+f[i*4+3]
        int i = 0;
            Log.i("aaa", f[i*4]+","+f[i*4+1]+","+f[i*4+2]+",");
        //}
    }
}
