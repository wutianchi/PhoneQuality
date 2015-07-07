package com.cattsoft.phone.quality.ui.fragments.stats;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cattsoft.commons.digest.StringUtils;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.SmsStructure;
import com.cattsoft.phone.quality.service.observer.SmsContentObserver;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.cattsoft.phone.quality.ui.widget.chart.BarChart;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DataType;
import org.achartengine.GraphicalView;
import org.joda.time.DateTime;
import roboguice.inject.InjectView;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Xiaohong on 2014/5/15.
 */
public class SmsFragment extends RoboLazyFragment {
    @InjectView(R.id.stats_voice_usage)
    FrameLayout voice_usage_layout;
    @InjectView(R.id.stats_voice_prop)
    LinearLayout voice_prop_layout;
    @InjectView(R.id.stats_sms_scroll)
    HorizontalScrollView chartScrollView;

    @InjectView(R.id.prop_sms_mmsg)
    TextView mmsg;

    BarChart usageBarChart;
    BarChart propBarChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_stats_sms, container, false);
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
        NumberFormat valueFormat = new NumberFormat() {
            DecimalFormat format = new DecimalFormat("#.#");

            @Override
            public StringBuffer format(double v, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                if (v == 0)
                    return stringBuffer;
                return stringBuffer.append(format.format(v)).append("条");
            }

            @Override
            public StringBuffer format(long l, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                if (l == 0)
                    return stringBuffer;
                return stringBuffer.append(format.format(l)).append("条");
            }

            @Override
            public Number parse(String s, ParsePosition parsePosition) {
                return null;
            }
        };

        List<double[]> values = new ArrayList<double[]>();
        RuntimeExceptionDao<SmsStructure, Long> dao = getApplication().getDatabaseHelper().getSmsStructureDAO();
        String sql = "SELECT count(id) as count, CAST(strftime('%d', ddate / 1000, 'unixepoch', 'localtime') AS INTEGER) as day " +
                "FROM(" +
                "  SELECT id, strftime('%Y-%m-%d', ddate / 1000, 'unixepoch', 'localtime') date, ddate" +
                "  FROM pq_sms_structure WHERE TYPE = ? and strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')" +
                ") GROUP BY date";
        // -- 当月短信数据 - 接收
        double[] receiveData = new double[DateTime.now().dayOfMonth().getMaximumValue()];
        try {
            GenericRawResults<Object[]> rawResults = dao.queryRaw(sql, new DataType[]{DataType.DOUBLE, DataType.INTEGER}, Integer.toString(SmsContentObserver.SMS_TYPE_INBOX));
            List<Object[]> list = rawResults.getResults();
            for (Object[] objs : list)
                receiveData[(Integer) objs[1] - 1] = (Double) objs[0];
            rawResults.close();
        } catch (Exception e) {
            Log.e("voice", "查询短信发送数据时出现异常", e);
        }
        // -- 当月短信数据 - 发送
        double[] sentData = new double[DateTime.now().dayOfMonth().getMaximumValue()];
        try {
            GenericRawResults<Object[]> rawResults = dao.queryRaw(sql, new DataType[]{DataType.DOUBLE, DataType.INTEGER}, Integer.toString(SmsContentObserver.SMS_TYPE_SENT));
            List<Object[]> list = rawResults.getResults();
            for (Object[] objs : list)
                sentData[(Integer) objs[1] - 1] = (Double) objs[0];
            rawResults.close();
        } catch (Exception e) {
            Log.e("voice", "查询短信接收数据时出现异常", e);
        }

        values.add(sentData);
        values.add(receiveData);
        usageBarChart = new BarChart("", new String[]{"发送", "接收"}, values);
        usageBarChart.setyTitle("");
        usageBarChart.setxTitle("");
        usageBarChart.setColors(new int[]{getResources().getColor(R.color.blue),
                getResources().getColor(R.color.forestgreen)
        });
        int today = DateTime.now().getDayOfMonth();
        int days = DateTime.now().dayOfMonth().getMaximumValue();

        usageBarChart.init(getActivity().getApplicationContext());

        usageBarChart.getRenderer().setYLabels(0);
        usageBarChart.getRenderer().setXLabels(0);
        usageBarChart.getRenderer().setShowLegend(true);
        for (int i = 0; i <= days; i++)
            usageBarChart.getRenderer().addXTextLabel(i, (i > 0 ? (i == today ? "今天" : "" + i) : ""));
        usageBarChart.getRenderer().setXRoundedLabels(true);

        for (int i = 0; i < usageBarChart.getRenderer().getSeriesRendererCount(); i++)
            usageBarChart.getRenderer().getSeriesRendererAt(i).setChartValuesFormat(valueFormat);

        GraphicalView graphicalView = usageBarChart.execute(getActivity().getApplicationContext());
        graphicalView.setMinimumWidth(days * 60);
        voice_usage_layout.addView(graphicalView);

        // ------------ 排行
        values = new ArrayList<double[]>();
        List<String> names = new LinkedList<String>();
        String topSql = "SELECT count(id) as count, name, number " +
                "FROM (" +
                "  SELECT id, name, number, strftime('%Y-%m-%d', ddate / 1000, 'unixepoch', 'localtime') date" +
                "  FROM pq_sms_structure WHERE TYPE = 1 and strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')" +
                ") GROUP BY number ORDER BY count DESC limit 5";
        try {
            GenericRawResults<Object[]> rawResults = dao.queryRaw(topSql, new DataType[]{DataType.DOUBLE, DataType.STRING, DataType.STRING});
            List<Object[]> list = rawResults.getResults();
            for (int i = 0; i < list.size(); i++) {
                double[] vals = new double[list.size()];
                Object[] objs = list.get(i);
                vals[i] = (Double) objs[0];
                values.add(vals);
                names.add(StringUtils.isNotEmpty((String) objs[1]) ? (String) objs[1] : (String) objs[2]);
            }
            rawResults.close();
        } catch (Exception e) {
            Log.e("sms", "查询短信排行数据时出现异常", e);
        }

        propBarChart = new BarChart("", names.toArray(new String[names.size()]), values);
        propBarChart.setyTitle("");
        propBarChart.setxTitle("");
        int[] colors = new int[]{getResources().getColor(R.color.color2), getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color4), getResources().getColor(R.color.color5),
                getResources().getColor(R.color.color9)};

        int[] colorVals = new int[names.size()];
        for (int i = 0; i < names.size(); i++)
            colorVals[i] = colors[i];
        propBarChart.setColors(colorVals);
        propBarChart.init(getActivity().getApplicationContext());

        NumberFormat numberFormat = new NumberFormat() {
            DecimalFormat format = new DecimalFormat("#.#");

            @Override
            public StringBuffer format(double v, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                return stringBuffer.append(v > 0 ? format.format(v) + "条" : "");
            }

            @Override
            public StringBuffer format(long l, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                return stringBuffer.append(l > 0 ? format.format(l) + "条" : "");
            }

            @Override
            public Number parse(String s, ParsePosition parsePosition) {
                return null;
            }
        };
        for (int i = 0; i < propBarChart.getRenderer().getSeriesRendererCount(); i++)
            propBarChart.getRenderer().getSeriesRendererAt(i).setChartValuesFormat(numberFormat);
        propBarChart.getRenderer().setXLabels(0);
        propBarChart.getRenderer().setYLabels(0);
        propBarChart.getRenderer().setShowLabels(false);
        propBarChart.getRenderer().setShowLegend(true);
        for (int i = 0; i <= values.size(); i++)
            propBarChart.getRenderer().addXTextLabel(i, (i > 0 ? names.get(i - 1) : ""));
        propBarChart.getRenderer().setXRoundedLabels(true);
        if (values.size() == 0)
            voice_prop_layout.addView(getActivity().getLayoutInflater().inflate(R.layout.no_data_mask, null));
        else
            voice_prop_layout.addView(propBarChart.execute(getActivity().getApplicationContext()));
        try {
            Cursor cursor = getActivity().getContentResolver().query(Uri.parse("content://mms"), null, null, null, null);
            int count = cursor.getCount();
            if (count > 0)
                mmsg.setText(Integer.toString(cursor.getCount()) + "条");
            else
                mmsg.setText("未接收");
            cursor.close();
        } catch (Exception e) {
        }

        chartScrollView.post(new Runnable() {
            @Override
            public void run() {
                chartScrollView.smoothScrollTo((DateTime.now().getDayOfMonth() - 1) * 60, 0);
            }
        });
    }
}
