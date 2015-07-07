package com.cattsoft.phone.quality.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.SpeedTestResult;
import com.cattsoft.phone.quality.utils.Devices;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.cattsoft.phone.quality.utils.speed.NetSpeedTest;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.inject.Inject;
import org.joda.time.DateTime;
import roboguice.util.Strings;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Xiaohong on 2014/5/4.
 */
public class SpeedTestAsyncTask extends BaseAsyncTask<Boolean> {
    /** 测速服务不可用 */
    public static final int SERVER_INVALID = 104;
    /** 服务器延迟 */
    public static final int SERVER_PING = 105;
    /** 用户取消 */
    public static final int USER_CANCELED = 102;
    /** 测试失败 */
    public static final int TEST_FAILED = 101;
    /** 测速完成 */
    public static final int SUCCESS = 100;
    /** 完成 */
    public static final int COMPLETE = 103;
    /** 测试进度处理 */
    public static final int TEST_PROGRESS = 200;
    /** 等待测试 */
    public static final int WAIT_TO_TEST = 206;
    /** 切换 */
    public static final int SWITCH = 300;
    private static final String TAG = "speed";
    @Inject
    SharedPreferences sharedPreferences;
    private boolean canceled = false;
    private NetSpeedTest speedTest;
    private String uuid;
    private String server, serverName, host;
    private SpeedTestResult result;
    private int seconds = 10;
    private AtomicLong speedSum;

    public SpeedTestAsyncTask(Context context, String server, Handler handler) {
        super(context, handler);
        this.server = server;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sendMessage(WAIT_TO_TEST);
        speedSum = new AtomicLong(0);
        host = String.format("http://%s:%d/speed", server, 8080);
        result = new SpeedTestResult(MobileNetType.getPhoneType(context),
                MobileNetType.getMobileTypeValue(context),
                MobileNetType.getNetWorkType(context).type, server, serverName, DateTime.now());
        result.setLocation(QualityApplication.getApplication(context).getDatabaseHelper().getLastLocation());
        try {
            result.setOperator(Integer.parseInt(Devices.getOperatorCode(context)));
        } catch (Exception e) {
            Log.e(TAG, "设置运营商信息失败", e);
        }
        speedTest = new NetSpeedTest(getContext().getCacheDir(), Devices.availableProcessors() * 4, seconds) {
            @Override
            public void handleProgress(Type type, long length, long bytes, long speed, long sequence) {
                sendMessage(TEST_PROGRESS, new long[]{type.value, length, bytes, speed, sequence, speedSum.addAndGet(speed)});
            }
        };
    }

    /**
     * 获取当前测速服务状态
     *
     * @param canceled 是否注销当前测速编号
     * @return 是否成功
     */
    public boolean dovalid(boolean canceled) {
        try {
            String result = validRequest(canceled);
            if (null != result && result.startsWith("1")) {
                uuid = result.substring(2);
                Log.d(TAG, "dovalid request：" + result);
                return true;
            }
            return false;
        } catch (Exception e) {
            if (!canceled) {
                Log.w(TAG, "当前测速服务器不可用", e);
                sendMessage(SERVER_INVALID);
                throw new RuntimeException(e);
            }
            return false;
        }
    }

    private String validRequest(boolean canceled) {
        String url = host + "/dovalid";

        Map<String, Object> request = new LinkedHashMap<String, Object>();
        request.put("key", (Strings.isEmpty(uuid) ? System.currentTimeMillis() : uuid));
        request.put("flag", Boolean.toString(true));
        if (canceled) {
            return HttpRequest.post(url, request, false)
                    .readTimeout((int) TimeUnit.SECONDS.toMillis(1))
                    .connectTimeout((int) TimeUnit.SECONDS.toMillis(3)).body("UTF-8");
        } else {
            return HttpRequest.get(url, request, false)
                    .readTimeout((int) TimeUnit.SECONDS.toMillis(1))
                    .connectTimeout((int) TimeUnit.SECONDS.toMillis(3)).body("UTF-8");
        }
    }

    @Override
    Boolean call() throws Exception {
        if (checkValid()) return false;
        ping();
        download();
        upload();

        return true;
    }

    private void upload() throws InterruptedException, MalformedURLException {
        if (!canceled) {
            handler.obtainMessage(SWITCH, NetSpeedTest.Type.UPLOAD).sendToTarget();
            Thread.currentThread().sleep(500);

            double bytes = speedTest.upload(new URL(HttpRequest.post(host + "/doload.do", defaultParams(), false).url().toString()));
            Log.d(TAG, "总上传长度：" + (bytes / 1024.0));
            result.setUpload(speedSum.get() / (double) seconds);
            speedSum.set(0);
            Log.d(TAG, "上传测试完成，平均:" + (result.getUpload() / 1024.0) + " KB/s");
            if (0 == bytes)
                result.setFailure(1);   // 上传失败
        }
    }

    private Map<String, Object> defaultParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("key", uuid);
        params.put("r", System.currentTimeMillis());

        return params;
    }

    private void download() throws Exception {
        if (!canceled) {
            boolean bandwidth = sharedPreferences.getBoolean("pref_key_bandwidth_option", false);
            String url = bandwidth ? (sharedPreferences.getString("pref_key_speed_url", getContext().getString(R.string.speed_bandwidth_url))) : host + "/30MB.dat";

            try {
                Log.d(TAG, bandwidth ? "文件下载测速" : "网络带宽测速");
                handler.obtainMessage(SWITCH, NetSpeedTest.Type.DOWNLOAD).sendToTarget();
                Thread.currentThread().sleep(500);
                double bytes = speedTest.download(HttpRequest.get(url, defaultParams(), false).url());
                Log.d(TAG, "总下载长度：" + (bytes / 1024.0));
                result.setDownload(speedSum.get() / (double) seconds);
            } catch (Exception e) {
                result.setFailure(2);
                throw e;
            } finally {
                speedSum.set(0);
            }
            Log.d(TAG, "下载测试完成，平均:" + (result.getDownload() / 1024.0) + " KB/s");
        }
    }

    private void ping() {
        if (!canceled) {
            handler.obtainMessage(SWITCH, NetSpeedTest.Type.PING).sendToTarget();
            float ping = speedTest.ping(server);
            result.setPing(ping);
            sendMessage(SERVER_PING, ping);
        }
    }

    private boolean checkValid() {
        boolean valid = false;
        try {
            valid = dovalid(false);
        } catch (Exception e) {
            // 网络异常
            Log.w(TAG, "连接测速服务器时出现异常，请检查网络", e);
        } finally {
            if (!valid) {
                // 当前测速服务器不可用
                sendMessage(SERVER_INVALID);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onSuccess(Boolean b) throws Exception {
        super.onSuccess(b);
        if (!canceled && b) {
            // 保存到数据库
            try {
                QualityApplication.getApplication(context).getDatabaseHelper().getSpeedTestResultDAO().create(result);
            } catch (Exception e) {
                Log.e(TAG, "保存测速数据时出现异常", e);
            }
            // 测速成功
            sendMessage(SUCCESS, result);
        }
    }

    @Override
    protected void onThrowable(Throwable t) throws RuntimeException {
        if (!canceled) {
            sendMessage(TEST_FAILED, result);
            Log.w(TAG, "测试过程出现异常", t);
        }
        canceled = true;
        speedTest.cancel();
    }

    @Override
    protected void onFinally() throws RuntimeException {
        dovalid(true);
        sendMessage(COMPLETE, result);
        super.onFinally();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel();
    }

    public void cancel() {
        try {
            canceled = true;
            speedTest.cancel();
            result.setFailure(-1);
            sendMessage(USER_CANCELED);
            Log.w(TAG, "测试已由用户取消");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
