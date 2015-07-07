package com.cattsoft.phone.quality.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.cattsoft.commons.digest.StringUtils;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.SpeedTimeLine;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.cattsoft.phone.quality.utils.Speeds;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Xiaohong on 14-3-6.
 */
public class SpeedShareAdapter extends ArrayAdapter<SpeedTimeLine> {
    DecimalFormat format = new DecimalFormat("0.0#");

    public SpeedShareAdapter(Context context, ArrayList<SpeedTimeLine> results) {
        super(context, R.layout.layout_speed_timeline_item, results);
    }

    public int addTop(SpeedTimeLine line, int index) {
        insert(line, index);

        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SpeedTimeLine result = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.layout_speed_timeline_item, null);
            viewHolder.carriec_image = (BootstrapCircleThumbnail) convertView.findViewById(R.id.carriec_thumbnail);
            viewHolder.speed_item_name = (TextView) convertView.findViewById(R.id.speed_item_name);
            viewHolder.speed_item_createTime = (TextView) convertView.findViewById(R.id.speed_item_createTime);
            viewHolder.speed_item_content = (TextView) convertView.findViewById(R.id.speed_item_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        try {
            String carrier = "未识别";
            MobileNetType.NetWorkType type = MobileNetType.NetWorkType.toType(result.getNetType());
            if (type == MobileNetType.NetWorkType.TYPE_WIFI) {
                carrier = "";
                viewHolder.carriec_image.setImage(R.drawable.wlan);
            } else {
                if (result.getOperator() == 46000 ||
                        result.getOperator() == 46002 ||
                        result.getOperator() == 46007) {
                    carrier = "中国移动";
                    viewHolder.carriec_image.setImage(R.drawable.carrier_cmcc);
                } else if (result.getOperator() == 46001 ||
                        result.getOperator() == 46010) {
                    carrier = "中国联通";
                    viewHolder.carriec_image.setImage(R.drawable.carrier_cucc);
                } else if (result.getOperator() == 46003) {
                    carrier = "中国电信";
                    viewHolder.carriec_image.setImage(R.drawable.carrier_ctcc);
                }
            }
            String kbps = getContext().getString(R.string.kbitSec);
            String kmps = getContext().getString(R.string.kMbitSec);

            double ukbps = result.getUpload() / 1024.0 * 8.0;
            double dkbps = result.getDownload() / 1024.0 * 8.0;

            double umbps = 0d;
//            if(ukbps >= 1024.0)
//                umbps = ukbps / 1024.0;
            double dmbps = 0d;
//            if(dkbps >= 1024.0)
//                dmbps = dkbps / 1024.0;

            viewHolder.speed_item_name.setText("");
            viewHolder.speed_item_content.setText("我在" + carrier + " " + type.nickname + "网络下测速，" +
                    (StringUtils.isEmpty(result.getAddress()) ? "" : "\n位置：" + result.getAddress()) + "\n网络延迟：" + result.getPing() + " ms" +
                    "\n测试下载速率：" + Speeds.humanReadableByteCount(result.getDownload(), true, "%.2f %sb/s") +
                    "\n测试上传速率：" + Speeds.humanReadableByteCount(result.getUpload(), true, "%.2f %sb/s"));
            viewHolder.speed_item_createTime.setText(result.getDdate().toString("yyyy-MM-dd HH:mm"));
        } catch (Exception e) {
        }
        return convertView;
    }

    private static class ViewHolder {
        BootstrapCircleThumbnail carriec_image;
        TextView speed_item_name;
        TextView speed_item_createTime;
        TextView speed_item_content;
    }
}
