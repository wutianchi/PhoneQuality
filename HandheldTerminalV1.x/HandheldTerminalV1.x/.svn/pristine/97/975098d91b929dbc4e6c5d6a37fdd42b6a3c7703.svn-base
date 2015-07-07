package com.cattsoft.phone.quality.service.handler;

import android.app.Application;
import android.content.Context;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.model.MobileServiceActivity;
import com.cattsoft.phone.quality.utils.Connectivity;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.google.inject.Inject;
import org.joda.time.DateTime;
import roboguice.RoboGuice;

/**
 * 网络服务状态变更统计.
 * 综合统计各网络制式持续时长。
 * 包括UMTS、HSDPA、GPRS，飞行模式，无服务等
 * Created by Xiaohong on 2014/5/8.
 */
public class ServiceStateHandler {
    public static final String TAG = "mobile";
    private Context context;

    @Inject
    private TelephonyManager telephonyManager;
    private MobileServiceActivity activity;

    private int mType = MobileNetType.NetWorkType.TYPE_NONE.type;

    public ServiceStateHandler(Context context) {
        this.context = context;
        RoboGuice.getBaseApplicationInjector((Application) context.getApplicationContext()).injectMembers(this);
    }

    public void handle(ServiceState serviceState) {
        int type = MobileNetType.getMobileTypeValue(context);
        // 飞行模式
        if (Connectivity.isAirplaneModeOn(context))
            type = MobileNetType.NetWorkType.TYPE_AIRPLANE.type;
        if (type != mType) {
            // 服务类型出现变更
            if (mType != MobileNetType.NetWorkType.TYPE_NONE.type)
                deactivate();
            mType = type;
            activate();
        }
    }

    /**
     * 网络激活
     */
    private void activate() {
        MobileNetType.NetWorkType nType = MobileNetType.getNetWorkType(context);
        int mType = MobileNetType.getMobileTypeValue(context);
        int mPhone = telephonyManager.getPhoneType();

        activity = new MobileServiceActivity(mPhone, mType, nType.type, DateTime.now());
        if (Connectivity.isAirplaneModeOn(context)) {
            // 设置飞行模式下的网络类型 -1
            activity.setPhoneType(MobileNetType.NetWorkType.TYPE_AIRPLANE.type);
            activity.setNetworkType(MobileNetType.NetWorkType.TYPE_AIRPLANE.type);
            activity.setMobileType(MobileNetType.NetWorkType.TYPE_AIRPLANE.type);
        }
    }

    /**
     * 网络关闭
     */
    private void deactivate() {
        activity.setDeactivate(DateTime.now());
        try {
            QualityApplication.getApplication(context).getDatabaseHelper().getMobileServiceActivitieDAO().create(activity);
        } catch (Exception e) {
            Log.e(TAG, "当前网络持续时长统计失败", e);
        }
//        Log.d(TAG, "网络服务类型切换或断开：" + MobileNetType.PHONE_NETWORK_TYPE_MAP.get(activity.getMobileType()) +
//                "，持续时长：" + TimeUnit.MILLISECONDS.toSeconds(activity.getDeactivate().getMillis() - activity.getActivate().getMillis()) + "秒");
        activity = null;
    }
}
