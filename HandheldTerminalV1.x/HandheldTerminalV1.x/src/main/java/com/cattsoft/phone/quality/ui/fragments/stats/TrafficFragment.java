package com.cattsoft.phone.quality.ui.fragments.stats;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.cattsoft.commons.digest.StringUtils;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.AppTraffic;
import com.cattsoft.phone.quality.model.MetaData;
import com.cattsoft.phone.quality.model.NetworkActivity;
import com.cattsoft.phone.quality.model.SystemTraffic;
import com.cattsoft.phone.quality.ui.adapter.AppTrafficAdapter;
import com.cattsoft.phone.quality.ui.adapter.NetworkActivityAdapter;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.cattsoft.phone.quality.ui.widget.DismissablePopupWindow;
import com.cattsoft.phone.quality.ui.widget.ProgressWheel;
import com.cattsoft.phone.quality.ui.widget.chart.BarChart;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.google.inject.Inject;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DataType;
import org.achartengine.GraphicalView;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import roboguice.inject.InjectView;
import roboguice.receiver.RoboBroadcastReceiver;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaohong on 2014/5/15.
 */
public class TrafficFragment extends RoboLazyFragment {
    @InjectView(R.id.rootView)
    LinearLayout rootView;
    @InjectView(R.id.traffic_charts)
    FrameLayout traffic_charts_layout;
    @InjectView(R.id.gprs_used_for_month)
    TextView traffic_used_month;
    @InjectView(R.id.gprs_retail_for_month)
    TextView retail_for_month;
    @InjectView(R.id.tips_not_currect_text)
    TextView tips_not_currect;
    @InjectView(R.id.traffic_net_type)
    TextView traffic_net_type;
    @InjectView(R.id.traffic_net_status)
    TextView traffic_net_status;
    @InjectView(R.id.traffic_speed)
    TextView traffic_speed;
    @InjectView(R.id.traffic_progress)
    ProgressWheel traffic_progress;
    @InjectView(R.id.gprs_percent_image_middle)
    ImageView percent_image_middle;
    @InjectView(R.id.srollGPRSView)
    HorizontalScrollView chartScrollView;

    @InjectView(R.id.traffic_progress_layout)
    View progress_layout;
    @InjectView(R.id.traffic_detail_layout)
    View detail_layout;
    @InjectView(R.id.traffic_apps_layout)
    View apps_traffic_layout;
    BarChart trafficBarChart;
    @Inject
    WifiManager wifiManager;
    @Inject
    LayoutInflater layoutInflater;
    private SpeedReceiver speedReceiver;
    private ListView activitys_list;
    private ArrayAdapter<NetworkActivity> activityAdapter;
    private ListView apps_list;
    private ArrayAdapter<AppTraffic> appAdapter;
    private double trafficUsedMbs = 0;
    private double reulateUsedMbs = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_stats_traffic, container, false);
    }

    @Override
    protected void doStuffInBackground(Application app) {
        super.doStuffInBackground(app);
        View.OnClickListener detail_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trafficList();
            }
        };
        progress_layout.setOnClickListener(detail_listener);
        detail_layout.setOnClickListener(detail_listener);
        apps_traffic_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trafficApps();
            }
        });

        tips_not_currect.setClickable(true);
        tips_not_currect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DismissablePopupWindow popupWindow = new DismissablePopupWindow(getActivity().getApplicationContext(), R.layout.layout_traffic_popup);
                ((EditText) popupWindow.getContentView().findViewById(R.id.et_used)).setText(new DecimalFormat("#.##").format(reulateUsedMbs));
                ((EditText) popupWindow.getContentView().findViewById(R.id.et_pack)).setText(
                        getDatabaseHelper().getString("month_retail_traffic_pack", "100"));
                (popupWindow.getContentView().findViewById(R.id.dismiss)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String val = ((EditText) popupWindow.getContentView().findViewById(R.id.et_used)).getText().toString();
                        if (StringUtils.isNotEmpty(val)) {
                            // 本月校正流量
                            double regulate = Double.parseDouble(((EditText) popupWindow.getContentView().findViewById(R.id.et_used)).getText().toString()) - trafficUsedMbs;
                            getDatabaseHelper().getMetaDataDAO().createOrUpdate(new MetaData("month_regulate_traffic_" + DateTime.now().toString("yyyyMM"), Double.toString(regulate)));
                            getDatabaseHelper().getMetaDataDAO().createOrUpdate(new MetaData("month_retail_traffic_pack", ((EditText) popupWindow.getContentView().findViewById(R.id.et_pack)).getText().toString()));
                            loadUsage();
                        }
                        popupWindow.dismiss();
                    }
                });
                popupWindow.show(rootView, 0, 100);
            }
        });
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
        speedReceiver = new SpeedReceiver();
        // showPopup(rootView);
        RuntimeExceptionDao<SystemTraffic, Long> dao = getDatabaseHelper().getSystemTrafficDAO();
        loadUsage();

        List<double[]> values = new ArrayList<double[]>();
        // 统计每天的使用量
        double[] data = new double[DateTime.now().dayOfMonth().getMaximumValue()];
        try {
            GenericRawResults<Object[]> rawResults = dao.queryRaw("SELECT CAST(strftime('%d', ddate / 1000, 'unixepoch', 'localtime') AS INTEGER), (sum(mobileRxBytes) + sum(mobileTxBytes)) used " +
                    "FROM pq_system_traffic WHERE strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime') GROUP BY ddate", new DataType[]{DataType.INTEGER, DataType.DOUBLE});
            List<Object[]> rawList = rawResults.getResults();
            for (Object[] result : rawList)
                data[(Integer) result[0] - 1] = ((Double) result[1] / 1024.0d / 1024.0d);
            rawResults.close();
        } catch (Exception e) {
            Log.e("traffic", "无法查询每日流量", e);
        }
        values.add(data);
        trafficBarChart = new BarChart("", new String[]{"Mobile"}, values);
        trafficBarChart.setyTitle("");
        trafficBarChart.setxTitle("");
        trafficBarChart.setColors(new int[]{getResources().getColor(R.color.blue)
        });
        int today = DateTime.now().getDayOfMonth();
        int days = DateTime.now().dayOfMonth().getMaximumValue();

        trafficBarChart.init(getActivity().getApplicationContext());

        trafficBarChart.getRenderer().setYLabels(0);
        trafficBarChart.getRenderer().setXLabels(0);
        trafficBarChart.getRenderer().setShowLegend(true);
        NumberFormat valueFormat = new NumberFormat() {
            DecimalFormat format = new DecimalFormat("#.##");

            @Override
            public StringBuffer format(double v, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                return stringBuffer.append(format.format(v)).append(" MB");
            }

            @Override
            public StringBuffer format(long l, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                return stringBuffer.append(format.format(l)).append(" MB");
            }

            @Override
            public Number parse(String s, ParsePosition parsePosition) {
                return null;
            }
        };
        for (int i = 0; i < trafficBarChart.getRenderer().getSeriesRendererCount(); i++)
            trafficBarChart.getRenderer().getSeriesRendererAt(i).setChartValuesFormat(valueFormat);
        for (int i = 0; i <= days; i++)
            trafficBarChart.getRenderer().addXTextLabel(i, (i > 0 ? (i == today ? "今天" : "" + i) : ""));
        trafficBarChart.getRenderer().setXRoundedLabels(true);

        GraphicalView graphicalView = trafficBarChart.execute(getActivity().getApplicationContext());
        graphicalView.setMinimumWidth(days * 60);
        traffic_charts_layout.removeAllViews();
        traffic_charts_layout.addView(graphicalView);

        chartScrollView.post(new Runnable() {
            @Override
            public void run() {
                chartScrollView.smoothScrollTo((DateTime.now().getDayOfMonth() - 1) * 60, 0);
            }
        });

//        getActivity().registerReceiver(speedReceiver, new IntentFilter(NetStatsRunnable.NET_SPEED_ACTION));
    }

    private void loadUsage() {
        RuntimeExceptionDao<SystemTraffic, Long> dao = getDatabaseHelper().getSystemTrafficDAO();
        reulateUsedMbs = trafficUsedMbs;
        try {
            long used = dao.queryRawValue("SELECT (sum(mobileRxBytes) + sum(mobileTxBytes)) used " +
                    "FROM pq_system_traffic WHERE strftime('%Y-%m', ddate / 1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')");
            trafficUsedMbs = used / 1024d / 1024d;
            if (trafficUsedMbs < 0) trafficUsedMbs = 0;
            reulateUsedMbs = trafficUsedMbs + getDatabaseHelper().getInt("month_regulate_traffic_" + DateTime.now().toString("yyyyMM"), 0);
            traffic_used_month.setText(getString(R.string.traffic_used_month, new DecimalFormat("#.##").format(reulateUsedMbs)));
        } catch (Exception e) {
            Log.e("traffic", "无法统计流量使用总量");
        }
        long month_retail = getDatabaseHelper().getInt("month_retail_traffic_pack", 100);
        int d = Long.valueOf(Math.round(360d / month_retail * reulateUsedMbs)).intValue();
        if (d > 360) d = 360;
        if (d > 270) {
            percent_image_middle.setImageResource(R.drawable.content_top_bg_03_flow_03);
            traffic_progress.setBarColor(getResources().getColor(R.color.traffic_bar_color_red));
        } else if (d > 180) {
            percent_image_middle.setImageResource(R.drawable.content_top_bg_03_usage_04);
            traffic_progress.setBarColor(getResources().getColor(R.color.traffic_bar_color_yellow));
        } else if (d > 0) {
            percent_image_middle.setImageResource(R.drawable.content_top_bg_03_usage_02);
            traffic_progress.setBarColor(getResources().getColor(R.color.traffic_bar_color_blue));
        } else {
            percent_image_middle.setImageResource(R.drawable.content_top_bg_03_flow_01);
            traffic_progress.setBarColor(getResources().getColor(R.color.traffic_rim_color));
        }
        traffic_progress.setupPaints();
        traffic_progress.setProgress(d);
        retail_for_month.setText(getString(R.string.traffic_used_month, Long.toString(month_retail)));
    }

    private boolean trafficList() {
        String dayOfSql = "SELECT distinct strftime('%Y-%m-%d', deactivate / 1000, 'unixepoch', 'localtime') as ddate " +
                "  FROM pq_network_activity " +
                "  WHERE strftime('%Y-%m', deactivate / 1000, 'unixepoch', 'localtime') == strftime('%Y-%m', 'now', 'localtime') order by activate desc";
        RuntimeExceptionDao<NetworkActivity, Long> dao = getDatabaseHelper().getNetworkActivitieDAO();
        String[] days = new String[]{};
        try {
            GenericRawResults<Object[]> rawResults = dao.queryRaw(dayOfSql, new DataType[]{DataType.STRING});
            List<Object[]> result = rawResults.getResults();
            days = new String[result.size()];
            for (int i = 0; i < result.size(); i++)
                days[i] = (String) result.get(i)[0];
        } catch (Exception e) {

        }
        DismissablePopupWindow popupWindow = new DismissablePopupWindow(getActivity(), R.layout.layout_traffic_activity_popup);
        popupWindow.setOutsideTouchable(true);
        activityAdapter = new NetworkActivityAdapter(getActivity(), new ArrayList<NetworkActivity>());
        activitys_list = (ListView) popupWindow.getContentView().findViewById(R.id.activity_list);
        activitys_list.addHeaderView(layoutInflater.inflate(R.layout.network_activity_item_head, activitys_list, false), null, false);
        activitys_list.setAdapter(activityAdapter);
        ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, days);
        Spinner spinner = ((Spinner) popupWindow.getContentView().findViewById(R.id.month_spinner));
        spinner.setAdapter(_Adapter);
        if (days.length == 0) {
            // 添加提示信息，当前没有可进行显示的流量活动
            Toast.makeText(getActivity(), "当前没有流量记录", Toast.LENGTH_SHORT).show();
            return false;
        }
        spinner.setSelection(0, true);
        // 查询活动
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadActivity((String) parent.getAdapter().getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        popupWindow.show(rootView, 0, 0);
        loadActivity(days[0]);

        return true;
    }

    private void trafficApps() {
        DismissablePopupWindow popupWindow = new DismissablePopupWindow(getActivity(), R.layout.layout_traffic_detail_popup);
        popupWindow.setOutsideTouchable(true);
        RuntimeExceptionDao<AppTraffic, Long> dao = getDatabaseHelper().getAppTrafficDAO();
        List<AppTraffic> list = new ArrayList<AppTraffic>();
        try {
            list = dao.query(dao.queryBuilder().groupBy("uid").orderByRaw(" MOBILERXBYTES DESC , MOBILETXBYTES DESC, WIFIRXBYTES DESC, WIFITXBYTES DESC, SYSTEM ASC, UID ").prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        appAdapter = new AppTrafficAdapter(getActivity(), list);
        apps_list = (ListView) popupWindow.getContentView().findViewById(R.id.traffic_list);
        apps_list.addHeaderView(layoutInflater.inflate(R.layout.traffic_app_item_head, apps_list, false), null, false);
        apps_list.setAdapter(appAdapter);
        popupWindow.show(rootView, 0, 0);
    }

    private void loadActivity(String date) {
        try {
            if (null == activitys_list)
                return;
            RuntimeExceptionDao<NetworkActivity, Long> dao = getDatabaseHelper().getNetworkActivitieDAO();

            List<NetworkActivity> activities = dao.query(dao.queryBuilder().orderBy("deactivate", false).where()
                    .ge("deactivate", DateTime.parse(date))
                    .and().lt("deactivate", DateTime.parse(DateTime.parse(date).plusDays(1).toString(DateTimeFormat.shortDate()),
                            DateTimeFormat.shortDate())).prepare());
            activityAdapter.clear();
            activityAdapter.addAll(activities);
            activityAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SpeedReceiver extends RoboBroadcastReceiver {
        @Override
        protected void handleReceive(Context context, Intent intent) {
            super.handleReceive(context, intent);
            // 当前网络类型
            int type = intent.getIntExtra("type", MobileNetType.NetWorkType.TYPE_INVALID.type);
            MobileNetType.NetWorkType netWorkType = MobileNetType.NetWorkType.toType(type);
            if (type != MobileNetType.NetWorkType.TYPE_INVALID.type) {
                traffic_speed.setText(new DecimalFormat("0.0").format(intent.getFloatExtra("speed", 0)) + " kb/s");
                if (type == MobileNetType.NetWorkType.TYPE_WIFI.type) {
                    traffic_net_status.setText(wifiManager.getConnectionInfo().getSSID());
                    traffic_net_type.setText("Wi-Fi连接");
                } else {
                    traffic_net_type.setText(netWorkType.nickname + "网络");
                    traffic_net_status.setText(
                            MobileNetType.PHONE_NETWORK_TYPE_MAP.get(intent.getIntExtra("netType", TelephonyManager.NETWORK_TYPE_UNKNOWN)));
                }
            } else {
                traffic_speed.setText("-");
                traffic_net_type.setText("WiFi/Mobile连接");
                traffic_net_status.setText("无有效网络");
            }
        }
    }
}
