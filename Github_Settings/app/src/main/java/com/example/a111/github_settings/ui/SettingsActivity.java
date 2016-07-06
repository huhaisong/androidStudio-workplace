package com.example.a111.github_settings.ui;

import android.content.Intent;
import android.preference.PreferenceActivity;

import com.example.a111.github_settings.R;
import com.example.a111.github_settings.ui.bluetooth.BluetoothActivity;
import com.example.a111.github_settings.ui.wifi.WifiActivity;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        loadHeadersFromResource(R.xml.settings, target);
    }

    @Override
    public void onHeaderClick(Header header, int position) {
        super.onHeaderClick(header, position);
        if (header.title.equals("WLAN")) {

            Intent intent = new Intent(this, WifiActivity.class);
            startActivity(intent);
        } else if (header.title.equals("蓝牙")) {
            Intent intent = new Intent(this, BluetoothActivity.class);
            startActivity(intent);
        } else if (header.title.equals("生成二维码")) {
            Intent intent = new Intent(this, QRSettingActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }
}
