package com.cattsoft.phone.quality.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Xiaohong on 2014/5/4.
 */
public abstract class BaseAsyncTask<ResultT> extends AsyncTask<Void, Void, ResultT> {
    protected Handler handler;
    protected Context context;

    public BaseAsyncTask(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        // RoboGuice.getInjector(context).injectMembers(this);
    }

    @Override
    protected final ResultT doInBackground(Void... params) {
        try {
            ResultT t = call();
            onSuccess(t);
            return t;
        } catch (Exception e) {
            onException(e);
        } finally {
            onFinally();
        }
        return null;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    abstract ResultT call() throws Exception;

    protected void onException(Exception e) throws RuntimeException {
        onThrowable(e);
    }

    protected void onThrowable(Throwable t) throws RuntimeException {
        Log.e("roboguice", "Throwable caught during background processing", t);
    }

    protected void onSuccess(ResultT resultT) throws Exception {
        //
    }

    /**
     * @throws RuntimeException, ignored
     */
    protected void onFinally() throws RuntimeException {
    }

    public Handler handler() {
        return handler;
    }

    public Context getContext() {
        return context;
    }

    protected void sendMessage(int what) {
        if (null != handler)
            handler.obtainMessage(what).sendToTarget();
    }

    protected void sendMessage(int what, Object obj) {
        if (null != handler)
            handler.obtainMessage(what, obj).sendToTarget();
    }
}
