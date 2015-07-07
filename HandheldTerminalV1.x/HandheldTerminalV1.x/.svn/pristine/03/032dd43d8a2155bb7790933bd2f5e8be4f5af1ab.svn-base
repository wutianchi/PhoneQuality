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
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.ui.fragments.RoboLazyFragment;
import com.google.inject.Inject;
import roboguice.inject.InjectView;
import roboguice.receiver.RoboBroadcastReceiver;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Xiaohong on 2014/5/15.
 */
public class IndicatorFragment extends RoboLazyFragment {
    @Inject
    WifiManager wifiManager;
    @InjectView(R.id.wifi_point)
    ImageView pointer;
    @InjectView(R.id.wifi_ssid)
    TextView ssid;
    // ------------
    private WiFiSignalReceiver receiver;
    private String bssid;
    private String mssid;

    private RotateAnimation rotateAnimation;
    private float degrees = 0;

    private boolean rotating = false;

    private WifiManager.WifiLock wifiLock;

    private ScanResult scanResult = null;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            handler.sendEmptyMessageDelayed(1, TimeUnit.SECONDS.toMillis(1));
            if (null != scanResult) {
                if (!rotating) {
                    toDegrees(scanResult.level + 1);
                    toDegrees(scanResult.level);
                }
            }
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_wifi_indicator_popup, container, false);
    }

    @Override
    protected void doResourceInBackground(Application app) {
        super.doResourceInBackground(app);
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "scan_indicator");
    }

    @Override
    protected void doWhenEverVisable(Application app) {
        super.doWhenEverVisable(app);
        if (null != wifiLock) {
            wifiLock.acquire();
        }
    }

    @Override
    protected void doWhenEverInVisable(Application app) {
        super.doWhenEverInVisable(app);
        if (wifiLock.isHeld())
            wifiLock.release();
    }

    @Override
    protected void doDataInBackground(Application app) {
        super.doDataInBackground(app);
        Intent intent = new Intent(WifiFragment.WIFI_SCAN_INTERVAL_ACTION);
        intent.putExtra("interval", 0);
        getActivity().sendBroadcast(intent);

        receiver = new WiFiSignalReceiver();
        if (null != bssid)
            this.ssid.setText(mssid + "(" + bssid + ")");
        registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        handler.sendEmptyMessageDelayed(1, TimeUnit.SECONDS.toMillis(1));
    }

    public void setBSSID(String ssid, String bssid) {
        this.bssid = bssid;
        this.mssid = ssid;

        if (null != ssid)
            this.ssid.setText(ssid + "(" + bssid + ")");
        if (null != pointer)
            toDegrees(-100);
    }

    private void toDegrees(float angle) {
        if (rotating)
            return;
        rotating = true;
        // 旋转角度

        float d = Math.abs(angle + 100);

        if (d <= 40) {
            d = (180 / 40f) * d;
        } else if (d <= 50) {
            d = (30 / 10f) * d + 60;
        } else if (d <= 60) {
            d = (22 / 10f) * d + 100;
        } else if (d <= 70) {
            d = (22 / 10f) * d + 100;
        }

        rotateAnimation = new RotateAnimation(degrees, d, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rotating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        float temp = d - degrees;
        if (temp < 0)
            temp = 100 - temp;
        degrees = d;

        rotateAnimation.setDuration((long) ((temp / 360) * 500)); // 持续时间
        pointer.startAnimation(rotateAnimation);
    }

    private class WiFiSignalReceiver extends RoboBroadcastReceiver {
        @Override
        protected void handleReceive(Context context, Intent intent) {
            super.handleReceive(context, intent);
            if (!getUserVisibleHint())
                return;
            List<ScanResult> resultList = wifiManager.getScanResults();
            for (ScanResult result : resultList) {
                if (result.BSSID.equals(bssid)) {
                    scanResult = result;
                    toDegrees(scanResult.level);
                    break;
                }
            }
        }
    }
}
