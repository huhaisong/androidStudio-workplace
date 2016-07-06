package com.example.a111.myapplication1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 111 on 2016/5/30.
 */
public class WifiAdapter extends BaseAdapter {

    private Context mContext;
    private List<WifiInfo> mWifiInfos;
    private LayoutInflater mLayoutInflater;

    public WifiAdapter(Context context, List<WifiInfo> wifiInfos) {
        this.mContext = context;
        this.mWifiInfos = wifiInfos;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mWifiInfos.size();
    }

    @Override
    public WifiInfo getItem(int position) {
        return mWifiInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.wifi_item, null);
            viewHolder = new ViewHolder();
            viewHolder.nameTV = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.passTV = (TextView) convertView.findViewById(R.id.tv_pass);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        WifiInfo wifiInfo = getItem(position);
        viewHolder.nameTV.setText("wifi:" + wifiInfo.getSsid());
        viewHolder.passTV.setText("pass:" + wifiInfo.getPassword());
        return convertView;
    }

    private class ViewHolder {

        TextView nameTV, passTV;
    }
}
