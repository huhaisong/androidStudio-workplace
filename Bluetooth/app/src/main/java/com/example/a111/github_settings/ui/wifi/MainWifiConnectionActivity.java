package com.example.a111.github_settings.ui.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a111.github_settings.R;
import com.example.a111.github_settings.bean.WifiInfo;
import com.example.a111.github_settings.config.Config;
import com.example.a111.github_settings.ui.BaseActivity;
import com.example.a111.github_settings.util.FileStore;
import com.example.a111.github_settings.util.WifiUtil;

public class MainWifiConnectionActivity extends BaseActivity {


    private TextView textView;
    private Button mCancel, mConnect;
    private EditText mPass;
    private String name;
    private WifiUtil mWifiUtil;
    private FileStore mFileStore;
    private WifiInfo mWifiInfo;
    private boolean mRegister = false;
    private WifiBroadcastReceiver mWifiBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wifi_connectioin);

        initView();
        initClick();
    }

    private void initClick() {

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = mPass.getText().toString();
                mWifiInfo = new WifiInfo(name, pass);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mWifiUtil.connectWifi(mWifiInfo);
                    }
                }).start();
                if (!mRegister) {

                    mWifiBroadcastReceiver = new WifiBroadcastReceiver();
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(WifiManager.RSSI_CHANGED_ACTION);  //信号强度变化
                    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);  //网络状态变化（是否连接上WIFI）
                    filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);  //wifi是否打开
                    registerReceiver(mWifiBroadcastReceiver, filter);
                    mRegister = true;
                }
            }
        });
    }

    private void initView() {
        Intent intent = getIntent();
        mFileStore = new FileStore(Config.WIFI_FILE_PATH, getBaseContext());
        mWifiUtil = new WifiUtil(getBaseContext());
        name = intent.getStringExtra("content");
        textView = (TextView) findViewById(R.id.tv_wifi_name);
        textView.setText(name);
        mCancel = (Button) findViewById(R.id.btn_wifi_cancel);
        mConnect = (Button) findViewById(R.id.btn_wifi_connect);
        mPass = (EditText) findViewById(R.id.ev_wifi_pass);
    }

    public class WifiBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    String newName = mWifiUtil.getSSID().replace("\"", "");
                    Log.i("test", "--------name:" + name);
                    Log.i("test", "--------newName:" + newName);
                    if (newName.equals(name)) {  //是否连接到指定wifi
                        mFileStore.writeWifiItem(mWifiInfo);
                        Toast.makeText(getBaseContext(), "连接成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWifiBroadcastReceiver != null) {
            unregisterReceiver(mWifiBroadcastReceiver);
        }
    }
}
