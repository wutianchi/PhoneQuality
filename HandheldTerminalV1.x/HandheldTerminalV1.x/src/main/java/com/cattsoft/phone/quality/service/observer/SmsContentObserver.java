package com.cattsoft.phone.quality.service.observer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.model.SmsStructure;
import com.cattsoft.phone.quality.utils.Contacts;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.google.inject.Inject;
import org.joda.time.DateTime;
import roboguice.RoboGuice;

/**
 * 短信数据库内容变更处理.
 * Created by Xiaohong on 2014/5/3.
 */
public class SmsContentObserver extends ContentObserver {
    public static final String SMS_FLAG_ID = "sms_id";
    public static final int SMS_TYPE_ALL = 0;
    public static final int SMS_TYPE_INBOX = 1;
    public static final int SMS_TYPE_SENT = 2;
    public static final int SMS_TYPE_DRAFT = 3;
    public static final int SMS_TYPE_OUTBOX = 4;
    public static final int SMS_TYPE_FAILED = 5; // for failed outgoing messages
    public static final int SMS_TYPE_QUEUED = 6; // for messages to send later
    private static final String TAG = "sms";
    public static Uri SMS_URI = Uri.parse("content://sms");
    @Inject
    SharedPreferences sharedPreferences;
    private Context context;

    /**
     * Creates a content observer.
     */
    public SmsContentObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
        RoboGuice.getBaseApplicationInjector((Application) context.getApplicationContext()).injectMembers(this);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(SMS_URI, null, null, null, null);
            // this will make it point to the first record, which is the last SMS sent
            if (!cursor.moveToNext())
                return;
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String id = cursor.getString(cursor.getColumnIndex("_id"));
            String last_id = sharedPreferences.getString(SMS_FLAG_ID, "");

            if ((type == SMS_TYPE_SENT || type == SMS_TYPE_INBOX || type == SMS_TYPE_FAILED)
                    && (!last_id.contentEquals(id))) {
                sharedPreferences.edit().putString(SMS_FLAG_ID, id).commit();

                // String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
                String number = cursor.getString(cursor.getColumnIndex("address"));
                String name = Contacts.lookupPersonName(context, number, null);
                DateTime ddate = new DateTime(cursor.getLong(cursor.getColumnIndex("date")));

                SmsStructure structure = new SmsStructure(MobileNetType.getPhoneType(context),
                        MobileNetType.getMobileTypeValue(context), name, number, type, ddate);
                QualityApplication.getApplication(context).getDatabaseHelper().getSmsStructureDAO().create(structure);
            } else {
                Log.d(TAG, "message type is not supported：" + type);
            }
        } catch (Exception e) {
            Log.w(TAG, "短信记录统计出现异常", e);
        } finally {
            try {
                if (null != cursor) cursor.close();
            } catch (Exception e) {
            }
        }
    }
}
