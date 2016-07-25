package com.example.a111.github_settings.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a111.github_settings.R;

import java.util.List;

/**
 * Created by 111 on 2016/5/16.
 */
public class WifiAdapter extends BaseAdapter {

    private LayoutInflater mInflater = null;
    private Context context;
    private List<ScanResult> scanResults;

    public WifiAdapter(Context context, List<ScanResult> scanResults) {
        this.context = context;
        this.scanResults = scanResults;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return scanResults.size();
    }

    @Override
    public ScanResult getItem(int position) {
        return scanResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        ScanResult scanResult = scanResults.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_wifi_item, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_wifi_name);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_wifi);
            viewHolder.state = (TextView) convertView.findViewById(R.id.tv_wifi_state);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(scanResult.SSID);

        setImage(viewHolder, scanResult);
        return convertView;
    }

    private void setImage(ViewHolder viewHolder, ScanResult scanResult) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        android.net.wifi.WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String name = wifiInfo.getSSID().replace("\"", "");
        if (!name.equals(scanResult.SSID)) {
            if (Math.abs(scanResult.level) > 100) {

                viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_0_dark));

            } else if (Math.abs(scanResult.level) > 80) {

                viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_1_dark));

            } else if (Math.abs(scanResult.level) > 70) {

                viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_2_dark));

            } else if (Math.abs(scanResult.level) > 60) {

                viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_3_dark));

            } else if (Math.abs(scanResult.level) > 50) {

                viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_4_dark));

            } else {

                viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wifi_lock_signal_4_dark));
            }
        } else {

            viewHolder.state.setText("已连接");

            if (Math.abs(scanResult.level) > 100) {

                viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wifi_signal_0_dark));

            } else if (Math.abs(scanResult.level) > 80) {

                viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wifi_signal_1_dark));

            } else if (Math.abs(scanResult.level) > 70) {

                viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wifi_signal_2_dark));

            } else if (Math.abs(scanResult.level) > 60) {

                viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wifi_signal_3_dark));

            } else if (Math.abs(scanResult.level) > 50) {

                viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wifi_signal_4_dark));

            } else {
                viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wifi_signal_4_dark));
            }
        }
    }

    static class ViewHolder {
        public TextView name, state;
        public ImageView imageView;
    }
}
