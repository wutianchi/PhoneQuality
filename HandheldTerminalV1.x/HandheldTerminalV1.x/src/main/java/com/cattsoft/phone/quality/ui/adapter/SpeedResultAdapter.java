package com.cattsoft.phone.quality.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.SpeedTestResult;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.cattsoft.phone.quality.utils.Speeds;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Xiaohong on 14-3-23.
 */
public class SpeedResultAdapter extends ArrayAdapter<SpeedTestResult> {
    int resourceId;

    private DecimalFormat decimalFormat = new DecimalFormat("#.#");

    private String unit;

    public SpeedResultAdapter(Context context, List<SpeedTestResult> objects) {
        super(context, R.layout.layout_speed_history_item, objects);
        resourceId = R.layout.layout_speed_history_item;
        this.unit = context.getString(R.string.kByteSec);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SpeedTestResult result = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(resourceId, null);
            viewHolder.server_nettype = (TextView) convertView.findViewById(R.id.speed_server_nettype);
            viewHolder.speed_download = (TextView) convertView.findViewById(R.id.speed_download);
            viewHolder.speed_upload = (TextView) convertView.findViewById(R.id.speed_upload);
            viewHolder.speed_date = (TextView) convertView.findViewById(R.id.speed_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final DecimalFormat format = new DecimalFormat("#.##");

//        String carrier = "未识别";
//        if (result.getOperator() == 46000 ||
//                result.getOperator() == 46002 ||
//                result.getOperator() == 46007) {
//            carrier = "中国移动";
//        } else if (result.getOperator() == 46001 ||
//                result.getOperator() == 46010) {
//            carrier = "中国联通";
//        } else if (result.getOperator() == 46003) {
//            carrier = "中国电信";
//        }

        // Populate the data into the template view using the data object
        viewHolder.server_nettype.setText(result.getServerName());
        viewHolder.speed_date.setText(result.getDdate().toString("MM-dd HH:mm"));
        viewHolder.speed_download.setText(decimalFormat.format(result.getDownload()) + unit);
        viewHolder.speed_upload.setText(decimalFormat.format(result.getUpload()) + unit);
        // Return the completed view to render on screen
        return convertView;
    }

    static class ViewHolder {
        TextView server_nettype;
        TextView speed_download;
        TextView speed_upload;
        TextView speed_date;
    }
}
