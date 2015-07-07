package com.cattsoft.phone.quality.ui.fragments.stats;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.CallsStructure;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.cattsoft.phone.quality.ui.widget.OverScrollView;
import com.cattsoft.phone.quality.ui.widget.chart.BarChart;
import com.cattsoft.phone.quality.ui.widget.chart.PieChart;
import com.cattsoft.phone.quality.utils.Numbers;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Xiaohong on 2014/5/15.
 */
public class VoiceFragment extends RoboLazyFragment {
    @InjectView(R.id.scrollView)
    OverScrollView scrollView;
    @InjectView(R.id.rootView)
    LinearLayout rootView;
    @InjectView(R.id.stats_voice_usage)
    FrameLayout voice_usage_layout;
    @InjectView(R.id.stats_voice_scroll)
    HorizontalScrollView voice_usage_scroll;
    @InjectView(R.id.stats_voice_prop)
    LinearLayout voice_prop_layout;
    @InjectView(R.id.prop_call_timer)
    TextView prop_call_timer;
    @InjectView(R.id.prop_caller)
    TextView prop_caller;
    @InjectView(R.id.prop_answer)
    TextView prop_answer;
    @InjectView(R.id.stats_voice_scroll)
    HorizontalScrollView chartScrollView;

    BarChart usageBarChart;
    PieChart propPieChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_stats_voice, container, false);
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
        List<double[]> values = new ArrayList<double[]>();
        String[] causes = getResources().getStringArray(R.array.drop_case_error);
        StringBuffer argDot = new StringBuffer();
        for (int i = 0; i < causes.length; i++)
            argDot.append("?").append(i < (causes.length - 1) ? ", " : "");

        RuntimeExceptionDao<CallsStructure, Long> dao = getApplication().getDatabaseHelper().getCallsStructureDAO();

        // -- 当月每日通话数据 - 主叫
        String callerSql = "SELECT sum(duration) as durations, CAST(strftime('%d', ddate / 1000, 'unixepoch', 'localtime') as integer) as day " +
                "FROM(SELECT duration, strftime('%Y-%m-%d', ddate / 1000, 'unixepoch', 'localtime') date, ddate " +
                "FROM pq_call_structure WHERE type = 2 and strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')) GROUP BY date";
        double[] callerData = new double[DateTime.now().dayOfMonth().getMaximumValue()];
        try {
            GenericRawResults<Object[]> rawResults = dao.queryRaw(callerSql, new DataType[]{DataType.DOUBLE, DataType.INTEGER});
            List<Object[]> list = rawResults.getResults();
            for (Object[] objs : list)
                callerData[(Integer) objs[1] - 1] = (Double) objs[0] / 60;
            rawResults.close();
        } catch (Exception e) {
            Log.e("voice", "查询通话主叫数据时出现异常", e);
        }
        // -- 当月每日通话数据 - 接听
        String answerSql = "SELECT sum(duration) as durations, CAST(strftime('%d', ddate / 1000, 'unixepoch', 'localtime') as integer) as day " +
                "FROM(SELECT duration, strftime('%Y-%m-%d', ddate / 1000, 'unixepoch', 'localtime') date, ddate " +
                "FROM pq_call_structure WHERE type = 1 and strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')) GROUP BY date";
        double[] answerData = new double[DateTime.now().dayOfMonth().getMaximumValue()];
        try {
            GenericRawResults<Object[]> rawResults = dao.queryRaw(answerSql, new DataType[]{DataType.DOUBLE, DataType.INTEGER});
            List<Object[]> list = rawResults.getResults();
            for (Object[] objs : list)
                answerData[((Integer) objs[1] - 1)] = (Double) objs[0] / 60;
            rawResults.close();
        } catch (Exception e) {
            Log.e("voice", "查询通话接听数据时出现异常", e);
        }

        values.add(callerData);
        values.add(answerData);
        usageBarChart = new BarChart("", new String[]{"主叫", "接听"}, values);
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

        NumberFormat valueFormat = new NumberFormat() {
            DecimalFormat format = new DecimalFormat("#.#");

            @Override
            public StringBuffer format(double v, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                if (v == 0)
                    return stringBuffer;
                return stringBuffer.append(format.format(v)).append("分");
            }

            @Override
            public StringBuffer format(long l, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                if (l == 0)
                    return stringBuffer;
                return stringBuffer.append(format.format(l)).append("分");
            }

            @Override
            public Number parse(String s, ParsePosition parsePosition) {
                return null;
            }
        };
        for (int i = 0; i < usageBarChart.getRenderer().getSeriesRendererCount(); i++)
            usageBarChart.getRenderer().getSeriesRendererAt(i).setChartValuesFormat(valueFormat);

        GraphicalView graphicalView = usageBarChart.execute(getActivity().getApplicationContext());
        graphicalView.setMinimumWidth(days * 60);
        voice_usage_layout.addView(graphicalView);

        // 主叫，接听，未接，拒接，主叫失败，接听失败
        String propSql = "SELECT" +
                "  (SELECT count(id) from pq_call_structure WHERE type = 2 AND strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')) as caller," +
                "  (SELECT count(id) from pq_call_structure WHERE type = 1 AND strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')) as answer," +
                "  (SELECT count(id) from pq_call_structure WHERE type = 3 AND strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')) as missed," +
                "  (SELECT count(id) from pq_call_structure WHERE type = 4 AND strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')) as reject," +
                "  (SELECT count(id) from pq_call_structure WHERE type = 2 AND CAUSE IN(" + argDot + ") AND strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')) as fcaller," +
                "  (SELECT count(id) from pq_call_structure WHERE type = 1 AND CAUSE IN(" + argDot + ") AND strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')) as fanswer," +
                "  (SELECT sum(duration) from pq_call_structure WHERE type = 2 AND strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')) as sumCaller," +
                "  (SELECT sum(duration) from pq_call_structure WHERE type = 1 AND strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')) as sumAnswer";
        long sumCaller = 0, sumAnswer = 0;
        List<String> causeArgs = new LinkedList<String>();
        causeArgs.addAll(Arrays.asList(causes));
        causeArgs.addAll(Arrays.asList(causes));

        List<Double> propValues = new LinkedList<Double>();
        propValues.add(0d);
        propValues.add(0d);
        List<Integer> colors = new LinkedList<Integer>();
        colors.add(getResources().getColor(R.color.blue));
        colors.add(getResources().getColor(R.color.forestgreen));
        List<String> titles = new LinkedList<String>();
        titles.add("主叫");
        titles.add("接听");
        try {
            GenericRawResults<Object[]> propRawResults = dao.queryRaw(propSql, new DataType[]{DataType.INTEGER, DataType.INTEGER,
                    DataType.INTEGER, DataType.INTEGER,
                    DataType.INTEGER, DataType.INTEGER, DataType.LONG, DataType.LONG}, causeArgs.toArray(new String[causeArgs.size()]));
            Object[] props = propRawResults.getFirstResult();
            propValues.set(0, Double.valueOf((Integer) props[0]));
            propValues.set(1, Double.valueOf((Integer) props[1]));
            int missed = (Integer) props[2];
            int reject = (Integer) props[3];
            int fcaller = (Integer) props[4];
            int fanswer = (Integer) props[5];
            sumCaller = (Long) props[6];
            sumAnswer = (Long) props[7];

            if (missed > 0) {
                titles.add("未接");
                propValues.add(Double.valueOf(missed));
                colors.add(getResources().getColor(R.color.color6));
            }
            if (reject > 0) {
                titles.add("拒接");
                propValues.add(Double.valueOf(reject));
                colors.add(getResources().getColor(R.color.color1));
            }
            if (fcaller > 0) {
                titles.add("呼叫失败");
                propValues.add(Double.valueOf(fcaller));
                colors.add(getResources().getColor(R.color.color7));
            }
            if (fanswer > 0) {
                titles.add("接听失败");
                propValues.add(Double.valueOf(fanswer));
                colors.add(getResources().getColor(R.color.color3));
            }
            propRawResults.close();
        } catch (Exception e) {
            Log.e("voice", "无法统计通话各类型总数", e);
        }
        DecimalFormat mFormat = new DecimalFormat("#.#");
        prop_call_timer.setText(mFormat.format((sumCaller + sumAnswer) / 60.0f) + "分钟");
        prop_caller.setText(mFormat.format(sumCaller / 60.0f) + "分钟");
        prop_answer.setText(mFormat.format(sumAnswer / 60.0f) + "分钟");

        double[] dvalues = new double[propValues.size()];
        for (int i = 0; i < propValues.size(); i++)
            dvalues[i] = propValues.get(i).doubleValue();
        propPieChart = new PieChart("", titles.toArray(new String[titles.size()]), dvalues);

        propPieChart.setShowLegend(true);
        propPieChart.setHighlighted(true);
        propPieChart.setValueUnit("条");
        int[] colorValues = new int[colors.size()];
        for (int i = 0; i < colors.size(); i++)
            colorValues[i] = colors.get(i).intValue();
        propPieChart.setColors(colorValues);
        propPieChart.init(getActivity().getApplicationContext());
        propPieChart.getRenderer().setStartAngle(280);
        propPieChart.getRenderer().setDisplayValues(true);
        propPieChart.getRenderer().setShowLabels(true);
        if (Numbers.sum(dvalues) <= 0)
            voice_prop_layout.addView(getActivity().getLayoutInflater().inflate(R.layout.no_data_mask, null));
        else
            voice_prop_layout.addView(propPieChart.execute(getActivity().getApplicationContext()));

        chartScrollView.post(new Runnable() {
            @Override
            public void run() {
                chartScrollView.smoothScrollTo((DateTime.now().getDayOfMonth() - 1) * 60, 0);
            }
        });
    }
}
