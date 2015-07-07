package com.cattsoft.phone.quality.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.model.AppTraffic;
import com.cattsoft.phone.quality.model.SystemTraffic;
import com.cattsoft.phone.quality.service.receiver.NetActivityReceiver;
import com.cattsoft.phone.quality.utils.Connectivity;
import com.cattsoft.phone.quality.utils.Numbers;
import com.google.inject.Inject;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import org.joda.time.DateTime;
import roboguice.service.RoboIntentService;

import java.util.ArrayList;
import java.util.List;

/**
 * 流量统计服务.
 * 处理流量统计耗时任务.
 * Created by Xiaohong on 2014/5/9.
 */
public class TrafficStatsService extends RoboIntentService {
    public static final int REQUEST_CODE = 300;
    public static final int REQUEST_CODE_LIST_APP = 301;
    public static final String APP_LISTED_KEY = "app_listed";
    private static final String TAG = "traffic";
    @Inject
    SharedPreferences sharedPreferences;
    /** 统计触发时间 */
    private DateTime ddate;

    private int netType = Connectivity.TYPE_NONE;

    public TrafficStatsService() {
        super("TrafficStatsService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ddate = DateTime.now();
    }

    /**
     * 系统流量使用状况统计
     */
    private void statsSystem() {
        long mobileRxBytes = TrafficStats.getMobileRxBytes() - sharedPreferences.getLong("mobileRxBytes", 0);
        long mobileTxBytes = TrafficStats.getMobileTxBytes() - sharedPreferences.getLong("mobileTxBytes", 0);
        long wifiRxBytes = TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes() - sharedPreferences.getLong("wifiRxBytes", 0);
        long wifiTxBytes = TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes() - sharedPreferences.getLong("wifiTxBytes", 0);

//        Log.d(TAG, "系统流量统计，移动网络：" + (mobileRxBytes + mobileTxBytes) + "，Wi-Fi网络：" + (wifiRxBytes + wifiTxBytes));
        try {
            RuntimeExceptionDao<SystemTraffic, Long> dao = QualityApplication.getApplication(this).getDatabaseHelper().getSystemTrafficDAO();
            DateTime dateTime = ddate.withHourOfDay(0)
                    .withMinuteOfHour(0)
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0);
            SystemTraffic traffic = dao.queryBuilder().where().eq("ddate", dateTime).queryForFirst();
            if (null == traffic) {
                dao.create(new SystemTraffic(mobileRxBytes, mobileTxBytes, wifiRxBytes, wifiTxBytes, dateTime));
            } else {
                traffic.setMobileRxBytes(traffic.getMobileRxBytes() + mobileRxBytes);
                traffic.setMobileTxBytes(traffic.getMobileTxBytes() + mobileTxBytes);
                traffic.setWifiRxBytes(traffic.getWifiRxBytes() + wifiRxBytes);
                traffic.setWifiTxBytes(traffic.getWifiTxBytes() + wifiTxBytes);
                dao.update(traffic);
            }
        } catch (Exception e) {
            Log.w(TAG, "系统流量使用状况统计失败", e);
        }
        sharedPreferences.edit()
                .putLong("mobileRxBytes", TrafficStats.getMobileRxBytes())
                .putLong("mobileTxBytes", TrafficStats.getMobileTxBytes())
                .putLong("wifiRxBytes", TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes())
                .putLong("wifiTxBytes", TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes())
                .commit();
    }

    private List<PackageInfo> listApps() {
        PackageManager packageManager = getPackageManager();
        // 是否统计系统应用流量
        boolean systems = sharedPreferences.getBoolean("system_app_traffic", false);
        // 获取统计应用列表
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.SIGNATURE_MATCH);
        if (null == packageInfoList) {
            Log.d(TAG, "无法获取当前已安装的应用列表");
            return null;
        }
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        RuntimeExceptionDao<AppTraffic, Long> dao = QualityApplication.getApplication(this).getDatabaseHelper().getAppTrafficDAO();
        for (PackageInfo packageInfo : packageInfoList) {
            try {
                ApplicationInfo appInfo = packageManager.getApplicationInfo(packageInfo.packageName, 0);
                Intent launchIntent = packageManager.getLaunchIntentForPackage(packageInfo.packageName);
                if (launchIntent != null && (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
                        || (systems && PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.INTERNET", packageInfo.packageName))) {
                    apps.add(packageInfo);
                    String apname = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                    String pkname = packageInfo.packageName;
                    AppTraffic traffic = new AppTraffic(appInfo.uid, apname, pkname,
                            Numbers.getNegativeZero(TrafficStats.getUidRxBytes(appInfo.uid)),
                            Numbers.getNegativeZero(TrafficStats.getUidTxBytes(appInfo.uid)), DateTime.now());
                    if (null == dao.queryBuilder().where().eq("packageName", pkname).queryForFirst())
                        dao.create(traffic);
                }
            } catch (Exception e) {
                Log.w(TAG, "无法获取当前应用信息", e);
            }
        }
        sharedPreferences.edit().putBoolean(APP_LISTED_KEY, true).commit();
        return apps;
    }

    /**
     * 应用程序流量使用状况
     */
    private void statsApps() {
        if (!sharedPreferences.getBoolean(APP_LISTED_KEY, false))
            listApps();
        List<AppTraffic> traffics = null;
        try {
            traffics = QualityApplication.getApplication(this).getDatabaseHelper().getAppTrafficDAO().queryForAll();
        } catch (Exception e) {
        }
        if (null == traffics)
            return;
        RuntimeExceptionDao<AppTraffic, Long> dao = QualityApplication.getApplication(this).getDatabaseHelper().getAppTrafficDAO();
        for (AppTraffic traffic : traffics) {
            // 统计应用流量
            long rxBytes = Numbers.getNegativeZero(TrafficStats.getUidRxBytes(traffic.getUid())) - traffic.getRxBytes();
            long txBytes = Numbers.getNegativeZero(TrafficStats.getUidTxBytes(traffic.getUid())) - traffic.getTxBytes();
            if (netType != Connectivity.TYPE_NONE) {
                traffic.setRxBytes(Numbers.getNegativeZero(TrafficStats.getUidRxBytes(traffic.getUid())));
                traffic.setTxBytes(Numbers.getNegativeZero(TrafficStats.getUidTxBytes(traffic.getUid())));
            }
            // 根据网络类型统计流量
            if (netType == ConnectivityManager.TYPE_MOBILE) {
                traffic.setMobileRxBytes(traffic.getMobileRxBytes() + rxBytes);
                traffic.setMobileTxBytes(traffic.getMobileTxBytes() + txBytes);
            } else if (netType == ConnectivityManager.TYPE_WIFI) {
                traffic.setWifiRxBytes(traffic.getWifiRxBytes() + rxBytes);
                traffic.setWifiTxBytes(traffic.getWifiTxBytes() + txBytes);
            }
            traffic.setDdate(DateTime.now());
            // 应用更新
            try {
                dao.update(traffic);
            } catch (Exception e) {
                Log.w(TAG, "应用流量未统计：" + traffic.getPackageName(), e);
            }
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (REQUEST_CODE_LIST_APP == intent.getIntExtra("flag", -1)) {
            // 获取应用信息
            listApps();
        } else {
            if (intent.getIntExtra("flag", -1) != -1)
                netType = intent.getIntExtra(NetActivityReceiver.DISCONNECTED_NETWORK_TYPE, Connectivity.TYPE_NONE);
            // 流量统计
            statsSystem();
            statsApps();
        }
    }
}
