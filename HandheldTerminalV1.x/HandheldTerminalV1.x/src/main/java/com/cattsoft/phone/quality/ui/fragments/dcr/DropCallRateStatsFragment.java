package com.cattsoft.phone.quality.ui.fragments.dcr;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.CallsStructure;
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
 * Created by Xiaohong on 14-3-19.
 */
public class DropCallRateStatsFragment extends RoboLazyFragment {
    @InjectView(R.id.dcr_today)
    LinearLayout today_layout;
    @InjectView(R.id.dcr_month)
    LinearLayout month_layout;

    @InjectView(R.id.prop_today)
    TextView prop_today;
    @InjectView(R.id.prop_month)
    TextView prop_month;

    BudgetDoughnutChart dayChart = null;
    BudgetDoughnutChart monthChart = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_dcr_stats, container, false);
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
        RuntimeExceptionDao<CallsStructure, Long> dao = getApplication().getDatabaseHelper().getCallsStructureDAO();
        String[] causes = getResources().getStringArray(R.array.drop_case_error);
        StringBuffer argDot = new StringBuffer();
        for (int i = 0; i < causes.length; i++)
            argDot.append("?").append(i < (causes.length - 1) ? ", " : "");

        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        nf.setParseIntegerOnly(true);

        // -- 本月掉话率统计
        String monthSql = "SELECT " +
                "  (SELECT count(id) FROM PQ_CALL_STRUCTURE WHERE strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime') " +
                "                                              AND CAUSE in (" + argDot + ")) as dropcall, " +
                "  (SELECT count(id) FROM PQ_CALL_STRUCTURE WHERE strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')) as calls";
        // -- 当天掉话率统计
        String daySql = "SELECT " +
                "  (SELECT count(id) FROM PQ_CALL_STRUCTURE WHERE strftime('%Y-%m-%d', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m-%d', 'now', 'localtime') " +
                "                                              AND CAUSE in (" + argDot + ")) as dropcall, " +
                "  (SELECT count(id) FROM PQ_CALL_STRUCTURE WHERE strftime('%Y-%m-%d', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m-%d', 'now', 'localtime')) as calls";

        int[] colors = new int[]{getResources().getColor(R.color.mid_red),
                getResources().getColor(R.color.dark_cyan)};

        List<String[]> titles = new ArrayList<String[]>();
        titles.add(new String[]{"掉话率", "接通率"});

        List<double[]> values = new ArrayList<double[]>();
        values.add(new double[]{0, 1});
        double daySum = 0;
        try {
            GenericRawResults<Object[]> rawResults = dao.queryRaw(daySql, new DataType[]{DataType.INTEGER, DataType.INTEGER}, causes);
            Object[] results = rawResults.getFirstResult();
            values.set(0, new double[]{(Integer) results[0], (Integer) results[1]});
            daySum = (Integer) results[0] + (Integer) results[1];
            // 百分比计算
            String dprop = nf.format((Integer) results[0] / daySum);
            String aprop = nf.format(((Integer) results[1] - (Integer) results[0]) / daySum);
            titles.set(0, new String[]{"掉话率(" + dprop + ")", "接通率(" + aprop + ")"});
            prop_today.setText(results[0] + " 次");
            rawResults.close();
        } catch (Exception e) {
            Log.e("dcr", "无法统计当天掉话率", e);
        }
        dayChart = new BudgetDoughnutChart("", titles, values);
        dayChart.setColors(colors);
        dayChart.init(getActivity().getApplicationContext());
        dayChart.getRenderer().setShowLegend(false);
        dayChart.getRenderer().setMargins(new int[]{0, 0, 0, 0});
        dayChart.getRenderer().setExternalZoomEnabled(true);
        dayChart.getRenderer().setStartAngle(220);
        dayChart.getRenderer().setFitLegend(false);
        if (daySum <= 0)
            today_layout.addView(getActivity().getLayoutInflater().inflate(R.layout.no_data_mask, null));
        else
            today_layout.addView(dayChart.execute(getActivity().getApplicationContext()));

        titles = new ArrayList<String[]>();
        titles.add(new String[]{"掉话率", "接通率"});
        values = new ArrayList<double[]>();
        values.add(new double[]{0, 1});
        double monthSum = 0;
        try {
            GenericRawResults<Object[]> rawResults = dao.queryRaw(monthSql, new DataType[]{DataType.INTEGER, DataType.INTEGER}, causes);
            Object[] results = rawResults.getFirstResult();
            values.set(0, new double[]{(Integer) results[0], (Integer) results[1]});
            monthSum = (Integer) results[0] + (Integer) results[1];
            // 百分比计算
            String dprop = nf.format((Integer) results[0] / monthSum);
            String aprop = nf.format(((Integer) results[1] - (Integer) results[0]) / monthSum);
            titles.set(0, new String[]{"掉话率(" + dprop + ")", "接通率(" + aprop + ")"});
            prop_month.setText(results[0] + " 次");
            rawResults.close();
        } catch (Exception e) {
            Log.e("dcr", "无法统计本月掉话率", e);
        }
        monthChart = new BudgetDoughnutChart("", titles, values);
        monthChart.setColors(colors);
        monthChart.init(getActivity().getApplicationContext());
        monthChart.getRenderer().setShowLegend(false);
        monthChart.getRenderer().setMargins(new int[]{0, 0, 0, 0});
        monthChart.getRenderer().setExternalZoomEnabled(true);
        monthChart.getRenderer().setStartAngle(220);
        monthChart.getRenderer().setFitLegend(false);
        if (monthSum <= 0)
            month_layout.addView(getActivity().getLayoutInflater().inflate(R.layout.no_data_mask, null));
        else
            month_layout.addView(monthChart.execute(getActivity().getApplicationContext()));
    }
}