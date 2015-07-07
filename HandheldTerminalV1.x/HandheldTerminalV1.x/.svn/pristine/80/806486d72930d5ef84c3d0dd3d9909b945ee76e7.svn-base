package com.cattsoft.phone.quality.service.handler;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.util.Log;
import com.cattsoft.phone.quality.BuildConfig;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.model.SignalActivity;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.cattsoft.phone.quality.utils.Signal;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import roboguice.RoboGuice;

import java.util.concurrent.TimeUnit;

/**
 * 网络信号处理.
 * Created by Xiaohong on 2014/5/11.
 */
public class SignalStrengthHandler {
    public static final String TAG = "signal";
    private Context context;
    private DateTime activate = null;
    private MobileNetType.NetWorkType type = MobileNetType.NetWorkType.TYPE_NONE;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        DateTime trigger = DateTime.now().plusHours(1)
                                .withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
                                .minusSeconds(1);
                        // 距离下个整点不足10分钟，整点前发送一次
                        if (new Duration(DateTime.now(), trigger).getStandardMinutes() < 5)
                            handler.sendEmptyMessageAtTime(0, trigger.getMillis());
                        else
                            handler.sendEmptyMessageDelayed(0, TimeUnit.MINUTES.toMillis(3));
                        if (BuildConfig.DEBUG)
                            Log.v(TAG, "已定时统计网络信号类型时长");
                        deactivate();
                        activate();
                    } catch (Exception e) {
                        Log.w(TAG, "网络信号类型时长统计失败", e);
                    }
                    break;
            }
            return false;
        }
    });

    public SignalStrengthHandler(Context context) {
        this.context = context;
        RoboGuice.getBaseApplicationInjector((Application) context.getApplicationContext()).injectMembers(this);
        handler.sendEmptyMessageDelayed(0, TimeUnit.MINUTES.toMillis(1));
    }

    public Signal handle(SignalStrength signalStrength) {
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        CellInfoGsm cellinfogsm = (CellInfoGsm)telephonyManager.getAllCellInfo().get(0);
//        CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
//        cellSignalStrengthGsm.getDbm();

        MobileNetType.NetWorkType netWorkType = MobileNetType.getMobileType(context);
        if (netWorkType != type) {
            if (type != MobileNetType.NetWorkType.TYPE_NONE)
                deactivate();
            type = netWorkType;
            activate();
        }
        Signal signal = new Signal(signalStrength);
        DateTime date = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0);
        SignalActivity activity = null;
        try {
            RuntimeExceptionDao<SignalActivity, Long> dao = QualityApplication.getApplication(context).getDatabaseHelper().getSignalActivitieDAO();
            activity = dao.queryForFirst(dao.queryBuilder().where().eq("ddate", date).prepare());
        } catch (Exception e) {
        }
        ;
        if (null == activity)
            activity = new SignalActivity(date);
        activity.setSignals(activity.getSignals() + signal.getmGsmSignalStrength());
        activity.setTargets(activity.getTargets() + 1);
        if (signal.getmLteSignalStrength() != -99 && signal.getmLteSignalStrength() != -1) {
            activity.setSignals4G(activity.getSignals4G() + signal.getmLteSignalStrength());
            activity.setTargets4G(activity.getTargets4G() + 1);
        }
        try {
            RuntimeExceptionDao<SignalActivity, Long> dao = QualityApplication.getApplication(context).getDatabaseHelper().getSignalActivitieDAO();
            if (activity.getId() == 0)
                dao.create(activity);
            else
                dao.update(activity);
        } catch (Exception e) {
            Log.d(TAG, "当前信号强度统计失败", e);
        }
        return signal;
    }

    public void handle(ServiceState serviceState) {
        if (serviceState.getState() == ServiceState.STATE_OUT_OF_SERVICE ||
                serviceState.getState() == ServiceState.STATE_POWER_OFF) {
            type = MobileNetType.NetWorkType.TYPE_INVALID;
            activate();
        } else if (type == MobileNetType.NetWorkType.TYPE_INVALID) {
            deactivate();
        }
    }

    private void activate() {
        activate = DateTime.now();
    }

    private void deactivate() {
        long millis = new Duration(activate, DateTime.now()).getMillis();
        DateTime date = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0);
        SignalActivity activity = null;
        try {
            RuntimeExceptionDao<SignalActivity, Long> dao = QualityApplication.getApplication(context).getDatabaseHelper().getSignalActivitieDAO();
            activity = dao.queryForFirst(dao.queryBuilder().where().eq("ddate", date).prepare());
        } catch (Exception e) {
        }
        if (null == activity)
            activity = new SignalActivity(date);
        if (type == MobileNetType.NetWorkType.TYPE_2G)
            activity.setSignal2G(activity.getSignal2G() + millis);
        else if (type == MobileNetType.NetWorkType.TYPE_3G)
            activity.setSignal3G(activity.getSignal4G() + millis);
        else if (type == MobileNetType.NetWorkType.TYPE_4G)
            activity.setSignal4G(activity.getSignal4G() + millis);
        else if (type == MobileNetType.NetWorkType.TYPE_INVALID)
            activity.setNoSignal(activity.getNoSignal() + millis);
        try {
            RuntimeExceptionDao<SignalActivity, Long> dao = QualityApplication.getApplication(context).getDatabaseHelper().getSignalActivitieDAO();
            if (activity.getId() == 0)
                dao.create(activity);
            else
                dao.update(activity);
        } catch (Exception e) {
        }
    }


}
