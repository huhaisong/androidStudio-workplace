package com.example.a111.myapplication1;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 111 on 2016/5/6.
 */
public class WifiThread extends Thread {

    private Context context;

    public WifiThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(context, ShowActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * @return 已经保存的所有的wifi账号和密码
     */
    public List<WifiInfo> getWifis() {
        //获得所有的已经用过的wifi账号和密码
        List<WifiInfo> wifiInfos = new ArrayList<WifiInfo>();
        Process process = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        StringBuffer wifiConf = new StringBuffer();
        try {
            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataInputStream = new DataInputStream(process.getInputStream());
            dataOutputStream.writeBytes("cat /data/misc/wifi/*.conf\n");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                wifiConf.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                process.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //提取账号密码信息并保存进wifiinfos
        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Matcher networkMatcher = network.matcher(wifiConf.toString());
        while (networkMatcher.find()) {
            String networkBlock = networkMatcher.group();
            Pattern ssid = Pattern.compile("ssid=\"([^\"]+)\"");
            Matcher ssidMatcher = ssid.matcher(networkBlock);

            if (ssidMatcher.find()) {
                WifiInfo wifiInfo = new WifiInfo();
                wifiInfo.Ssid = ssidMatcher.group(1);
                Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");
                Matcher pskMatcher = psk.matcher(networkBlock);
                if (pskMatcher.find()) {
                    wifiInfo.Password = pskMatcher.group(1);
                } else {
                    wifiInfo.Password = "";
                }
                wifiInfos.add(wifiInfo);
            }
        }
        return wifiInfos;
    }

    /**
     * @param wifiInfos 已经保存的所有的wifi账号和密码
     * @return 当前正在连接的账号账号和密码
     */
    private WifiInfo getCurrentWifi(List<WifiInfo> wifiInfos) {

        String newName = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            android.net.wifi.WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String name = wifiInfo.getSSID();
            newName = name.replace("\"", "");
            //对比出两个账号的名称，并且获得名称相同的wifi
            for (WifiInfo item : wifiInfos) {
                if (item.Ssid.equals(newName)) {

                    Log.i("test", "---------" + item.toString());
                    //返回当前的wifi
                    return item;
                }
            }
        }
        return null;
    }

}
