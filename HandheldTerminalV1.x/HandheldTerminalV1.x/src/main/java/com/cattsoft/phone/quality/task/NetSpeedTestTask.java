package com.cattsoft.phone.quality.task;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.cattsoft.phone.quality.utils.Devices;
import com.cattsoft.phone.quality.utils.speed.NetSpeedTest;
import roboguice.util.RoboAsyncTask;

/**
 * 网络速率测试任务.
 * 因不知道是RoboGuice抽疯还是Android抽疯，
 * 运行有可能会导致出现{@link android.os.NetworkOnMainThreadException}的异常，
 * 暂时放弃使用{@link roboguice.util.RoboAsyncTask}，请使用{@link com.cattsoft.phone.quality.task.SpeedTestAsyncTask}
 * Created by Xiaohong on 2014/5/4.
 */
@Deprecated
public class NetSpeedTestTask extends RoboAsyncTask<Boolean> {
    /** 测速服务不可用 */
    public static final int SERVER_INVALID = 104;
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
    private static final String TAG = "speed";
    private boolean canceled = false;
    private NetSpeedTest speedTest;
    private String uuid;

    public NetSpeedTestTask(Context context, Handler handler) {
        super(context, handler);
    }

    @Override
    protected void onPreExecute() throws Exception {
        super.onPreExecute();
        speedTest = new NetSpeedTest(getContext().getCacheDir(), Devices.availableProcessors(), 10) {
            @Override
            public void handleProgress(Type type, long length, long bytes, long speed, long seq) {
                sendMessage(TEST_PROGRESS, new long[]{type.value, length, bytes, speed, seq});
            }
        };
    }

    /**
     * 获取当前测速服务状态
     *
     * @param flag 为true时表示获取当前服务器是否可用，否则表示注销
     * @return 是否成功
     */
    public boolean dovalid(boolean flag) {
        String url = String.format("http://%s:%d/speed/dovalid", "61.135.214.54", 8080);
        // Create a new RestTemplate instance
//        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//        requestFactory.setReadTimeout(2000);
//        requestFactory.setConnectTimeout(3000);
//        RestTemplate restTemplate = new RestTemplate(requestFactory);
//        // Add the String message converter
//        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
//        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
//
//        MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
//        request.add("key", (Strings.isEmpty(uuid) ? System.currentTimeMillis() : uuid));
//        request.add("flag", flag);
        try {
            String result = "";//restTemplate.getForObject(url, String.class, request);
            if (result.startsWith("1"))
                uuid = result.substring(2);
            return result.startsWith("1");
        } catch (Exception e) {
            Log.w(TAG, "当前测速服务器不可用", e);
            if (flag) {
                sendMessage(SERVER_INVALID);
                throw new RuntimeException(e);
            }
            return false;
        }
    }

    @Override
    public Boolean call() throws Exception {
        boolean valid = false;
        try {
            valid = dovalid(true);
        } catch (Exception e) {
            // 网络异常
        } finally {
            if (!valid) {
                // 当前测速服务器不可用
                sendMessage(SERVER_INVALID);
                return false;
            }
        }
//        if(!canceled)
//            speedTest.ping();
//        if(!canceled)
//            speedTest.upload();
//        if(!canceled)
//            speedTest.download(HttpRequest.post());
        return true;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        canceled = true;
        speedTest.cancel();
        sendMessage(USER_CANCELED);
        Log.w(TAG, "测试已由用户取消");
        return super.cancel(mayInterruptIfRunning);
    }

    @Override
    protected void onSuccess(Boolean b) throws Exception {
        super.onSuccess(b);
        if (!canceled && b) {
            // 测速成功
            sendMessage(SUCCESS);
        }
    }

    @Override
    protected void onThrowable(Throwable t) throws RuntimeException {
        if (!canceled) {
            sendMessage(TEST_FAILED);
            Log.w(TAG, "测试过程出现异常", t);
        }
        canceled = true;
        speedTest.cancel();
    }

    @Override
    protected void onFinally() throws RuntimeException {
        dovalid(false);
        sendMessage(COMPLETE);
        super.onFinally();
    }

    private void sendMessage(int what) {
        if (null != handler)
            handler.obtainMessage(what).sendToTarget();
    }

    private void sendMessage(int what, Object obj) {
        if (null != handler)
            handler.obtainMessage(what, obj).sendToTarget();
    }
}
