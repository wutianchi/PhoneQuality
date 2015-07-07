package com.cattsoft.phone.quality.service.handler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.telephony.ServiceState;
import android.telephony.SmsMessage;
import android.util.Log;
import com.cattsoft.commons.digest.StringUtils;
import com.cattsoft.phone.quality.PhoneQualityActivity;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.PseudoSms;
import com.cattsoft.phone.quality.utils.Contacts;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import org.joda.time.DateTime;
import org.joda.time.Period;
import roboguice.receiver.RoboBroadcastReceiver;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 伪基站现象判定.
 * Created by Xiaohong on 2014/4/1.
 */
public class PseudoBaseHandler {
    public static final String TAG = "PSEUDO_BASE";

    private static Context context;
    private static NotificationManager notificationManager;

    private PhenoLevel level = PhenoLevel.NORMAL;
    private SmsReceivedReceiver smsReceivedReceiver;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            handler.sendEmptyMessageDelayed(1, TimeUnit.SECONDS.toMillis(30));
            // 每30秒调回一次等级
            level = level.toBack();
            return true;
        }
    });
    /** 基站位置短时间内变更次数 */
    private int cellChangedFreq = 0;
    private DateTime preCellChanged;
    /** 上一次脱网时间 */
    private DateTime preServiceOff;
    /** 上一次收到短信，避免重复问题 */
    private DateTime preSmsReceived = DateTime.now();
    /** 连续强信号数量 */
    private int strongSignalFreq = 0;
    public PseudoBaseHandler(Context context) {
        this.context = context;
        try {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            handler.obtainMessage(1).sendToTarget();
            smsReceivedReceiver = new SmsReceivedReceiver();
            IntentFilter smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            smsFilter.setPriority(Integer.MAX_VALUE);
            context.registerReceiver(smsReceivedReceiver, smsFilter);
        } catch (Exception e) {
            Log.w(TAG, "无法注册伪基站识别");
        }
//        Log.d("handle", "已注册短信接收器:" + smsFilter);
    }

    public void handleCellLocationChanged() {
        // 基站位置变动频繁
        try {
            if (null != preCellChanged) {
                // 基站位置变更间隔小于5秒并变更超过10次时提升等级
                Period period = new Period(preServiceOff, DateTime.now());
                if (period.getSeconds() < 3) {
                    cellChangedFreq++;
                    if (cellChangedFreq >= 5) {
                        level = level.toNext();
                        Log.d(TAG, "基站位置变更符合预定规则，已变更等级：" + level);
                    }
                    preCellChanged = DateTime.now();
                } else {
                    cellChangedFreq = 0;
                    preCellChanged = null;
                }
            }
        } catch (Exception e) {
        }
    }

    public void handleDataConnectionState(int state, int networkType) {
        // 数据连接断开
        // level = level.toNext();
    }

    public void handleBaseCellChanged(String mcc, String mnc, int lac, int cid) {
        //Lac 范围 0x0000－0xFFFF 该范围详见百度百科：http://baike.baidu.com/client/view/1243005.htm?app=3&font=2&statwiki=1
        //Cid 范围 0 - 65535
//        Log.d(TAG, "基站位置改变:" + lac + ", " + cid);
        if (lac == 0) {
            // 位置区域码为0时确定为伪基站
            level = PhenoLevel.WARNING.toNext();
        } else if ((lac < 0x0000 || lac > 0xFFFF) || (cid < 0 || cid > 65535)) {
            level = level.toNext();
        }
    }

    public void handleServiceState(ServiceState serviceState) {
        try {
            // 是否脱网8～12 秒,统计脱网时长
            if (serviceState.getState() == ServiceState.STATE_OUT_OF_SERVICE) {
//            Log.d(TAG, "当前网络出现脱网");
                preServiceOff = DateTime.now();
                preSmsReceived = null;
            } else if (null != preServiceOff) {
                // 计算脱网时长
                Period period = new Period(preServiceOff, DateTime.now());
                if (period.getSeconds() >= 2 && period.getSeconds() <= 15) {
                    // 8 ~ 15 秒脱网时长
                    level = level.toNext();
                    Log.d(TAG, "出现断网时长符合预定规则8~15秒，已变更等级：" + level);
                } else {
                    level = level.toBack();
                }
                preServiceOff = null;
            }
        } catch (Exception e) {
        }
    }

    public void handleSignalStrengths(int mGsmSignalStrength) {
        // 处于强信号状态
        if (mGsmSignalStrength > -50 && mGsmSignalStrength < 0) {
            // 强信号
//            Log.d(TAG, "强信号状态");

            strongSignalFreq++;
            if (strongSignalFreq >= 10) {
                level = level.toNext();
                strongSignalFreq = 0;
            }
        } else {
            strongSignalFreq = 0;
        }
    }

    private void handleSmsReceived(Context context, Intent intent) {
        // 收到短信
        if (null == preSmsReceived) {
//            Log.d(TAG, "接收到短信");
            preSmsReceived = DateTime.now();

            // 当被重置回NORMAL时表示已发送提示，当前为伪基站
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    String address = messages[i].getOriginatingAddress();
                    String name = Contacts.lookupPersonName(context, address, null);
                    // 发件人为存在,不应当视为伪基站短信
                    if (null != name)
                        return;
                }
                level = level.toNext();
                if (level == PhenoLevel.NORMAL) {
                    // 保存短信
                    for (int i = 0; i < messages.length; i++) {
                        try {
                            String address = messages[i].getOriginatingAddress();
                            String name = Contacts.lookupPersonName(context, address, null);
                            String content = messages[i].getMessageBody();
                            String service = messages[i].getServiceCenterAddress();
                            int status = messages[i].getStatus();
                            DateTime dateTime = new DateTime(messages[i].getTimestampMillis());
                            PseudoSms pseudoBaseSms = new PseudoSms(address, StringUtils.isEmpty(name) ? address : name, status, service, messages[i].getProtocolIdentifier(), content, dateTime);
                            // 保存
                            RuntimeExceptionDao<PseudoSms, Long> dao = QualityApplication.getApplication(context).getDatabaseHelper().getPseudoSmsDAO();
                            dao.create(pseudoBaseSms);
                        } catch (Exception e) {
                            Log.e(TAG, "伪基站短信保存失败", e);
                        }
                    }
                }
            }
        } else {
            //
        }
    }

    /** 通知等级 */
    private static enum PhenoLevel implements Serializable {
        NORMAL, PENDING, WARNING;

        public PhenoLevel toBack() {
            if (this == WARNING)
                return PENDING;
            else
                return NORMAL;
        }

        public PhenoLevel toNext() {
            try {
                if (this == PENDING)
                    return WARNING;
                else if (this == NORMAL)
                    return PENDING;
                else
                    return NORMAL;
            } finally {
                Log.d(TAG, "当前识别等级：" + this);
                if (this == WARNING) {
                    try {
                        // 发出提示/警报
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("伪基站提醒")
                                .setContentText("当前手机使用的基站可能是伪基站，请注意不要相信此时接收到的短信关于汇款等的信息");
                        mBuilder.setTicker("当前基站可能是伪基站");//第一次提示消息的时候显示在通知栏上
                        mBuilder.setAutoCancel(true);//自己维护通知的消失
                        mBuilder.setContentIntent(PendingIntent.getActivity(context, 1011, new Intent(context, PhoneQualityActivity.class), 0));

                        Notification notification = mBuilder.build();
                        //通知的默认参数 DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS.
                        //如果要全部采用默认值, 用 DEFAULT_ALL.
                        //此处采用默认声音
                        notification.defaults |= Notification.DEFAULT_SOUND;
                        notification.defaults |= Notification.DEFAULT_VIBRATE;
                        notification.defaults |= Notification.DEFAULT_LIGHTS;
                        //通知被点击后，自动消失
                        notification.flags |= Notification.FLAG_AUTO_CANCEL;
                        //点击'Clear'时，不清楚该通知(QQ的通知无法清除，就是用的这个)
//                notification.flags |= Notification.FLAG_NO_CLEAR;

                        //获取通知管理器对象
                        // notificationManager.notify(0, notification);
                    } catch (Exception e) {
                        Log.e(TAG, "无法发送伪基站提示信息", e);
                    }

                    return NORMAL;
                }
            }
        }

        public PhenoLevel relieve() {
            return NORMAL;
        }
    }

    public class SmsReceivedReceiver extends RoboBroadcastReceiver {
        @Override
        protected void handleReceive(Context context, Intent intent) {
            super.handleReceive(context, intent);
            try {
                handleSmsReceived(context, intent);
            } catch (Exception e) {
            }
        }
    }
}
