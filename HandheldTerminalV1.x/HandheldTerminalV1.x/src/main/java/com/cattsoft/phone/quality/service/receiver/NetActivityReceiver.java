package com.cattsoft.phone.quality.service.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.service.BaiduLocationService;
import com.cattsoft.phone.quality.service.NetStateService;
import com.cattsoft.phone.quality.service.TrafficStatsService;
import com.cattsoft.phone.quality.service.WifiScanService;
import com.cattsoft.phone.quality.task.DeviceAuthTask;
import com.cattsoft.phone.quality.utils.Connectivity;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import roboguice.receiver.RoboBroadcastReceiver;

/**
 * 网络连接改变广播接收.
 * Created by Xiaohong on 2014/5/1.
 */
public class NetActivityReceiver extends RoboBroadcastReceiver {
    public static final String NET_DISCONNECT_ACTION = "com.cattsoft.phone.NET_DISCONNECT_ACTION";
    public static final String NET_CHANGED_ACTION = "com.cattsoft.phone.NET_CHANGED_ACTION";
    public static final String DISCONNECTED_NETWORK_TYPE = "net.disconnect_type";
    private static final String TAG = "net_activity";
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    private WifiManager wifiManager;
    @Inject
    private ConnectivityManager cm;

    @Override
    protected void handleReceive(Context context, Intent intent) {
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            // Wifi 开启或关闭事件
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                // 扫描
                context.startService(new Intent(context.getApplicationContext(), WifiScanService.class));
            }
            return;
        }
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting()) {
            int netType = sharedPreferences.getInt(DISCONNECTED_NETWORK_TYPE, Connectivity.TYPE_NONE);
            Log.i(TAG, "No connection");
            // 发送网络断开广播通知
            context.sendBroadcast(new Intent(NET_DISCONNECT_ACTION));
            // 无网络时停止网络数据统计服务
            context.stopService(new Intent(context.getApplicationContext(), NetStateService.class));
            // 统计系统流量使用状况
            Intent trafficIntent = new Intent(context.getApplicationContext(), TrafficStatsService.class);
            trafficIntent.putExtra("flag", 1);
            trafficIntent.putExtra(DISCONNECTED_NETWORK_TYPE, netType);
            context.startService(trafficIntent);
            sharedPreferences.edit().remove(DISCONNECTED_NETWORK_TYPE).apply();
            Intent intent1 = new Intent(NET_CHANGED_ACTION);
            intent1.putExtra("netType", netType);
            context.sendBroadcast(intent1);
        } else {
            int netType = activeNetworkInfo.getType();
            if (netType == ConnectivityManager.TYPE_WIFI) {
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();

                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (null != wifiInfo && networkInfo.isAvailable() && state == NetworkInfo.State.CONNECTED) {
                        Log.i(TAG, "wifi connection");
                        sharedPreferences.edit().putInt(DISCONNECTED_NETWORK_TYPE, netType).commit();
                        // 发送数据上传请求广播
                        Intent uploadIntent = new Intent(ReportReceiver.REPORT_UPLOAD_ACTION);
                        uploadIntent.putExtra("flag", 0);
                        context.sendBroadcast(uploadIntent);
                        // WiFi 热点扫描
                        context.startService(new Intent(context.getApplicationContext(), WifiScanService.class));
                        activate(context, netType);
                    }
                }
            } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                Log.i(TAG, "GPRS/3G connection");
                sharedPreferences.edit().putInt(DISCONNECTED_NETWORK_TYPE, netType).commit();
                // 发送数据上传请求广播
                context.sendBroadcast(new Intent(ReportReceiver.REPORT_UPLOAD_ACTION));
                // Need to get differentiate between 3G/GPRS
                activate(context, netType);
            }
        }
    }

    private void activate(Context context, int netType) {
        Duration duration = new Duration(new DateTime(sharedPreferences.getLong(BaiduLocationService.LOCATED_TIME_KEY, DateTime.now().getMillis())),
                DateTime.now());
        // 超过半小时发送定位请求
        if (duration.getStandardMinutes() >= 30)
            context.startService(new Intent(context.getApplicationContext(), BaiduLocationService.class));
        // 启动网络数据统计服务
        context.startService(new Intent(context.getApplicationContext(), NetStateService.class));
        // 发送网络类型变更通知
        Intent intent = new Intent(NET_CHANGED_ACTION);
        intent.putExtra("netType", netType);
        context.sendBroadcast(intent);

        // 检查设备注册状态
        try {
            String token = QualityApplication.getApplication(context).getDatabaseHelper().getString("oauth_verifier", null);
            if (Strings.isNullOrEmpty(token))
                new DeviceAuthTask(context, null).execute();
        } catch (Throwable t) {
        }
    }
}

////获得网络连接服务
//ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
//String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
//boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
//
//NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
//
//if(currentNetworkInfo.isConnected()){
//        // 是否是连接到WiFi事件
//        if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
//        Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//        if(null != parcelableExtra) {
//        NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
//        NetworkInfo.ShowType state = networkInfo.getState();
//
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        if(null != wifiInfo && networkInfo.isAvailable() && state == NetworkInfo.ShowType.CONNECTED) {
//        Log.d(tag, "已连接到无线热点：" + wifiInfo.getSSID());
//        // 通知数据上传
//        }
//        }
//        }
//        Log.d(tag, "已连接到网络");
//        }else{
//        // 网络断开
//        Log.d(tag, "当前无网络连接");
//        }