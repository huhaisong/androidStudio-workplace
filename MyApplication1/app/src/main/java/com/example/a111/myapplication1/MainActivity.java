package com.example.a111.myapplication1;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

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

public class MainActivity extends Activity implements View.OnClickListener {


    private static final String TAG = "MainActivity";


    private Button mWifiList, mOpenQR;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {

        mOpenQR = (Button) findViewById(R.id.btn_wifi_QR);
        mWifiList = (Button) findViewById(R.id.btn_wifi_list);
        mListView = (ListView) findViewById(R.id.lv_wifi);
        mOpenQR.setOnClickListener(this);
        mWifiList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String content = null;
        switch (v.getId()) {
            case R.id.btn_wifi_list:
                List<WifiInfo> wifiInfos = getWifis();
                Log.i(TAG, "----wifiInfos" + wifiInfos);
                WifiAdapter wifiAdapter = new WifiAdapter(this, wifiInfos);
                mListView.setAdapter(wifiAdapter);
                break;
            case R.id.btn_wifi_QR:
                WifiInfo wifiInfo = getCurrentWifi(getWifis());
                content = wifiInfo.getSsid() + ":" + wifiInfo.getPassword();
                startActivity(ShowActivity.class, content);
                break;
            default:
                break;
        }
    }

    private Bundle bundle = new Bundle();
    private Intent intent = null;

    protected void startActivity(Class target, String content) {

        intent = new Intent(getBaseContext(), target);
        bundle.putString("content", content);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void startActivity(Class target) {

        intent = new Intent(getBaseContext(), target);
        startActivity(intent);
    }

    /**
     * @return 已经保存的所有的wifi账号和密码
     */
    public List<WifiInfo> getWifis() {
        //获得系统所有的已经用过的wifi账号和密码
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

        Log.i(TAG, "----wifiConf" + wifiConf);
        //处理获得的数据，得到wifiInfos
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
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
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
