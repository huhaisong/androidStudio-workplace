package com.example.a111.github_settings.ui.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.a111.github_settings.R;
import com.example.a111.github_settings.ui.BaseActivity;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class BluetoothDeviceSettingActivity extends BaseActivity {


    private TextView mNametv;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_device_setting);
        initView();
    }

    private void initView() {

        mNametv = (TextView) findViewById(R.id.tv_name);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        mNametv.setText(name);
    }

    //取消配对
    public void cancelPair(View v) throws InvocationTargetException, IllegalAccessException {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice d : pairedDevices) {
            if (d.getName().equals(name)) {//如果名字相等，那么就取消配对 。
                try {
                    d.getClass().getMethod("removeBond").invoke(d);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        finish();
    }
}
