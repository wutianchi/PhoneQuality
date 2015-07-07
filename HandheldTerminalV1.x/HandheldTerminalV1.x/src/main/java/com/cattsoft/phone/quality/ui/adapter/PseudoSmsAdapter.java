package com.cattsoft.phone.quality.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.PseudoSms;

import java.util.List;

/**
 * Created by Xiaohong on 2014/4/11.
 */
public class PseudoSmsAdapter extends ArrayAdapter<PseudoSms> {
    public PseudoSmsAdapter(Context context, List<PseudoSms> objects) {
        super(context, R.layout.pseudo_sms_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PseudoSms result = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.pseudo_sms_item, null);
            viewHolder.address = (TextView) convertView.findViewById(R.id.sms_address);
            viewHolder.date = (TextView) convertView.findViewById(R.id.sms_date);
            viewHolder.content = (TextView) convertView.findViewById(R.id.sms_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.address.setText(result.getAddress());
        viewHolder.date.setText(result.getDdate().toString("MM-dd HH:mm"));
        viewHolder.content.setText(result.getContent());
        // Return the completed view to render on screen
        return convertView;
    }

    static class ViewHolder {
        TextView address;
        TextView date;
        TextView content;
    }
}
