package com.cattsoft.phone.quality.ui.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.utils.Wifis;

import java.util.List;

/**
 * Created by Xiaohong on 14-1-24.
 */
public class WiFiItemAdapter extends ArrayAdapter<ScanResult> {

    public WiFiItemAdapter(Context context, List<ScanResult> results) {
        super(context, R.layout.wifi_info_item, results);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ScanResult result = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.wifi_info_item, null);
            viewHolder.ssid = (TextView) convertView.findViewById(R.id.ssid);
            viewHolder.channel = (TextView) convertView.findViewById(R.id.channel);
            viewHolder.signal = (ProgressBar) convertView.findViewById(R.id.signal);
            viewHolder.signal_text = (TextView) convertView.findViewById(R.id.signal_text);
            viewHolder.power = (TextView) convertView.findViewById(R.id.power);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.ssid.setText(result.SSID + "（" + result.BSSID + "）");
        viewHolder.channel.setText(Integer.toString(Wifis.getChannelFromFrequency(result.frequency)));
        viewHolder.signal.setProgress(100 - Math.abs(result.level));
        viewHolder.signal_text.setText(result.level + " dBm");
        viewHolder.power.setText(result.frequency + " MHz");
        // Return the completed view to render on screen
        return convertView;
    }

    private static class ViewHolder {
        TextView ssid;
        TextView channel;
        ProgressBar signal;
        TextView signal_text;
        TextView power;
    }

}
