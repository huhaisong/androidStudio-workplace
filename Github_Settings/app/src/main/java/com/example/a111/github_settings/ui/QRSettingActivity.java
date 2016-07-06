package com.example.a111.github_settings.ui;

import android.bluetooth.BluetoothAdapter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.a111.github_settings.R;
import com.example.a111.github_settings.bean.SettingsInfo;
import com.example.a111.github_settings.bean.WifiInfo;
import com.example.a111.github_settings.config.Config;
import com.example.a111.github_settings.util.FileStore;
import com.example.a111.github_settings.util.WifiUtil;

import java.util.List;

public class QRSettingActivity extends BaseActivity {

    private static final String WIFI_ON = "on";
    private static final String WIFI_OFF = "off";
    private static final String BLUETOOTH_ON = "on";
    private static final String BLUETOOTH_OFF = "off";

    private TextView mWifiState, mBluetoothState, mWifiName, mBluetoothName, mReminder;
    private LinearLayout mWifiLayout, mBluetoothLayout;
    private Button mButton;
    private WifiUtil mWifiUtil;
    private BluetoothAdapter mBluetoothAdapter;
    private SettingsInfo mSettingsInfo = new SettingsInfo();
    private FileStore mWifiFileStore, mBluetoothFileStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrsetting);
        initView();
        initClick();
    }

    private void initView() {

        mWifiUtil = new WifiUtil(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mWifiFileStore = new FileStore(Config.WIFI_FILE_PATH, this);
        mBluetoothFileStore = new FileStore(Config.BLUETOOTH_FILE_PATH, this);

        mReminder = (TextView) findViewById(R.id.tv_reminder);
        mWifiState = (TextView) findViewById(R.id.tv_wifi_state);
        mWifiName = (TextView) findViewById(R.id.tv_wifi_name);
        mWifiLayout = (LinearLayout) findViewById(R.id.layout_wifi_connected_state);
        mBluetoothState = (TextView) findViewById(R.id.tv_bluetooth_state);
        mBluetoothName = (TextView) findViewById(R.id.tv_bluetooth_name);
        mBluetoothLayout = (LinearLayout) findViewById(R.id.layout_bluetooth_connected_state);
        mButton = (Button) findViewById(R.id.btn_QR);


        if (mWifiUtil.checkState() == WifiManager.WIFI_STATE_ENABLED) {
            mWifiState.setText(WIFI_ON);
            mWifiLayout.setVisibility(View.VISIBLE);
        } else {
            mWifiState.setText(WIFI_OFF);
            mWifiLayout.setVisibility(View.GONE);
        }

        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothState.setText(BLUETOOTH_ON);
            mBluetoothLayout.setVisibility(View.VISIBLE);
        } else {
            mBluetoothState.setText(BLUETOOTH_OFF);
            mBluetoothLayout.setVisibility(View.GONE);
        }
        getQRSettingsInfo();
    }

    private void initClick() {

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = JSON.toJSONString(mSettingsInfo);
                startActivity(ShowActivity.class, json);
                Log.i("mButton", "----------" + json);
            }
        });
    }

    //获取当前设置信息
    private SettingsInfo getQRSettingsInfo() {

        //获取wifi信息
        if (mWifiUtil.checkState() == WifiManager.WIFI_STATE_ENABLED) {
            String name = mWifiUtil.getSSID().replace("\"", "");
            List<WifiInfo> wifiInfos = mWifiFileStore.read();
            WifiInfo wifiInfo = null;
            for (WifiInfo w : wifiInfos) {
                if (w.getName().equals(name)) {
                    wifiInfo = w;
                }
            }
            if (wifiInfo == null) {
                mReminder.setText("提示：您当前连接的wifi没有相应数据，请重新设置");
                mButton.setEnabled(false);
            } else {
                mSettingsInfo.setWifiName(wifiInfo.getName());
                mSettingsInfo.setWifiPass(wifiInfo.getPass());
                mSettingsInfo.setWifiState(WIFI_ON);
                mWifiName.setText(name);
            }
        } else {
            mSettingsInfo.setWifiState(WIFI_OFF);
        }

        //获取蓝牙信息
        if (mBluetoothAdapter.isEnabled()) {
            mSettingsInfo.setBluetoothState(BLUETOOTH_ON);
            String name = mBluetoothFileStore.readBluetooth();
            mBluetoothName.setText(name);
            mSettingsInfo.setBluetoothName(name);
        } else {
            mSettingsInfo.setBluetoothState(BLUETOOTH_OFF);
        }

        return mSettingsInfo;
    }
}
