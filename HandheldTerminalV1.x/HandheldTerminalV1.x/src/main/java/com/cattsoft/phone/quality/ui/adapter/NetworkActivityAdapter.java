package com.cattsoft.phone.quality.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.NetworkActivity;
import com.cattsoft.phone.quality.utils.MobileNetType;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Xiaohong on 2014/3/31.
 */
public class NetworkActivityAdapter extends ArrayAdapter<NetworkActivity> {
    private DecimalFormat format = new DecimalFormat("#.##");

    public NetworkActivityAdapter(Context context, List<NetworkActivity> objects) {
        super(context, R.layout.network_activity_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        NetworkActivity activity = getItem(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.network_activity_item, null);
            viewHolder.activated = (TextView) convertView.findViewById(R.id.na_activated);
            viewHolder.freeze = (TextView) convertView.findViewById(R.id.na_freeze);
            viewHolder.type = (TextView) convertView.findViewById(R.id.na_type);
            viewHolder.traffics = (TextView) convertView.findViewById(R.id.na_traffic);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.activated.setText(activity.getActivate().toString("HH:mm"));
        viewHolder.freeze.setText(activity.getDeactivate().toString("HH:mm"));
        viewHolder.type.setText(MobileNetType.NetWorkType.toType(activity.getNetworkType()).nickname);
        viewHolder.traffics.setText(format.format(activity.getTraffic() / 1024f / 1024f) + "M");

        return convertView;
    }

    private static class ViewHolder {
        TextView activated, freeze, type, traffics;
    }
}
