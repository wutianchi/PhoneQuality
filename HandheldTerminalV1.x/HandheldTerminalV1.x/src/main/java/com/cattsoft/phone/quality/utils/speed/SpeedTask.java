package com.cattsoft.phone.quality.utils.speed;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Created by Xiaohong on 2014/5/4.
 */
public abstract class SpeedTask implements Runnable {
    protected Counter counter;
    protected String threadName;

    public SpeedTask(Counter counter) {
        this.counter = counter;
    }

    public abstract void interrupt();

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    protected void closeQuality(HttpURLConnection connection) {
        if (null != connection) {
            try {
                connection.disconnect();
            } catch (Exception e) {
            }
        }
    }

    protected void closeQuality(InputStream inputStream) {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (Exception e) {
            }
        }
    }

    protected void closeQuality(OutputStream outputStream) {
        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (Exception e) {
            }
        }
    }
}
