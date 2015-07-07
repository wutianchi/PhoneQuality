package com.cattsoft.phone.quality.service.receiver;

import android.content.Context;
import android.content.Intent;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import com.cattsoft.phone.quality.service.DropCallService;
import roboguice.receiver.RoboBroadcastReceiver;

/**
 * 呼叫状态广播接收.
 * Created by Xiaohong on 2014/5/3.
 */
public class CallStateReceiver extends RoboBroadcastReceiver {
    private static final String TAG = "call";

    @Override
    protected void handleReceive(Context context, Intent intent) {
        super.handleReceive(context, intent);
        final String extra = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        Intent dropCall = new Intent(context.getApplicationContext(), DropCallService.class);
        dropCall.putExtra("flag", DropCallService.CALL_REQUEST_NONE);
        dropCall.putExtra("name", intent.getStringExtra("name"));
        dropCall.putExtra("numberType", intent.getIntExtra("numberType", -1));
        dropCall.putExtra("fromDialer", intent.getBooleanExtra("fromDialer", false));
        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
            // 主叫呼出
            final String originalNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            dropCall.putExtra("flag", DropCallService.CALL_REQUEST_OUTGOING);
            dropCall.putExtra("type", CallLog.Calls.OUTGOING_TYPE);
            dropCall.putExtra("number", originalNumber);
        } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(extra)) {
            // 被叫，正在振铃
            final String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            dropCall.putExtra("flag", DropCallService.CALL_REQUEST_INCOMING);
            dropCall.putExtra("type", CallLog.Calls.INCOMING_TYPE);
            dropCall.putExtra("number", incomingNumber);
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(extra)) {
            // 挂机
            final String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            dropCall.putExtra("flag", DropCallService.CALL_REQUEST_IDLE);
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(extra)) {
            // 摘机，通话状态
            final String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            dropCall.putExtra("flag", DropCallService.CALL_REQUEST_OFFHOOK);   // 标识已接听，识别未接号码
        } else {
            return;
        }
        context.startService(dropCall);
    }
}
