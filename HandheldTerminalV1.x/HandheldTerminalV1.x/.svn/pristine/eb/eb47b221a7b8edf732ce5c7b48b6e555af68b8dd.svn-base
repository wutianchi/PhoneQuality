package com.cattsoft.phone.quality.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import roboguice.util.Strings;

/**
 * Created by Xiaohong on 2014/4/11.
 */
public class Contacts {
    public static String lookupPersonName(Context context, String address, String def) {
        Cursor cur = null;
        try {
            Uri personUri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI, address);
            cur = context.getContentResolver().query(personUri,
                    new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},
                    null, null, null);
            if (cur.moveToFirst()) {
                String name = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                return !Strings.isEmpty(name) ? name : def;
            }
        } catch (Exception e) {
        } finally {
            try {
                if (null != cur) cur.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return def;
    }
}
