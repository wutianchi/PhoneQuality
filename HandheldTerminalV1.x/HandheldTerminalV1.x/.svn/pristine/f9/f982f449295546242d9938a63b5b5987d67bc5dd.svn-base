package com.cattsoft.phone.quality.service;

import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.model.NetworkActivity;
import com.cattsoft.phone.quality.service.runnable.NetSpeedRunnable;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.cattsoft.phone.quality.utils.Traffics;
import com.google.inject.Inject;
import org.joda.time.DateTime;
import roboguice.service.RoboService;

/**
 * 统计网络使用状况数据。
 * 统计网络开启时长，数据流量消耗，网络速率统计
 * Created by Xiaohong on 2014/5/2.
 */
public class NetStateService extends RoboService {
    private static final String TAG = "netState";

    @Inject
    private TelephonyManager telephonyManager;

    /** 当前数据网络类型 wifi、3g、2g */
    private MobileNetType.NetWorkType netType = MobileNetType.NetWorkType.TYPE_INVALID;
    /** 当前移动网络类型 GPRS、HSDPA */
    private int mobileType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
    /** 当前手机制式类型 GSM、CDMA */
    private int phoneType = TelephonyManager.PHONE_TYPE_NONE;
    /** 网络活动统计 */
    private NetworkActivity networkActivity;
    /** 网络速率计算 */
    private NetSpeedRunnable speedRunnable;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        speedRunnable = new NetSpeedRunnable(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MobileNetType.NetWorkType netWorkType = MobileNetType.getNetWorkType(getApplicationContext());
        if (MobileNetType.NetWorkType.TYPE_INVALID == netWorkType) {
            stopSelf();
        } else {
            activate();
        }
        return START_NOT_STICKY;
    }

    /**
     * 网络激活
     */
    private void activate() {
        MobileNetType.NetWorkType aType = MobileNetType.getNetWorkType(this);
        int mType = MobileNetType.getMobileTypeValue(this);
        int mPhone = telephonyManager.getPhoneType();

        if (aType == MobileNetType.NetWorkType.TYPE_INVALID) {
            // 无网络
            deactivate();
        } else if (aType != netType) {
            // 网络类型变化，从WiFi 到 3G 或 3G 到 2G 切换
            if (netType != MobileNetType.NetWorkType.TYPE_INVALID)
                deactivate();
            speedRunnable.start();

            netType = aType;
            mobileType = mType;
            phoneType = mPhone;

            networkActivity = new NetworkActivity(phoneType, mobileType, netType.type, DateTime.now());
            networkActivity.setTraffic(Traffics.getNetBytes(netType, 0));

//            Log.d(TAG, "当前网络类型：" + netType.nickname + "，移动网络："
//                    + MobileNetType.PHONE_TYPE_MAP.get(mPhone) + "，" + MobileNetType.getMobileTypeName(this));
        }
    }

    /**
     * 网络关闭
     */
    private void deactivate() {
        speedRunnable.stop();
        if (netType != MobileNetType.NetWorkType.TYPE_INVALID) {
            networkActivity.setDeactivate(DateTime.now());
            networkActivity.setTraffic(Traffics.getNetBytes(netType, 0) - networkActivity.getTraffic());
            try {
                QualityApplication.getApplication(getApplicationContext()).getDatabaseHelper().getNetworkActivitieDAO().create(networkActivity);
            } catch (Exception e) {
                Log.w("TAG", "网络活动时长统计失败");
            }
        }
        networkActivity = null;
    }

    @Override
    public void onDestroy() {
        // 停止网络服务
        deactivate();
        super.onDestroy();
    }
}
