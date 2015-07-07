package com.cattsoft.phone.quality.utils.speed;

import java.util.concurrent.TimeUnit;

/**
 * 计时器线程.
 * Created by Xiaohong on 2014/5/4.
 */
public class SecondsTimerThread implements Runnable {
    public static final int DEFAULT_SECONDS = 10;
    /** 计时时长 */
    private int seconds;
    /** 中断标记 */
    private boolean interrup = false;
    /** 计数器 */
    private Counter counter;

    private Callback callback;

    public SecondsTimerThread() {
        this(DEFAULT_SECONDS);
    }

    public SecondsTimerThread(int seconds) {
        this(seconds, null);
    }

    public SecondsTimerThread(Callback callback) {
        this(DEFAULT_SECONDS, callback);
    }

    public SecondsTimerThread(int seconds, Callback callback) {
        this.seconds = seconds;
        this.callback = callback;
    }

    @Override
    public void run() {
        // 设置计时器线程优先级
        // Java
        // Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        // Android
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        int _seconds = seconds;
        while ((_seconds--) > 0) {
            if (interrup || Thread.currentThread().isInterrupted())
                break;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                break;
            }
            if (null != callback)
                callback.progress();
        }
        interrup = false;
        if (null != callback)
            callback.callback();
    }

    public void interrupt() {
        interrup = true;
        try {
            Thread.currentThread().interrupted();
        } catch (Exception e) {
        }
    }

    public Thread start() {
        Thread thread = new Thread(this);
        thread.start();
        return thread;
    }

    public int getSeconds() {
        return seconds;
    }

    public static abstract class Callback {
        public void progress() {
            //
        }

        ;

        public abstract void callback();
    }

}
