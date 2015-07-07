package com.cattsoft.phone.quality.service;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.model.CallCauseCache;
import com.cattsoft.phone.quality.model.CallsStructure;
import com.cattsoft.phone.quality.utils.DatabaseHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import org.joda.time.DateTime;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;
import roboguice.service.RoboService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 掉话率统计.
 * Created by Xiaohong on 2014/5/3.
 */
public class DropCallService extends RoboService {
    /** 默认值 */
    public static final int CALL_REQUEST_NONE = -1;
    /** 挂机 */
    public static final int CALL_REQUEST_IDLE = 0;
    /** 摘机 */
    public static final int CALL_REQUEST_OFFHOOK = 3;
    /** 被叫 */
    public static final int CALL_REQUEST_INCOMING = 1;
    /** 主叫 */
    public static final int CALL_REQUEST_OUTGOING = 2;
    private static final String TAG = "dropCall";
    private ProcessExecutor executor;
    private StartedProcess startedProcess;

    private CallCauseCache causeCache;

    /** 是否未接 */
    private boolean missed = true;
    private int type = CALL_REQUEST_NONE;

    private String regex = ".*(?i)onDisconnect.*(?i)cause\\s*=\\s*([\\w\\s]+).*(?i)incoming\\s*=\\s*(\\w+).*(?i)date\\s*=\\s*(\\d+)";
    private String mtRegex = "onCallConnected.*MT\\s+(?i)call.*\\s(\\w+)";

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    try {
                        DatabaseHelper databaseHelper = QualityApplication.getApplication(getApplicationContext()).getDatabaseHelper();
                        // 更新数据库通话记录中断原因
                        CallCauseCache cache = (CallCauseCache) msg.obj;
                        RuntimeExceptionDao<CallsStructure, Long> dao = databaseHelper.getCallsStructureDAO();
                        Map<String, Object> args = new HashMap<String, Object>();
                        args.put("type", cache.getType());
                        args.put("number", cache.getNumber());
                        args.put("ddate", cache.getDdate());
                        List<CallsStructure> eqList = dao.queryForFieldValues(args);
                        if (null != eqList && eqList.size() > 0) {
                            CallsStructure structure = eqList.get(0);
                            structure.setCause(cache.getCause());
                            dao.update(structure);
                            handler.removeCallbacksAndMessages(null);
                            break;
                        }
                        if (msg.arg1 < 3) {
                            handler.sendMessageDelayed(handler.obtainMessage(100, ++msg.arg1, 0, cache), TimeUnit.SECONDS.toMillis(3));
                        } else {
                            // 挂断原因存入缓存数据中
                            RuntimeExceptionDao<CallCauseCache, Long> cacheDao = databaseHelper.getCallCauseCacheDAO();
                            cacheDao.create(cache);
                            handler.removeCallbacksAndMessages(null);
                        }
                    } catch (Exception e) {
                    }
                    break;
                default:
                    stopSelf();
                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            int val = new ProcessExecutor("logcat", "-c").execute().getExitValue();
//            Log.d(TAG, "日志记录已清除：" + val);
        } catch (Exception e) {
            Log.w(TAG, "日志清除命令执行失败", e);
        }
        executor = new ProcessExecutor().command("logcat", "-s", "CallNotifier:D*:S")
                .redirectErrorStream(true)
                .readOutput(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 标识，非0时表示挂断
        int flag = intent.getIntExtra("flag", CALL_REQUEST_NONE);
        int _type = intent.getIntExtra("type", CALL_REQUEST_NONE);
        // 设置当前类型
        if (flag == CALL_REQUEST_NONE) {
            // 默认情况返回不处理
            return START_NOT_STICKY;
        } else if (_type == CallLog.Calls.OUTGOING_TYPE ||
                _type == CallLog.Calls.INCOMING_TYPE) {
            this.type = _type;
            try {
                if (null == startedProcess) {
                    causeCache = new CallCauseCache(type, intent.getStringExtra("name"), intent.getStringExtra("number"), true);
                    startedProcess = executor.start();
//                    Log.d(TAG, "正在读取：@" + startedProcess.hashCode());
                }
            } catch (Exception e) {
                Log.w(TAG, "无法启动日志读取进程", e);
            }
        } else if (flag == CALL_REQUEST_IDLE) {
            // 通话类型为被叫时识别是否未接听
            if (this.type == CallLog.Calls.INCOMING_TYPE)
                causeCache.setAnswered(missed ? false : true);
            handler.sendEmptyMessageDelayed(flag, TimeUnit.SECONDS.toMillis(1));
        } else if (flag == CALL_REQUEST_OFFHOOK) {
            // 标识已接听
            missed = false;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // 停止服务, 杀命令行进程
        try {
//            Log.d(TAG, "停止日志读取进程：" + startedProcess.process());
            startedProcess.process().destroy();
        } catch (Throwable t) {
            Log.w(TAG, "停止日志读取进程出现异常", t);
            try {
                android.os.Process.killProcess(pid(startedProcess.process()));
            } catch (Throwable t1) {
                Log.w(TAG, "强制停止进程时出现异常", t1);
            }
        }
        try {
            String result = startedProcess.future().get(1, TimeUnit.SECONDS).outputUTF8();
            Matcher matcher = Pattern.compile(regex).matcher(result);
            String[] groups = new String[matcher.groupCount()];
            while (matcher.find()) {
                for (int i = 0; i < matcher.groupCount(); i++)
                    groups[i] = matcher.group(i + 1);
            }
            matcher = Pattern.compile(mtRegex).matcher(result);
            boolean mt = false;
            while (matcher.find())
                mt = Boolean.parseBoolean(matcher.group(1));
            causeCache.setCause(groups[0]);
            causeCache.setDdate(new DateTime((Long.parseLong(groups[2]))));
            // 组合消息，发送延迟消息处理通话记录
            handler.sendMessageDelayed(handler.obtainMessage(100, 1, 0, causeCache), TimeUnit.SECONDS.toMillis(4));
        } catch (Exception e) {
            Log.w(TAG, "无法获取日志输出", e);
        }
        super.onDestroy();
    }

    /**
     * 获取当前进程ID
     *
     * @return
     */
    public int pid(Process process) {
        // Java 通过反射获取
        if (process.getClass().getName().matches("java\\.lang\\.UNIXProcess")
                || process.getClass().getName().matches("java\\.lang\\.ProcessManager\\$ProcessImpl")) {
            // Unix 获取进程ID
            try {
                java.lang.reflect.Field f = process.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                return Integer.valueOf(f.get(process).toString());
            } catch (Exception e) {
            }
        }
        return -1;
    }
}
