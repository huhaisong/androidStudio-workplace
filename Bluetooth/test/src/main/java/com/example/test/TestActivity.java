package com.example.test;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.UUID;

/**
 * Created by 111 on 2016/7/27.
 */
public class TestActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(TestActivity.this, "你点击了菜单1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item2:
                Toast.makeText(TestActivity.this, "你点击了菜单2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item3:
                Toast.makeText(TestActivity.this, "你点击了菜单3", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(TestActivity.this, "没有点击任何东西", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}
