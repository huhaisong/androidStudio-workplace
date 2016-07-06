package com.example.a111.opengl_fbo2;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    GLSurfaceView glSurfaceView ;
    MySurfaceView mySurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        glSurfaceView = new GLSurfaceView(this);
        Test7Renderer renderer = new Test7Renderer(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(renderer);
        setContentView(glSurfaceView);*/


        mySurfaceView = new MySurfaceView(this);

        setContentView(mySurfaceView);
    }
}
