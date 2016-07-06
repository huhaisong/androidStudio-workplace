package com.example.a111.github_settings.util;


import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.example.a111.github_settings.bean.WifiInfo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 111 on 2016/5/17.
 */
public class FileStore {

    private final String mBuffPath;
    private Context context;

    public FileStore(String mBuffPath, Context context) {
        this.mBuffPath = mBuffPath;
        this.context = context;
    }


    public synchronized String readBluetooth() {
        FileInputStream fis = null;
        String bluetooth = null;
        try {
            fis = context.openFileInput(mBuffPath);
            int len = fis.available();
            byte[] buffer = new byte[len];
            fis.read(buffer);
            bluetooth = new String(buffer);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bluetooth;
    }


    public synchronized void writeBluetooth(String s) {
        try {
            FileOutputStream fos = context.openFileOutput(mBuffPath, Context.MODE_PRIVATE);
            fos.write(s.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入单个信息
     *
     * @param wifiInfo wifi的信息
     */
    public synchronized void writeWifiItem(WifiInfo wifiInfo) {
        if (wifiInfo == null) {
            return;
        }
        boolean exist = false;
        List<WifiInfo> oldList = get();
        for (WifiInfo w : oldList) {
            if (w.equals(wifiInfo)) {
                exist = true;
            }
        }
        if (!exist) {
            oldList.add(wifiInfo);
        }
        put(oldList);
    }

    /**
     * 删除单个信息
     *
     * @param remove wifi的信息
     */
    public synchronized void remove(WifiInfo remove) {

        List<WifiInfo> oldList = get();
        WifiInfo target = null;
        for (WifiInfo w : oldList) {
            if (w.equals(remove)) {
                target = w;
            }
        }
        if (target != null) {
            oldList.remove(target);
        }
        put(oldList);
    }

    /**
     * 读取所有的信息
     *
     * @return 返回所有的wifi信息
     */
    public synchronized List<WifiInfo> read() {
        return get();
    }

    /**
     * 写入整个信息
     *
     * @param list wifi信息的集合
     */
    private void put(List<WifiInfo> list) {
        try {

            // 打开文件
            FileOutputStream fos = context.openFileOutput(mBuffPath, Context.MODE_PRIVATE);
            String json = JSON.toJSONString(list, true);
            fos.write(json.getBytes());
            // 释放资源
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得所有信息
     *
     * @return 返回所有的wifi信息
     */
    private List<WifiInfo> get() {

        List<WifiInfo> list = new ArrayList<WifiInfo>();
        try {
            // 打开文件
            FileInputStream fis = context.openFileInput(mBuffPath);
            // 读取文件
            int len = fis.available();
            byte[] buffer = new byte[len];
            fis.read(buffer);
            String json = new String(buffer);
            list = JSON.parseArray(json, WifiInfo.class);
            // 释放资源
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}


