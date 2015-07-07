package com.cattsoft.phone.quality.ui.fragments.signal;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.service.handler.PhoneStateHandler;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.cattsoft.phone.quality.ui.widget.chart.LineChart;
import com.cattsoft.phone.quality.utils.Connectivity;
import com.cattsoft.phone.quality.utils.Devices;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.cattsoft.phone.quality.utils.Signal;
import com.google.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.Period;
import roboguice.inject.InjectView;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 实时信号强度.
 * Created by Xiaohong on 2014/5/12.
 */
public class RealtimeFragment extends RoboLazyFragment {
    //    @InjectView(R.id.scrollView) OverScrollView scrollView;
    @InjectView(R.id.rootView)
    LinearLayout rootView;
    @InjectView(R.id.chartLayout)
    LinearLayout chartLayout;
    @Inject
    TelephonyManager telephonyManager;
    @InjectView(R.id.operator)
    TextView operator;
    @InjectView(R.id.deviceModel)
    TextView model;
    @InjectView(R.id.networkType)
    TextView networkType;
    @InjectView(R.id.networkState)
    TextView networkState;
    @InjectView(R.id.externalIP)
    TextView externalIP;
    private LineChart signalChart;
    private DateTime update;

    private BroadcastReceiver signalReceiver = null, serviceReceiver = null;
    private Handler refreshHandler = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_signal_rt, container, false);
    }

    @Override
    protected void doResourceInBackground(Application app) {
        super.doResourceInBackground(app);
        String[] titles = new String[]{"信号强度(dBm)", "发射功率(mW)"};
        List<double[]> values = new ArrayList<double[]>();
        values.add(new double[]{});
        values.add(new double[]{});

        signalChart = new LineChart(titles, values);
        signalChart.setColors(new int[]{getResources().getColor(R.color.blue), getResources().getColor(R.color.forestgreen)});
        signalChart.init(getActivity());
        signalChart.getRenderer().setXAxisMax(10.5);
        signalChart.getRenderer().setYAxisMin(-1);
        signalChart.getRenderer().setYAxisMax(34);
        signalChart.getRenderer().setXAxisColor(getResources().getColor(R.color.pager_background_alternate));
        signalChart.getRenderer().setShowGridX(false);
        signalChart.getRenderer().addYTextLabel(2, "差");
        signalChart.getRenderer().addYTextLabel(4, "微弱");
        signalChart.getRenderer().addYTextLabel(5, "弱");
        signalChart.getRenderer().addYTextLabel(8, "好");
        signalChart.getRenderer().addYTextLabel(12, "强");
        signalChart.getRenderer().setGridColor(getResources().getColor(R.color.pager_background));
        signalChart.getRenderer().setShowCustomTextGridY(true);
        signalChart.getRenderer().setLabelsTextSize(1f);
        signalChart.getRenderer().setXLabelsColor(signalChart.getRenderer().getBackgroundColor());
        signalChart.setMaxChartValues(10);
        signalChart.getRenderer().getSeriesRendererAt(0).setChartValuesFormat(new NumberFormat() {
            @Override
            public StringBuffer format(double v, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                return stringBuffer.append(v == -1 ? "-" : (new Double(-113 + 2 * v).intValue()));
            }

            @Override
            public StringBuffer format(long l, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                return stringBuffer.append(l == -1 ? "-" : (-113 + 2 * l));
            }

            @Override
            public Number parse(String s, ParsePosition parsePosition) {
                return null;
            }
        });
        signalReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Signal signal = intent.getParcelableExtra("signalStrength");
                setSignal(signal);
            }
        };
        serviceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ServiceState serviceState = intent.getParcelableExtra("serviceState");
                setServiceState(serviceState);
            }
        };
    }

    @Override
    protected void doStuffInBackground(Application app) {
        super.doStuffInBackground(app);
        chartLayout.addView(signalChart.execute(getActivity()));
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
        if (null != refreshHandler)
            refreshHandler.removeCallbacksAndMessages(null);
        refreshHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                try {
                    Period period = new Period(update, DateTime.now());
                    if (period.getSeconds() >= 2) {
                        if (isResumed() && getUserVisibleHint()) {
                            if (null != signalChart && null != getBinder()) {
                                Signal signal = getBinder().getSignal();
                                setSignal(signal);
                            }
                            refreshHandler.sendEmptyMessageDelayed(0, TimeUnit.SECONDS.toMillis(2));
                        }
                    } else {
                        refreshHandler.sendEmptyMessageDelayed(0, TimeUnit.SECONDS.toMillis(period.getSeconds()));
                    }
//                    setServiceState(null);
                } catch (Exception e) {
                }
                return false;
            }
        });
        if (null != getBinder()) {
            try {
                Signal signal = getBinder().getSignal();
                setSignal(signal);
            } catch (Exception e) {
            }
            try {
                ServiceState serviceState = getBinder().getServiceState();
                setServiceState(serviceState);
            } catch (Exception e) {
            }
        }
        registerReceiver(signalReceiver, new IntentFilter(PhoneStateHandler.SIGNAL_ACTION));
        registerReceiver(serviceReceiver, new IntentFilter(PhoneStateHandler.SERVICE_STATE_ACTION));
        refreshHandler.sendEmptyMessageDelayed(0, TimeUnit.SECONDS.toMillis(2));
    }

    private void setSignal(Signal signal) {
        if (null != signal) {
            // 信号更新
            try {
                int mw = Math.abs(10 ^ ((-113 + 2 * signal.getmGsmSignalStrength()) / 10));
                signalChart.push(new double[]{(signal.getmGsmSignalStrength() > 31 ? -1 : signal.getmGsmSignalStrength()), mw});
                signalChart.repaint();
            } catch (Exception e) {
                Log.w("fragment", "目前无法刷新信号图表", e);
            }
            update = DateTime.now();
        }
    }

    private void setServiceState(ServiceState serviceState) {
        // 服务状态变更
        try {
            operator.setText(Devices.getOperator(getActivity()));
            if (Connectivity.isAirplaneModeOn(getActivity()))
                operator.setText("飞行模式");
            model.setText(Build.MODEL);
            MobileNetType.NetWorkType netType = MobileNetType.getNetWorkType(getActivity());
            networkType.setText((netType.isMobile() ? netType.nickname + "/" : "") + MobileNetType.getMobileTypeName(getActivity()));
            if (netType.isMobile()) {
                externalIP.setText(Devices.getExternalIP());
            } else {
                externalIP.setText("无法获取");
            }
            networkState.setText(telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED ? "数据已连接" : "已断开连接");
            if (null != serviceState) {
                if (serviceState.getState() == ServiceState.STATE_OUT_OF_SERVICE)
                    networkType.setText("无网络服务");
                else if (serviceState.getState() == ServiceState.STATE_EMERGENCY_ONLY)
                    networkType.setText("限紧急服务");
                else if (serviceState.getState() == ServiceState.STATE_POWER_OFF)
                    networkType.setText("POWER OFF");
            }
        } catch (Exception e) {
            Log.w("fragment", "服务状态获取失败", e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        refreshHandler.removeCallbacksAndMessages(null);
    }
}
