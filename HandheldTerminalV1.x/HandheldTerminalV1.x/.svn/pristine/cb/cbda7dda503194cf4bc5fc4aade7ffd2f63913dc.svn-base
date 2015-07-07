package com.cattsoft.phone.quality.ui.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.AppTraffic;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Xiaohong on 2014/3/31.
 */
public class AppTrafficAdapter extends ArrayAdapter<AppTraffic> {
    PackageManager packageManager;
    private DecimalFormat format = new DecimalFormat("#.##");

    public AppTrafficAdapter(Context context, List<AppTraffic> list) {
        super(context, R.layout.traffic_app_item, list);
        packageManager = context.getPackageManager();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        AppTraffic stats = getItem(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.traffic_app_item, null);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.ta_icon);
            viewHolder.mobile = (TextView) convertView.findViewById(R.id.ta_mobile);
            viewHolder.wifi = (TextView) convertView.findViewById(R.id.ta_wifi);
            viewHolder.appName = (TextView) convertView.findViewById(R.id.ta_appName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            viewHolder.icon.setImageDrawable(packageManager.getPackageInfo(stats.getPackageName(), 0).applicationInfo.loadIcon(packageManager));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        viewHolder.appName.setText(stats.getAppName());
        viewHolder.mobile.setText(format.format((stats.getMobileRxBytes() + stats.getMobileTxBytes()) / 1024f / 1024f) + "M");
        viewHolder.wifi.setText(format.format((stats.getWifiRxBytes() + stats.getWifiTxBytes()) / 1024f / 1024f) + "M");
        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView appName, mobile, wifi;
    }
}
