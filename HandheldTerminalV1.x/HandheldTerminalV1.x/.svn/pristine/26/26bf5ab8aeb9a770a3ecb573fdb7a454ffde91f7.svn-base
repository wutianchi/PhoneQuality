package com.cattsoft.phone.quality.service.observer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.model.CallsStructure;
import com.cattsoft.phone.quality.utils.CallDropCause;
import com.cattsoft.phone.quality.utils.Contacts;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.google.inject.Inject;
import org.joda.time.DateTime;
import roboguice.RoboGuice;

/**
 * 通话记录数据库变更处理.
 * Created by Xiaohong on 2014/5/9.
 */
public class CallsContentObserver extends ContentObserver {
    public static final String CALLS_FLAG_ID = "calls_id";
    private static final String TAG = "calls";
    public static Uri CALLS_URI = CallLog.Calls.CONTENT_URI;
    @Inject
    SharedPreferences sharedPreferences;

    private Context context;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public CallsContentObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
        RoboGuice.getBaseApplicationInjector((Application) context.getApplicationContext()).injectMembers(this);
    }

    @Override
    public void onChange(boolean selfChange) {
        onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(CALLS_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
            if (!cursor.moveToFirst())
                return;
            String id = cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            String last_id = sharedPreferences.getString(CALLS_FLAG_ID, "");
            if (!last_id.contentEquals(id)) {
                sharedPreferences.edit().putString(CALLS_FLAG_ID, id).commit();
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                String name = Contacts.lookupPersonName(context, number, cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
                long duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
                DateTime ddate = new DateTime(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));

                CallsStructure structure = new CallsStructure(MobileNetType.getPhoneType(context),
                        MobileNetType.getMobileTypeValue(context),
                        name, number, type, CallDropCause.UNKNOW.toString(), duration, ddate);
                QualityApplication.getApplication(context).getDatabaseHelper().getCallsStructureDAO().create(structure);
            }
        } catch (Exception e) {
            Log.w(TAG, "通话记录统计出现异常", e);
        } finally {
            try {
                if (null != cursor) cursor.close();
            } catch (Exception e) {
            }
        }
    }
}
