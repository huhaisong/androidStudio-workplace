package com.example.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.example.scan.WifiUtil;
import com.xys.libzxing.zxing.config.Config;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by 111 on 2016/5/6.
 */
public class FunctionThread extends Thread {

    private String mResults[];
    private Context context;
    WifiUtil wifiUtil;

    public FunctionThread(String[] mResults, Context context) {
        this.mResults = mResults;
        this.context = context;
        wifiUtil = new WifiUtil(context);
    }

    @Override
    public void run() {
        super.run();

        if (mResults != null) {

            openWifi();
        }
    }

    //打开并连接指定wifi
    private void openWifi() {
        wifiUtil.openWifi();
        String pass = mResults[1];
        String ssid = mResults[0];
        wifiUtil.addNetwork(wifiUtil.CreateWifiInfo(ssid, pass, 3));
    }
}















