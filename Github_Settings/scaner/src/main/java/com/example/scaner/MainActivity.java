package com.example.scaner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.xys.libzxing.zxing.activity.CaptureActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {

        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, 1);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String json = data.getStringExtra("result");
            SettingsInfo settingsInfo = JSON.parseObject(json, SettingsInfo.class);
            FunctionThread functionThread = new FunctionThread(getBaseContext(), settingsInfo);
            functionThread.start();
        }
    }
}
