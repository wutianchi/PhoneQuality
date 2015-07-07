package com.cattsoft.phone.quality.task;

import android.content.Context;
import android.os.Handler;
import com.cattsoft.phone.quality.utils.OAuth;

/**
 * 设备认证任务
 * Created by Xiaohong on 2014/5/3.
 */
public class DeviceAuthTask extends BaseAsyncTask<String> {
    private static final String TAG = "auth";

    public DeviceAuthTask(Context context, Handler handler) {
        super(context, handler);
    }

    @Override
    public String call() throws Exception {
        return OAuth.oauth(getContext());
    }

    @Override
    protected void onSuccess(String token) throws Exception {
        super.onSuccess(token);
        if (null != handler)
            handler.obtainMessage(0, token).sendToTarget();
    }

    @Override
    protected void onThrowable(Throwable t) throws RuntimeException {
        if (null != handler)
            handler.obtainMessage(1).sendToTarget();
    }
}
