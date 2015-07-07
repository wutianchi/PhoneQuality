package com.cattsoft.phone.quality.service.receiver;

import android.content.Context;
import android.content.Intent;
import com.cattsoft.phone.quality.service.PhoneStateService;
import roboguice.receiver.RoboBroadcastReceiver;

/**
 * Created by Xiaohong on 2014/5/1.
 */
public class BootCompleteReceiver extends RoboBroadcastReceiver {
    private static final String TAG = "boot";

    @Override
    protected void handleReceive(Context context, Intent intent) {
        super.handleReceive(context, intent);
        try {
            Intent service = new Intent(context.getApplicationContext(), PhoneStateService.class);
            service.putExtra("flag", 0);
            context.startService(service);
        } catch (Throwable t) {

        }
    }
}
