package zengshi.test;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by 111 on 2016/8/8.
 */
public class ImageActivity extends Activity {


    private ImageView mImageView ;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        mImageView  = new ImageView(this);
        setContentView(mImageView);

        Bundle bundle = getIntent().getExtras().getBundle("content");
        path = bundle.getString("path");
        mImageView.setPath(path);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageView.onPause();
    }
}
