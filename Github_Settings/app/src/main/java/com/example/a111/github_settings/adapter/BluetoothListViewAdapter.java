package com.example.a111.github_settings.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a111.github_settings.R;
import com.example.a111.github_settings.ui.bluetooth.BluetoothDeviceSettingActivity;

import java.util.List;

/**
 * Created by 111 on 2016/5/20.
 */
public class BluetoothListViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> datas;
    private boolean isPaired;

    public BluetoothListViewAdapter(boolean isPaired, Context context, List<String> datas) {
        this.isPaired = isPaired;
        this.context = context;
        this.datas = datas;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.bluetooth_device_item, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_bluetooth_image);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_bluetooth_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(datas.get(position));
        if (!isPaired) {
            viewHolder.imageView.setVisibility(View.GONE);
        } else {
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BluetoothDeviceSettingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("name", datas.get(position));
                    context.startActivity(intent);
                }
            });
            viewHolder.imageView.setFocusable(false);
        }
        return convertView;
    }

    static class ViewHolder {
        public TextView name;
        public ImageView imageView;
    }
}
