package com.example.a111.github_settings.ui.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.a111.github_settings.adapter.BluetoothListViewAdapter;
import com.example.a111.github_settings.config.Config;
import com.example.a111.github_settings.ui.BaseActivity;
import com.example.a111.github_settings.util.FileStore;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by 111 on 2016/5/16.
 */
public class BluetoothActivity extends BaseActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private Switch mSwitch;
    private LinearLayout mSearchLayout, mPairLayout, mNewLayout;
    private ListView mPairListView, mNewListView;
    private BluetoothListViewAdapter mPairAdapter, mNewAdapter;
    private List<String> mNewDatas = new ArrayList<>();
    private List<String> mPairDatas = new ArrayList<>();
    private Set<BluetoothDevice> pairedDevices;
    private Set<BluetoothDevice> newDevices = new HashSet<>();
    private MyBroadcast myBroadcast;
    private FileStore mFileStore;
    private String mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);
        initView();
        initClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPairListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcast);
    }

    private void initView() {
        mFileStore = new FileStore(Config.BLUETOOTH_FILE_PATH, getBaseContext());
        mDevice = "";
        mFileStore.writeBluetooth(mDevice);

        mSwitch = (Switch) findViewById(R.id.switch_bluetooth);
        mSearchLayout = (LinearLayout) findViewById(R.id.layout_search);
        mPairLayout = (LinearLayout) findViewById(R.id.layout_paired);

        mNewLayout = (LinearLayout) findViewById(R.id.layout_new);
        mNewListView = (ListView) findViewById(R.id.lv_new);
        mPairListView = (ListView) findViewById(R.id.lv_paired);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mSwitch.setChecked(true);
            mPairLayout.setVisibility(View.VISIBLE);
        } else {
            mSwitch.setChecked(false);
            mPairLayout.setVisibility(View.GONE);
        }

        //注册广播
        myBroadcast = new MyBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(myBroadcast, filter);
    }

    private void initClick() {

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPairLayout.setVisibility(View.VISIBLE);
                    if (!mBluetoothAdapter.isEnabled()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mBluetoothAdapter.enable();
                            }
                        }).start();
                    }
                } else {
                    mPairLayout.setVisibility(View.GONE);
                    if (mBluetoothAdapter.isEnabled())
                        mBluetoothAdapter.disable();
                }
            }
        });

        mSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewLayout.setVisibility(View.VISIBLE);
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                mBluetoothAdapter.startDiscovery();
                Toast.makeText(getBaseContext(), "正在搜索设备......", Toast.LENGTH_SHORT).show();
            }
        });

        mPairListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }

                Toast.makeText(BluetoothActivity.this, "正在连接设备：" + mPairDatas.get(position),
                        Toast.LENGTH_SHORT).show();
                //开启子线程连接蓝牙设备
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (BluetoothDevice device : pairedDevices) {
                            if (device.getName().equals(mPairDatas.get(position))) {
                                Log.i("test", "----------device.getName():" + device.getName());
                                connectBluetooth(device);
                                return;
                            }
                        }
                    }
                }).start();
            }
        });

        mNewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDevice = mNewDatas.get(position);
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                Toast.makeText(getBaseContext(), "正在配对......", Toast.LENGTH_SHORT).show();
                for (BluetoothDevice device : newDevices) {
                    if (device.getName().equals(mNewDatas.get(position))) {
                        Log.i("mNewListView", "----------device.getName():" + device.getName());
                        try {
                            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                            createBondMethod.invoke(device);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
            }
        });
    }

    //蓝牙广播
    private class MyBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothAdapter.ACTION_STATE_CHANGED
                    .equals(intent.getAction())) {//蓝牙状态改变
                Log.i("ACTION_STATE_CHANGED", "----------");
                setPairListView();
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED
                    .equals(intent.getAction())) {//绑定不同设备状态改变
                Log.i("BOND_STATE_CHANGED", "----------");
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    //如果绑定的设备跟点击的设备名称相同，表示配对成功，保存进蓝牙路径；
                    if (device.getName().equals(mDevice)) {
                        mFileStore.writeBluetooth(mDevice);
                    }
                    //更新mPairListView和mNewListView
                    for (String s : mNewDatas) {
                        if (s.equals(device.getName())) {
                            mNewDatas.remove(s);
                            setNewListView();
                            setPairListView();
                            return;
                        }
                    }
                }
                setPairListView();
            } else if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {//搜索到设备
                Log.i("ACTION_FOUND", "----------");
                newDevices.add(device);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (mNewDatas.size() > 0) {
                        boolean exist = false;
                        Log.i("aaa", "onReceive: " + mNewDatas.toString());
                        for (String s : mNewDatas) {
                            if (s.equals(device.getName())) {
                                exist = true;
                            }
                        }
                        if (!exist) {
                            mNewDatas.add(device.getName());
                        }
                    } else {
                        mNewDatas.add(device.getName());
                    }
                    setNewListView();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {//搜索状态改变
                Log.i("DISCOVERY_STARTED", "----------");
                mNewDatas.clear();
                setNewListView();
            }
        }
    }

    //配对蓝牙设备的list
    private void setPairListView() {
        Log.i("setPairListView", "--------");
        mPairDatas.clear();
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            mPairDatas.add(device.getName());
        }
        if (mPairDatas.size() > 0) {
            mPairAdapter = new BluetoothListViewAdapter(true, getBaseContext(), mPairDatas);
            mPairListView.setAdapter(mPairAdapter);
            setListViewHeightBasedOnChildren(mPairListView);
        }
    }

    //可用蓝牙设备的list
    private void setNewListView() {
        Log.i("setNewListView", "--------");
        mNewAdapter = new BluetoothListViewAdapter(false, getBaseContext(), mNewDatas);
        mNewListView.setAdapter(mNewAdapter);
        setListViewHeightBasedOnChildren(mNewListView);
    }

    //根据listView的item设置listView的高度
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

    //链接蓝牙设备
    private void connectBluetooth(BluetoothDevice device) {
        // 固定的UUID
        final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
        UUID uuid = UUID.fromString(SPP_UUID);
        try {
            device.createRfcommSocketToServiceRecord(uuid).connect();
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BluetoothActivity.this, "连接失败！", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
