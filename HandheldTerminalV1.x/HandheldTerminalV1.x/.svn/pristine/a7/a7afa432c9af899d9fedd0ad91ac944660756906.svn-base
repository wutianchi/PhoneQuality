package com.cattsoft.phone.quality.ui.fragments.wifi;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.cattsoft.phone.quality.ui.widget.chart.WiFiCubicLineChart;
import com.google.inject.Inject;
import roboguice.inject.InjectView;
import roboguice.receiver.RoboBroadcastReceiver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Xiaohong on 2014/5/15.
 */
public class SpectralFragment extends RoboLazyFragment {
    @InjectView(R.id.rootView)
    FrameLayout rootView;
    @InjectView(R.id.spectralChart)
    LinearLayout spectral;
    @InjectView(R.id.progress_tip)
    RelativeLayout progress_tip;

    @Inject
    WifiManager wifiManager;
    WifiReceiver receiver;

    private WiFiCubicLineChart spectralChart;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            spectralChart.repaint();
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_wifi_spectral, container, false);
    }

    @Override
    protected void doStuffInBackground(Application app) {
        super.doStuffInBackground(app);
        spectralChart = new WiFiCubicLineChart(getResources().getIntArray(R.array.wifiChannels));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        spectral.addView(spectralChart.execute(getActivity()), layoutParams);

        receiver = new WifiReceiver();
        registerReceiver(receiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        spectralChart.addWiFiSeries(getActivity(), "", "", 14, 0);
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
        Intent intent = new Intent(WifiFragment.WIFI_SCAN_INTERVAL_ACTION);
        intent.putExtra("interval", 5);
        getActivity().sendBroadcast(intent);
    }

    class WifiReceiver extends RoboBroadcastReceiver {
        @Override
        protected void handleReceive(Context context, Intent intent) {
            super.handleReceive(context, intent);
            getApplication().runInUi(new QualityApplication.Callback() {
                @Override
                public void call() {
                    if (!getUserVisibleHint())
                        return;
                    Set<String> keys = new HashSet<String>();

                    List<ScanResult> results = wifiManager.getScanResults();
                    for (ScanResult sr : results) {
                        spectralChart.addWiFiSeries(getActivity(), sr);
                        keys.add(sr.SSID);
                    }
                    // 删除没有的节点
                    spectralChart.removeNoSignal(keys);

                    handler.obtainMessage().sendToTarget();

                    progress_tip.setVisibility(View.GONE);
                }
            }, getActivity());
        }
    }
}
