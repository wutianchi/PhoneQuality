package com.cattsoft.phone.quality.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.*;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.AppTraffic;
import com.cattsoft.phone.quality.service.aidl.IPhoneStateService;
import com.cattsoft.phone.quality.service.handler.PhoneStateHandler;
import com.cattsoft.phone.quality.service.observer.CallsContentObserver;
import com.cattsoft.phone.quality.service.observer.SmsContentObserver;
import com.cattsoft.phone.quality.service.receiver.NetActivityReceiver;
import com.cattsoft.phone.quality.service.receiver.ReportReceiver;
import com.cattsoft.phone.quality.utils.Connectivity;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.cattsoft.phone.quality.utils.Signal;
import com.google.inject.Inject;
import com.j256.ormlite.stmt.UpdateBuilder;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import roboguice.inject.InjectResource;
import roboguice.service.RoboService;

import java.util.concurrent.TimeUnit;

/**
 * 手机状态服务.
 * 核心服务.
 * Created by Xiaohong on 2014/5/1.
 */
public class PhoneStateService extends RoboService {
    public static final int OBSERVER_INSTALL = 201;
    public static final int OBSERVER_INSTALL_SMS = 202;
    public static final int OBSERVER_INSTALL_CALLS = 203;
    public static final String SERVICE_BINDER_ACTION = "com.cattsoft.phone.quality.CORE_SERVICE";
    public static final String CORE_SERVICE_STARTED_ACTION = "com.cattsoft.phone.service.started";
    private static final String TAG = "state";
    @InjectResource(R.integer.report_sms_interval)
    int SMS_INTERVAL;
    @InjectResource(R.integer.report_call_interval)
    int DROP_CALL_INTERVAL;
    @InjectResource(R.integer.report_report_interval)
    int REPORT_INTERVAL;
    //    private PhoneStateBinder binder;
    private IPhoneStateService.Stub binderStub;
    private PhoneStateHandler stateHandler;
    private PendingIntent dropCallPending, smsPending, reportPending, trafficPending;
    private ContentObserver smsObserver, callsObserver;
    @Inject
    private TelephonyManager telephonyManager;
    @Inject
    private ConnectivityManager connectivityManager;
    @Inject
    private WifiManager wifiManager;
    @Inject
    private AlarmManager alarmManager;
    @Inject
    private SharedPreferences sharedPreferences;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case OBSERVER_INSTALL:
                    installObserver(msg.arg1);
                    break;
            }
            return false;
        }
    });

    public Handler handler() {
        return handler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        binder = new PhoneStateBinder();
        binderStub = new PhoneStateStub();
        if (null == stateHandler) {
            stateHandler = new PhoneStateHandler(getApplicationContext());
            installListener();
        }
        Intent dataTunnel = new Intent(getApplicationContext(), DataTunnelService.class);
        dataTunnel.putExtra("flag", 0);
        startService(dataTunnel);
        installAlarm();
        NetworkInfo activiteNetwork = connectivityManager.getActiveNetworkInfo();
        sharedPreferences.edit().putInt(NetActivityReceiver.DISCONNECTED_NETWORK_TYPE, null != activiteNetwork ? activiteNetwork.getType() : Connectivity.TYPE_NONE).apply();
//        sendBroadcast(new Intent(CORE_SERVICE_STARTED_ACTION));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (0 == intent.getIntExtra("flag", 1))
                boot();
        } catch (Exception e) {
        }
        return START_STICKY;
    }

    private void installListener() {
        // 设置监听
        try {
            telephonyManager.listen(stateHandler,
                    PhoneStateListener.LISTEN_SERVICE_STATE |   // 监听服务状态
                            PhoneStateListener.LISTEN_CALL_STATE |      // 监听通话状态
                            PhoneStateListener.LISTEN_CELL_LOCATION |   // 监听基站单元改变
                            PhoneStateListener.LISTEN_CELL_INFO |   // 基站信息
                            PhoneStateListener.LISTEN_SIGNAL_STRENGTHS |    //  监听信号
                            PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |   //  监听数据连接状态(峰窝)
                            PhoneStateListener.LISTEN_DATA_ACTIVITY);   //  监听数据流量方向
        } catch (Exception e) {
            Log.d(TAG, "无法设置状态监听,需要检查权限申请设置", e);
        }
    }

    private void installObserver(int what) {
        if (what == OBSERVER_INSTALL_SMS && null == smsObserver) {
            getContentResolver().registerContentObserver(SmsContentObserver.SMS_URI, true, (smsObserver = new SmsContentObserver(getApplicationContext(), null)));
        } else if (what == OBSERVER_INSTALL_CALLS && null == callsObserver) {
            getContentResolver().registerContentObserver(CallsContentObserver.CALLS_URI, true, (callsObserver = new CallsContentObserver(getApplicationContext(), null)));
        }
    }

    private void installAlarm() {
        // 掉话率统计
        dropCallPending = PendingIntent.getBroadcast(getApplicationContext(), ReportReceiver.REQUEST_CODE_CALL, new Intent(ReportReceiver.REPORT_CALL_ACTION),
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, DateTime.now().plusMinutes(DROP_CALL_INTERVAL).getMillis(), TimeUnit.MINUTES.toMillis(DROP_CALL_INTERVAL), dropCallPending);
        // 短信失败率
        smsPending = PendingIntent.getBroadcast(getApplicationContext(), ReportReceiver.REQUEST_CODE_SMS, new Intent(ReportReceiver.REPORT_SMS_ACTION),
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, DateTime.now().plusMinutes(SMS_INTERVAL).getMillis(), TimeUnit.MINUTES.toMillis(SMS_INTERVAL), smsPending);
        // 报文上传
        reportPending = PendingIntent.getBroadcast(getApplicationContext(), ReportReceiver.REQUEST_CODE_REPORT, new Intent(ReportReceiver.REPORT_ACTION),
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, DateTime.now().plusMinutes(REPORT_INTERVAL).getMillis(),
                TimeUnit.MINUTES.toMillis(REPORT_INTERVAL), reportPending);
        // 流量统计
        trafficPending = PendingIntent.getService(getApplicationContext(), TrafficStatsService.REQUEST_CODE, new Intent(getApplicationContext(), TrafficStatsService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 流量统计触发时间, 0点前
        DateTime trafficTrigger = DateTime.now().plusDays(1)
                .withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
                .minusSeconds(1);
        // 触发间隔时长，每天0点前触发，毫秒数
        long trafficInterval = new Duration(trafficTrigger, DateTime.now().plusDays(2)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0)
                .minusSeconds(1)).getMillis();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, trafficTrigger.getMillis(), trafficInterval, trafficPending);
        // 通知数据上传
        if (MobileNetType.NetWorkType.TYPE_WIFI == MobileNetType.getNetWorkType(this))
            sendBroadcast(new Intent(ReportReceiver.REPORT_UPLOAD_ACTION));
        // Wifi扫描
        if (wifiManager.isWifiEnabled())
            startService(new Intent(getApplicationContext(), WifiScanService.class));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binderStub;
    }

    public PhoneStateHandler getStateHandler() {
        return stateHandler;
    }

    @Override
    public void onDestroy() {
        if (null != dropCallPending)
            alarmManager.cancel(dropCallPending);
        if (null != smsPending)
            alarmManager.cancel(smsPending);
        if (null != reportPending)
            alarmManager.cancel(reportPending);
        if (null != trafficPending)
            alarmManager.cancel(trafficPending);
        if (null != smsObserver)
            getContentResolver().unregisterContentObserver(smsObserver);
        if (null != callsObserver)
            getContentResolver().unregisterContentObserver(callsObserver);
        super.onDestroy();
    }

    private void boot() {
        Log.d(TAG, "system boot complete");
        try {
            sharedPreferences.edit()
                    .putLong("mobileRxBytes", TrafficStats.getMobileRxBytes())
                    .putLong("mobileTxBytes", TrafficStats.getMobileTxBytes())
                    .putLong("wifiRxBytes", TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes())
                    .putLong("wifiTxBytes", TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes())
                    .commit();
        } catch (Exception e) {
            Log.w(TAG, "系统流量初始化失败", e);
        }
        try {
            UpdateBuilder<AppTraffic, Long> updateBuilder = QualityApplication.getApplication(this).getDatabaseHelper().getAppTrafficDAO().updateBuilder();
            updateBuilder.updateColumnValue("rxBytes", 0);
            updateBuilder.updateColumnValue("txBytes", 0);
            int updated = updateBuilder.update();
            Log.d(TAG, "app traffic previous bytes updated:" + updated);
        } catch (Exception e) {
            Log.w(TAG, "应用流量初始化失败", e);
        }
    }

//    public class PhoneStateBinder extends Binder {
//        public PhoneStateService getService() {
//            return PhoneStateService.this;
//        }
//    }

    public class PhoneStateStub extends IPhoneStateService.Stub {
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            try {
                if (getCallingUid() != getPackageManager().getApplicationInfo(getPackageName(), 0).uid)
                    return false;
            } catch (Exception e) {
                Log.e(TAG, "服务连接权限验证失败：" + getCallingUid());
            }
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public String getValue(String name) throws RemoteException {
            return null;
        }

        @Override
        public Bundle getBundle() throws RemoteException {
            return getStateHandler().getBundle();
        }

        @Override
        public ServiceState getServiceState() throws RemoteException {
            try {
                return getBundle().getParcelable("serviceState");
            } catch (Exception e) {
                Log.w(TAG, "无法获取服务状态数据");
            }
            return null;
        }

        @Override
        public Signal getSignal() throws RemoteException {
            try {
                return getBundle().getParcelable("signalStrength");
            } catch (Exception e) {
                Log.w(TAG, "无法获取信号数据");
            }
            return null;
        }

        @Override
        public void sendEmptyMessage(int flag) throws RemoteException {
            handler.sendEmptyMessage(flag);
        }

        @Override
        public void sendMessage(int what, int arg1) throws RemoteException {
            handler.obtainMessage(what, arg1, -1).sendToTarget();
        }
    }
}
