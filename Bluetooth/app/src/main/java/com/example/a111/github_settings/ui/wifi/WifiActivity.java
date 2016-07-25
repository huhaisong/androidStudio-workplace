package com.example.a111.github_settings.ui.wifi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.a111.github_settings.R;
import com.example.a111.github_settings.adapter.WifiAdapter;
import com.example.a111.github_settings.bean.WifiInfo;
import com.example.a111.github_settings.config.Config;
import com.example.a111.github_settings.ui.BaseActivity;
import com.example.a111.github_settings.util.FileStore;
import com.example.a111.github_settings.util.WifiUtil;

import java.util.HashSet;
import java.util.List;

/**
 * Created by 111 on 2016/5/16.
 */
public class WifiActivity extends BaseActivity {

    private Switch mSwitch;
    private LinearLayout mListLayout, mAddLayout;
    private WifiUtil mWifiUtil;
    private ListView mWifiListView;
    private List<ScanResult> mScanResults;
    private WifiAdapter mWifiAdapter;
    private List<WifiInfo> mWifiInfos;
    private FileStore mFileStore;
    private LinearLayout mScan;
    private WifiBroadcastReceiver mWifiBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wifi_activity);
        initView();
        initOnclick();
    }

    private void initView() {

        mFileStore = new FileStore(Config.WIFI_FILE_PATH, getBaseContext());
        mWifiUtil = new WifiUtil(getBaseContext());
        mWifiInfos = mFileStore.read();
        mWifiListView = (ListView) findViewById(R.id.lv_wifi);
        mAddLayout = (LinearLayout) findViewById(R.id.layout_add_wifi);
        mListLayout = (LinearLayout) findViewById(R.id.layout_lv_wifi);
        mSwitch = (Switch) findViewById(R.id.switch_wifi);
        mScan = (LinearLayout) findViewById(R.id.tv_scan);

        if (mWifiUtil.checkState() == WifiManager.WIFI_STATE_ENABLED) {
            mSwitch.setChecked(true);
            mListLayout.setVisibility(View.VISIBLE);
        } else if (mWifiUtil.checkState() == WifiManager.WIFI_STATE_DISABLED) {
            mSwitch.setChecked(false);
            mListLayout.setVisibility(View.GONE);
        }

        mWifiBroadcastReceiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        registerReceiver(mWifiBroadcastReceiver, filter);
    }

    private void initOnclick() {
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mListLayout.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mWifiUtil.openWifi();
                        }
                    }).start();
                } else {
                    mListLayout.setVisibility(View.GONE);
                    mWifiUtil.closeWifi();
                }
            }
        });

        mAddLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(WifiConnectionActivity.class);
            }
        });

        mWifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mWifiInfos = mFileStore.read();
                WifiInfo remove = null;
                boolean exist = false;
                for (WifiInfo w : mWifiInfos) {
                    if (mScanResults.get(position).SSID.equals(w.getName())) {
                        remove = w;
                        exist = true;
                    }
                }
                if (exist) {
                    dialog(mScanResults.get(position), remove);
                } else {
                    startActivity(MainWifiConnectionActivity.class, mScanResults.get(position).SSID);
                }
            }
        });


        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "正在扫描...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dialog(ScanResult scanResult, final WifiInfo remove) {

        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(scanResult.SSID)
                .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mWifiUtil.connectWifi(remove);
                            }
                        }).start();

                    }
                }).setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mFileStore.remove(remove);
                        mWifiInfos = mFileStore.read();
                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    //设置以及更新扫描到的wifi列表
    private void setList() {
        Log.i("setList", "--------------------");
        mScanResults = mWifiUtil.getWifiList();
        HashSet<ScanResult> h = new HashSet(mScanResults);
        mScanResults.clear();
        mScanResults.addAll(h);
        mWifiAdapter = new WifiAdapter(getBaseContext(), mScanResults);
        mWifiListView.setAdapter(mWifiAdapter);
        setListViewHeightBasedOnChildren(mWifiListView);
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    private class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("setList", "--------------------WifiBroadcastReceiver");
            setList();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("setList", "--------------------onResume");
        setList();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiBroadcastReceiver);
    }
}

