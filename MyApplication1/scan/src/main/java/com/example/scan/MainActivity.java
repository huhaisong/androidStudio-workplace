package com.example.scan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.example.scan.R;
import com.example.scan.FunctionThread;
import com.xys.libzxing.zxing.activity.CaptureActivity;

public class MainActivity extends Activity {

    private String mResult;
    private String mResults[];
    private TextView mContentTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContentTV = (TextView) findViewById(R.id.tv_content);
    }

    //扫描二维码按钮的点击事件
    public void scanQRCode(View view) {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获得二维码扫描的信息
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            mResult = bundle.getString("result");
            if (mResult != null) {
                mContentTV.setText("扫描结果:\n" + mResult);
                mResults = mResult.split(":");
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //开启子线程实现相应的功能
        new FunctionThread(mResults, this).start();
    }
}
