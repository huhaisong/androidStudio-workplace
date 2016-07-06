package com.example.scaner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by 111 on 2016/5/25.
 */
public class FunctionThread extends Thread {


    private Context context;
    private SettingsInfo mSettingsInfo;
    private WifiUtil mWifiUtil;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetooth;

    public FunctionThread(Context context, SettingsInfo settingsInfo) {
        this.context = context;
        this.mSettingsInfo = settingsInfo;
        mWifiUtil = new WifiUtil(context);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    @Override
    public void run() {
        super.run();

        //设置wifi状态
        if (mSettingsInfo.getWifiState().equals("on")) {
            openWifi();
        } else if (mSettingsInfo.getWifiState().equals("off")) {
            mWifiUtil.closeWifi();
        }

        //设置蓝牙状态
        if (mSettingsInfo.getBluetoothState().equals("on")) {
            openBluetooth(mSettingsInfo.getBluetoothName());
        } else if (mSettingsInfo.getBluetoothState().equals("off")) {
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }
        }
    }

    //打开指定wifi
    private void openWifi() {

        mWifiUtil.openWifi();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        context.registerReceiver(wifiReceiver, filter);
    }


    //打开指定蓝牙设备
    private void openBluetooth(String name) {

        mBluetooth = name;
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        if (!mBluetooth.equals("")) {
            // 设置蓝牙广播信息过滤
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            // 注册广播接收器，接收并处理搜索结果
            context.registerReceiver(bluetoothReceiver, intentFilter);
            // 寻找蓝牙设备，android会将查找到的设备以广播形式发出去
            mBluetoothAdapter.startDiscovery();
        }
    }


    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("test", "-----------action" + intent.getAction());
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                int type = mSettingsInfo.getWifiPass().equals("") ? 1 : 3;
                mWifiUtil.addNetwork(mWifiUtil.CreateWifiInfo(mSettingsInfo.getWifiName(), mSettingsInfo.getWifiPass(), type));
                context.unregisterReceiver(wifiReceiver);
            }
        }
    };

    //蓝牙广播
    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("test", "-----------action" + action);
            int connectState;
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 获取查找到的蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("test", "-------------" + device.getName());
                // 如果查找到的设备符合要连接的设备，处理
                if (device.getName().equalsIgnoreCase(mBluetooth)) {
                    // 搜索蓝牙设备的过程占用资源比较多，一旦找到需要连接的设备后需要及时关闭搜索
                    mBluetoothAdapter.cancelDiscovery();
                    // 获取蓝牙设备的连接状态
                    connectState = device.getBondState();
                    switch (connectState) {
                        case BluetoothDevice.BOND_NONE:
                            // 配对
                            try {
                                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                                createBondMethod.invoke(device);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            try {
                                // 连接指定设备
                                connectBluetooth(device);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                // 状态改变的广播
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName().equalsIgnoreCase(mBluetooth)) {
                    connectState = device.getBondState();
                    if (connectState == BluetoothDevice.BOND_BONDED) {
                        try {
                            // 连接
                            connectBluetooth(device);
                        } catch (IOException e) {
                            context.unregisterReceiver(bluetoothReceiver);
                            e.printStackTrace();
                        }
                    }
                    context.unregisterReceiver(bluetoothReceiver);
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (mBluetoothAdapter.isEnabled()) {
                    Log.i("test", "-----------isEnabled");
                    if (mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    mBluetoothAdapter.startDiscovery();
                }
            }
        }
    };

    //链接蓝牙
    private void connectBluetooth(BluetoothDevice device) throws IOException {
        // 固定的UUID
        final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
        UUID uuid = UUID.fromString(SPP_UUID);
        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
        socket.connect();
    }
}
