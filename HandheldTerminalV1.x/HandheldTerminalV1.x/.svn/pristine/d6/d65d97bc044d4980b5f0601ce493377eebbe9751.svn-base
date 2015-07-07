package com.cattsoft.phone.quality.service;

import android.content.*;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.CallLog;
import android.util.Log;
import com.cattsoft.phone.quality.BuildConfig;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.model.CallCauseCache;
import com.cattsoft.phone.quality.model.CallsStructure;
import com.cattsoft.phone.quality.model.SmsStructure;
import com.cattsoft.phone.quality.service.aidl.IPhoneStateService;
import com.cattsoft.phone.quality.service.observer.CallsContentObserver;
import com.cattsoft.phone.quality.service.observer.SmsContentObserver;
import com.cattsoft.phone.quality.utils.CallDropCause;
import com.cattsoft.phone.quality.utils.Contacts;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.google.inject.Inject;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import org.joda.time.DateTime;
import roboguice.service.RoboIntentService;
import roboguice.util.Strings;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 数据通道服务.
 * 服务目前用来统计应用程序关闭或安装前的需要统计的数据.
 * Created by Xiaohong on 2014/5/9.
 */
public class DataTunnelService extends RoboIntentService {
    public static final String TAG = "DataTunnel";
    @Inject
    SharedPreferences sharedPreferences;

    private IPhoneStateService binder = null;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = IPhoneStateService.Stub.asInterface(service);
            if (BuildConfig.DEBUG)
                Log.d(TAG, "PhoneStateService connected：" + binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };

    public DataTunnelService() {
        super("DataTunnelService");
    }

    private void smsTunnel() {
        Cursor cursor = null;
        String sms_id = null; // 数据处理标记，存入SharedPreferences
        try {
            RuntimeExceptionDao<SmsStructure, Long> dao = QualityApplication.getApplication(this).getDatabaseHelper().getSmsStructureDAO();
            cursor = getContentResolver().query(SmsContentObserver.SMS_URI, null, "_id > ?",
                    new String[]{sharedPreferences.getString(SmsContentObserver.SMS_FLAG_ID, "0")}, "_ID ASC");
            while (cursor.moveToNext()) {
                sms_id = cursor.getString(cursor.getColumnIndex("_id"));
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                String number = cursor.getString(cursor.getColumnIndex("address"));
                String name = Contacts.lookupPersonName(getApplicationContext(), number, null);
                DateTime ddate = new DateTime(cursor.getLong(cursor.getColumnIndex("date")));

                SmsStructure structure = new SmsStructure(MobileNetType.getPhoneType(getApplicationContext()),
                        MobileNetType.getMobileTypeValue(getApplicationContext()), name, number, type, ddate);
                dao.create(structure);
            }
        } catch (Exception e) {
            Log.w(TAG, "未能处理当前短信记录数据", e);
        } finally {
            try {
                if (null != cursor) cursor.close();
            } catch (Exception e) {
            }
            if (!Strings.isEmpty(sms_id))
                sharedPreferences.edit().putString(SmsContentObserver.SMS_FLAG_ID, sms_id).commit();
        }
        sendMessage(PhoneStateService.OBSERVER_INSTALL, PhoneStateService.OBSERVER_INSTALL_SMS);
    }

    private void callsTunnel() {
        Cursor cursor = null;
        String calls_id = null; // 数据处理标记，存入SharedPreferences
        try {
            RuntimeExceptionDao<CallsStructure, Long> dao = QualityApplication.getApplication(this).getDatabaseHelper().getCallsStructureDAO();
            cursor = getContentResolver().query(CallsContentObserver.CALLS_URI, null, "_id > ?",
                    new String[]{sharedPreferences.getString(CallsContentObserver.CALLS_FLAG_ID, "0")}, CallLog.Calls._ID + " ASC");
            while (cursor.moveToNext()) {
                calls_id = cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID));
                int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                String name = Contacts.lookupPersonName(getApplicationContext(), number, cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
                long duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
                DateTime ddate = new DateTime(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
                CallsStructure structure = new CallsStructure(MobileNetType.getPhoneType(getApplicationContext()),
                        MobileNetType.getMobileTypeValue(getApplicationContext()),
                        name, number, type, CallDropCause.UNKNOW.toString(), duration, ddate);
                dao.create(structure);
            }
        } catch (Exception e) {
            Log.w(TAG, "未能处理当前通话记录数据", e);
        } finally {
            try {
                if (null != cursor) cursor.close();
            } catch (Exception e) {
            }
            if (!Strings.isEmpty(calls_id))
                sharedPreferences.edit().putString(CallsContentObserver.CALLS_FLAG_ID, calls_id).commit();
        }
        try {
            RuntimeExceptionDao<CallCauseCache, Long> dao = QualityApplication.getApplication(this).getDatabaseHelper().getCallCauseCacheDAO();
            RuntimeExceptionDao<CallsStructure, Long> callDao = QualityApplication.getApplication(this).getDatabaseHelper().getCallsStructureDAO();
            List<CallCauseCache> caches = dao.queryForAll();
            // 遍历
            if (null != caches && caches.size() > 0) {
                for (CallCauseCache cache : caches) {
                    CallsStructure struct = callDao.queryBuilder().orderBy("ddate", false).where().eq("ddate", cache.getDdate()).queryForFirst();
                    if (null != struct) {
                        // 更新断开原因
                        struct.setCause(cache.getCause());
                        callDao.update(struct);
                        // 删除
                        dao.deleteById(cache.getId());
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "更新通话断开原因时出现异常");
        }
        sendMessage(PhoneStateService.OBSERVER_INSTALL, PhoneStateService.OBSERVER_INSTALL_CALLS);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bindService(new Intent(getApplicationContext(), PhoneStateService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void sendMessage(int what, int arg1) {
        try {
            if (null != binder)
                binder.sendMessage(what, arg1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitService() {
        int seq = 0;
        while (null == binder) {
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (Exception e) {
                break;
            }
            if (++seq > 10) {
                Log.d(TAG, "无法连接远程服务");
                break;
            }
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int flag = intent.getIntExtra("flag", 1);
        // 短信，通话记录数据统计
        if (0 == flag) {
            // 因为操作比较耗时，使用普通服务会导致ANR
            // 等待服务连接后操作
            waitService();

            callsTunnel();
            smsTunnel();
            sendMessage(PhoneStateService.OBSERVER_INSTALL, PhoneStateService.OBSERVER_INSTALL);
        }
    }

    @Override
    public void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
