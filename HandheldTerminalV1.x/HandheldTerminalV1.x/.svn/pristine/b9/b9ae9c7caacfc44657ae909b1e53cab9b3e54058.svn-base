package com.cattsoft.phone.quality.ui.fragments.wifi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.ui.adapter.FragmentAdapter;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.google.inject.Inject;
import roboguice.inject.InjectView;
import roboguice.receiver.RoboBroadcastReceiver;

import java.util.concurrent.TimeUnit;

/**
 * Created by Xiaohong on 2014/5/15.
 */
public class WifiFragment extends RoboLazyFragment {
    public static final String WIFI_SCAN_INTERVAL_ACTION = "com.cattsoft.phone.quality.WIFI_SCAN_INTERVAL";

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    ViewPager pager;
    @Inject
    WifiManager wifiManager;
    private FragmentAdapter adapter = null;
    private WifiScanReceiver receiver;
    private ScanDelayedReceiver delayedReceiver;
    /*** 扫描间隔 */
    private int interval = 5;

    /**
     * WiFi 扫描
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            wifiManager.startScan();
            return true;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_viewpager, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new FragmentAdapter(getFragmentManager(),
                new String[]{"频谱图", "WiFi列表"},
                new Class[]{SpectralFragment.class,
                        ListFragment.class});
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        if (!wifiManager.isWifiEnabled()) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("WiFi关闭")
                    .setMessage("目前没有开启Wi-Fi，无法扫描热点列表")
//                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
//                        @Override
//                        public void onCancel(DialogInterface dialogInterface) {
//                        }
//                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
        }
        registerReceiver((delayedReceiver = new ScanDelayedReceiver()), new IntentFilter(WIFI_SCAN_INTERVAL_ACTION));
        registerReceiver((receiver = new WifiScanReceiver()), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();
    }

    @Override
    public void onDestroy() {
        adapter = null;

        super.onDestroy();
    }

    private class WifiScanReceiver extends RoboBroadcastReceiver {
        @Override
        protected void handleReceive(Context context, Intent intent) {
            super.handleReceive(context, intent);
            if (interval > 0)
                handler.sendEmptyMessageDelayed(1, TimeUnit.SECONDS.toMillis(5));
            else
                handler.sendEmptyMessage(1);
        }
    }

    private class ScanDelayedReceiver extends RoboBroadcastReceiver {
        @Override
        protected void handleReceive(Context context, Intent intent) {
            super.handleReceive(context, intent);
            interval = intent.getIntExtra("interval", 5);
            if (0 == interval)
                handler.obtainMessage(1).sendToTarget();
        }
    }
}