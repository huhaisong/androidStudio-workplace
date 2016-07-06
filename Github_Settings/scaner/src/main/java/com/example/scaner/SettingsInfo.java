package com.example.scaner;

import java.io.Serializable;

/**
 * Created by 111 on 2016/5/24.
 */
public class SettingsInfo implements Serializable {
    private String wifiState, bluetoothState, wifiName, wifiPass = "", bluetoothName;

    public SettingsInfo() {
    }

    public String getWifiState() {
        return wifiState;
    }

    public void setWifiState(String wifiState) {
        this.wifiState = wifiState;
    }

    public String getBluetoothState() {
        return bluetoothState;
    }

    public void setBluetoothState(String bluetoothState) {
        this.bluetoothState = bluetoothState;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getWifiPass() {
        return wifiPass;
    }

    public void setWifiPass(String wifiPass) {
        this.wifiPass = wifiPass;
    }

    public String getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }
}
