package com.cattsoft.phone.quality.service;

import android.content.*;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.model.GeoLocation;
import com.cattsoft.phone.quality.model.WifiResult;
import com.google.inject.Inject;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import roboguice.service.RoboService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * WiFi热点扫描
 * Created by Xiaohong on 2014/5/7.
 */
public class WifiScanService extends RoboService {
    public static final String TAG = "wifi";
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    WifiManager wifiManager;
    WifiManager.WifiLock wifiLock;
    WifiReceiver receiver;
    private boolean instant = false;
    private int serviceTimeout = 60;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    List<ScanResult> results = (List<ScanResult>) msg.obj;
                    List<WifiResult> wifiResults = new ArrayList<WifiResult>();
                    for (ScanResult sr : results)
                        wifiResults.add(new WifiResult(sr));
                    GeoLocation location = null;
                    try {
                        RuntimeExceptionDao<GeoLocation, Long> geoDao = QualityApplication.getApplication(getApplicationContext()).getDatabaseHelper().getGeoLocationDAO();
                        location = geoDao.queryForFirst(geoDao.queryBuilder().orderBy("ddate", false).prepare());
                    } catch (Exception e) {
                        Log.w(TAG, "查询位置信息时出现异常", e);
                    }
                    RuntimeExceptionDao<WifiResult, Long> dao = QualityApplication.getApplication(getApplicationContext()).getDatabaseHelper().getWifiResultDAO();
                    for (WifiResult wifi : wifiResults) {
                        try {
                            WifiResult l = dao.queryForFirst(dao.queryBuilder().where().eq("bssid", wifi.getBssid()).prepare());
                            if (null == l) {
                                wifi.setLocation(location);
                                dao.create(wifi);
                            } else if (null != location) {
                                l.setLocation(location);
                                l.setLevel(wifi.getLevel());
                                l.setSsid(wifi.getSsid());
                                l.setCapabilities(wifi.getCapabilities());
                                l.setDdate(DateTime.now());
                                dao.update(l);
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "更新Wifi热点信息时出现异常", e);
                        }
                    }
                    stopSelf();
                    break;
                case 1:
                    if (!wifiLock.isHeld())
                        wifiLock.acquire();
                    wifiManager.startScan();
                    break;
                case 3:
                    // 定位失败的情况
                    handler.obtainMessage(1).sendToTarget();
                    break;
                default:
                    stopSelf();
                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
        instant = false;
        wifiLock = wifiManager.createWifiLock("Wifi-Scan");
        registerReceiver((receiver = new WifiReceiver()), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!instant) {
            Duration duration = new Duration(new DateTime(sharedPreferences.getLong(BaiduLocationService.LOCATED_TIME_KEY, DateTime.now().getMillis())),
                    DateTime.now());
            // 超过半小时发送定位请求
            if (duration.getStandardMinutes() >= 30) {
                startService(new Intent(getApplicationContext(), BaiduLocationService.class));
            } else {
                handler.obtainMessage(1).sendToTarget();
            }
            handler.sendEmptyMessageDelayed(2, TimeUnit.SECONDS.toMillis(serviceTimeout));
        }
        instant = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        try {
            if (wifiLock.isHeld())
                wifiLock.release();
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
        }
        try {
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            handler.obtainMessage(0, wifiManager.getScanResults()).sendToTarget();
        }
    }

    private class LocReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            handler.obtainMessage(1).sendToTarget();
            ;
        }
    }
}
