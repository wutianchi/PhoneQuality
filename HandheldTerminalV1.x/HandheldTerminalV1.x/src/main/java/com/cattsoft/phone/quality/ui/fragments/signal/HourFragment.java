package com.cattsoft.phone.quality.ui.fragments.signal;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.SignalActivity;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.cattsoft.phone.quality.ui.widget.chart.BudgetDoughnutChart;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DataType;
import roboguice.inject.InjectView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaohong on 2014/5/12.
 */
public class HourFragment extends RoboLazyFragment {
    @InjectView(R.id.levelLayout)
    LinearLayout levelLayout;
    @InjectView(R.id.propLayout)
    LinearLayout propLayout;
    @InjectView(R.id.prop_signal_level)
    ImageView signal_level;

    @InjectView(R.id.prop_signal_dbm)
    TextView signal_dbm;
    @InjectView(R.id.prop_signal_asu)
    TextView signal_asu;

    @InjectView(R.id.prop_signal_no)
    TextView signal_no;
    @InjectView(R.id.prop_signal_2g)
    TextView signal_2g;
    @InjectView(R.id.prop_signal_3g)
    TextView signal_3g;
    @InjectView(R.id.prop_signal_4g)
    TextView signal_4g;
    @InjectView(R.id.prop_signal_4g_layout)
    RelativeLayout signal_4g_layout;

    BudgetDoughnutChart propChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_signal_hour, container, false);
    }

    @Override
    protected void doStuffInBackground(Application app) {
        super.doStuffInBackground(app);
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
        List<String[]> titles = new ArrayList<String[]>();
        titles.add(new String[]{"无信号", "2G", "3G", "4G"});

        List<double[]> values = new ArrayList<double[]>();
        String sql = "SELECT sum(noSignal), sum(signal2G), sum(signal3G), sum(signal4G), (sum(signals)/sum(targets)), (sum(signals4G)/(CASE SUM(targets4G) WHEN 0 THEN 1 ELSE SUM(TARGETS4G) END)) " +
                " FROM PQ_SIGNAL_ACTIVITY " +
                " WHERE datetime(DDATE/1000, 'unixepoch', 'localtime') >= datetime('now', '-1 hour', 'localtime')";
        RuntimeExceptionDao<SignalActivity, Long> dao = getApplication().getDatabaseHelper().getSignalActivitieDAO();
        GenericRawResults<Object[]> genericRawResults = null;
        double signalSum = 0;
        try {
            genericRawResults = dao.queryRaw(sql, new DataType[]{DataType.LONG, DataType.LONG, DataType.LONG, DataType.LONG, DataType.LONG, DataType.LONG});
            Object[] result = genericRawResults.getFirstResult();
            double[] arr = new double[]{(Long) result[0], (Long) result[1], (Long) result[2], (Long) result[3]};
            values.add(arr);

            signalSum = (arr[0] + arr[1] + arr[2] + arr[3]);

            NumberFormat nf = NumberFormat.getPercentInstance();
            nf.setMinimumFractionDigits(1);
            nf.setParseIntegerOnly(true);

            signal_no.setText(nf.format(arr[0] / signalSum));
            signal_2g.setText(nf.format(arr[1] / signalSum));
            signal_3g.setText(nf.format(arr[2] / signalSum));
            signal_4g.setText(nf.format(arr[3] / signalSum));

            long asu = (Long) result[4];
            signal_dbm.setText((asu * 2 - 113) + " dBm");
            signal_asu.setText(asu + " asu");

            if (asu <= 2 || asu == 99) signal_level.setImageResource(R.drawable.wifi_d1);
            else if (asu >= 14) signal_level.setImageResource(R.drawable.wifi_d6);
            else if (asu >= 12) signal_level.setImageResource(R.drawable.wifi_d5);
            else if (asu >= 8) signal_level.setImageResource(R.drawable.wifi_d4);
            else if (asu >= 5) signal_level.setImageResource(R.drawable.wifi_d3);
            else signal_level.setImageResource(R.drawable.wifi_d2);
        } catch (Exception e) {
            Log.w(TAG, "无法加载数据", e);
        }
        closeQuietly(genericRawResults);
        signal_4g_layout.setVisibility(View.VISIBLE);

        int[] colors = new int[]{getResources().getColor(R.color.mid_red),
                getResources().getColor(R.color.blue),
                getResources().getColor(R.color.dark_cyan),
                getResources().getColor(R.color.text_dark)};

        propChart = new BudgetDoughnutChart("", titles, values);
        propChart.setColors(colors);
        propChart.init(getActivity().getApplicationContext());
        propChart.getRenderer().setShowLegend(false);
        propChart.getRenderer().setMargins(new int[]{0, 0, 0, 0});
        propChart.getRenderer().setExternalZoomEnabled(true);
        propChart.getRenderer().setStartAngle(220);
        propChart.getRenderer().setFitLegend(false);

        if (signalSum <= 0) {
            propLayout.addView(getActivity().getLayoutInflater().inflate(R.layout.no_data_mask, null));
        } else {
            propLayout.addView(propChart.execute(getActivity().getApplicationContext()));
        }
    }
}
