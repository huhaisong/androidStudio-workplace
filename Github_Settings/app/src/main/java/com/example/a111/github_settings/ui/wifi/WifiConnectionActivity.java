package com.example.a111.github_settings.ui.wifi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a111.github_settings.R;
import com.example.a111.github_settings.bean.WifiInfo;
import com.example.a111.github_settings.config.Config;
import com.example.a111.github_settings.ui.BaseActivity;
import com.example.a111.github_settings.util.FileStore;

public class WifiConnectionActivity extends BaseActivity {

    private EditText mSSID, mPass;
    private LinearLayout mTypeLayout;
    private TextView mTypeTextView;
    private Button mCancel, mPreserve;
    private FileStore fileStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connection);
        initView();
        initClick();
    }

    private void initView() {

        fileStore = new FileStore(Config.WIFI_FILE_PATH, getBaseContext());

        mSSID = (EditText) findViewById(R.id.ev_wifi_connection_ssid);
        mPass = (EditText) findViewById(R.id.ev_wifi_connection_pass);
        mCancel = (Button) findViewById(R.id.btn_wifi_connection_cancel);
        mPreserve = (Button) findViewById(R.id.btn_wifi_connection_preserve);
        mTypeLayout = (LinearLayout) findViewById(R.id.layout_wifi_connection_type);
        mTypeTextView = (TextView) findViewById(R.id.tv_wifi_connection_type_content);
    }

    private void initClick() {

        mTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mPreserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WifiInfo wifiInfo = new WifiInfo();
                String name = mSSID.getText().toString();
                if (!name.equals("")) {
                    wifiInfo.setName(name);
                    wifiInfo.setType(mTypeTextView.getText().toString());
                    if (!mTypeTextView.getText().equals("开放")) {
                        String pass = mPass.getText().toString();
                        if (!pass.equals("")) {
                            wifiInfo.setPass(pass);
                        } else {
                            Toast.makeText(getBaseContext(), "密码输入不能为空！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Log.i("test", "-------" + wifiInfo.toString());
                    fileStore.writeWifiItem(wifiInfo);
                } else {
                    Toast.makeText(getBaseContext(), "输入不能为空！", Toast.LENGTH_SHORT).show();
                }
                Log.i("test", "---------file:" + fileStore.read().toString());
            }
        });
    }

    private void dialog() {
        final String items[] = {"开放", "WPA/WPA2 PSK", "WEP"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //得到构造器
        builder.setTitle("安全性");
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mTypeTextView.setText(items[which]);
                if (which > 0) {
                    mPass.setVisibility(View.VISIBLE);
                } else {
                    mPass.setVisibility(View.GONE);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
