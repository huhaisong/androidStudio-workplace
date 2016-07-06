package com.zhf.wifidemo.broadcast;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class WIFIBroadcast extends BroadcastReceiver {

    public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().endsWith(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            for (int i = 0; i < ehList.size(); i++) {
                ((EventHandler) ehList.get(i)).scanResultsAvaiable();
            }

        } else if (intent.getAction().endsWith(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            for (int j = 0; j < ehList.size(); j++) {
                ((EventHandler) ehList.get(j)).wifiStatusNotification();
            }

        } else if (intent.getAction().endsWith(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            for (int m = 0; m < ehList.size(); m++) {
                ((EventHandler) ehList.get(m)).handleConnectChange();
            }
        }
    }

    public static abstract interface EventHandler {
        public abstract void handleConnectChange();

        public abstract void scanResultsAvaiable();

        public abstract void wifiStatusNotification();
    }

}
