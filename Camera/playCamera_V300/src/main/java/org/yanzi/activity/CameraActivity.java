package org.yanzi.activity;

import org.yanzi.glsurfaceview.MyGLSurfaceView;
import org.yanzi.playcamera_v3.R;
import org.yanzi.util.DisplayUtil;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;

public class CameraActivity extends Activity {
    private static final String TAG = "CameraActivity";
    MyGLSurfaceView glSurfaceView = null;
    float previewRate = -1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        glSurfaceView = (MyGLSurfaceView) findViewById(R.id.camera_textureview);
        initViewParams();
    }

    private void initViewParams() {
        LayoutParams params = glSurfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        previewRate = DisplayUtil.getScreenRate(this);
        glSurfaceView.setLayoutParams(params);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        glSurfaceView.bringToFront();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        glSurfaceView.onPause();
    }
}
