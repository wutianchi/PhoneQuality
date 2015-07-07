package com.cattsoft.phone.quality;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.cattsoft.phone.quality.model.MetaData;
import com.cattsoft.phone.quality.service.BaiduLocationService;
import com.cattsoft.phone.quality.service.PhoneStateService;
import com.cattsoft.phone.quality.service.TrafficStatsService;
import com.cattsoft.phone.quality.task.DeviceAuthTask;
import com.cattsoft.phone.quality.utils.DatabaseHelper;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import org.joda.time.DateTime;
import roboguice.RoboGuice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Xiaohong on 2014/5/1.
 */
public class QualityApplication extends Application {
    /** 应用程序版本标记 */
    public static final String VERSION_KEY = "app.lastVersion";
    public static final String VERSION_NAME_KEY = "app.lastVersion_name";
    public static final String VERSION_DATABASE_KEY = "database.version";
    public static final int RUN_IN_UI_CODE = 30003;
    private static final String TAG = "application";
    @Inject
    SharedPreferences sharedPreferences;

    private volatile DatabaseHelper databaseHelper = null;

    private ExecutorService executorService;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case RUN_IN_UI_CODE:
                    try {
                        ((Callback) msg.obj).call();
                    } catch (Exception e) {
                        Log.w(TAG, "函数回调出现异常", e);
                    }
                    break;
            }
            return false;
        }
    });

    /**
     * 提交任务运行}
     *
     * @param r       线程Runnable{@link java.lang.Runnable
     * @param context
     */
    public static void runInApplication(Runnable r, Context context) {
        if (null == r || null == context)
            return;
        getApplication(context).executorService.submit(r);
    }

    public static void runInUi(final Callback callback, final Context context) {
        getApplication(context).executorService.submit(new Runnable() {
            @Override
            public void run() {
                getApplication(context).handler().sendMessage(getApplication(context).handler().obtainMessage(RUN_IN_UI_CODE, callback));
            }
        });
    }

    public static QualityApplication getApplication(Context context) {
        Context c = context.getApplicationContext();
        if (c instanceof QualityApplication)
            return (QualityApplication) c;
        else
            return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RoboGuice.getBaseApplicationInjector(this).injectMembers(this);
        OpenHelperManager.setOpenHelperClass(DatabaseHelper.class);
        // 创建线程池供界面调用
        executorService = Executors.newSingleThreadExecutor();
        int currentVersion = 0;
        String currentVersionName = "";
        int lastVersion = sharedPreferences.getInt(VERSION_KEY, -1); // 最后记录的版本号
        int dbVersion = sharedPreferences.getInt(VERSION_DATABASE_KEY, 0);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.SIGNATURE_MATCH);
            currentVersion = info.versionCode;  // 当前版本号
            currentVersionName = info.versionName;
        } catch (Exception e) {
        }
        // 版本号判断
        if (currentVersion > lastVersion || dbVersion < getResources().getInteger(R.integer.database_version)) {
            //如果当前版本大于上次版本，该版本属于第一次启动
            //将当前版本写入preference中，则下次启动的时候，据此判断，不再为首次启动
            if (currentVersion > lastVersion) {
                sharedPreferences.edit()
                        .putInt(VERSION_KEY, currentVersion)
                        .putString(VERSION_NAME_KEY, currentVersionName)
                        .commit();
                // 当前版本第一次启动
            }
            // 版本升级，更新数据库
            sharedPreferences.edit().putInt(VERSION_DATABASE_KEY, getResources().getInteger(R.integer.database_version)).commit();
            try {
                getDatabaseHelper().getMetaDataDAO().deleteBuilder().delete();
            } catch (Exception e) {
            }
            try {
                getDatabaseHelper().getMetaDataDAO().create(new MetaData(VERSION_KEY, Integer.toString(currentVersion)));
                getDatabaseHelper().getMetaDataDAO().create(new MetaData(VERSION_NAME_KEY, currentVersionName));
            } catch (Exception e) {
            }
        }
        if (lastVersion < 0) {
            // 没找到版本号，第一次安装后启动，初始化默认值
            sharedPreferences.edit()
                    .putLong("app_install_date", DateTime.now().getMillis())
                    .putLong("mobileRxBytes", TrafficStats.getMobileRxBytes())
                    .putLong("mobileTxBytes", TrafficStats.getMobileTxBytes())
                    .putLong("wifiRxBytes", TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes())
                    .putLong("wifiTxBytes", TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes())
                    .commit();
            Intent listAppIntent = new Intent(this, TrafficStatsService.class);
            listAppIntent.putExtra("flag", TrafficStatsService.REQUEST_CODE_LIST_APP);
            startService(listAppIntent);    // 请求获取已安装的应用信息
        }
        try {
            if (null == getDatabaseHelper().getMetaDataDAO().queryForId("oauth_verifier"))
                new DeviceAuthTask(this, null).execute();
        } catch (Exception e) {
            Log.d(TAG, "无法查询数据，目前无法与服务器通信");
        }
        // 启动后台服务
        Intent service = new Intent(this, PhoneStateService.class);
        service.putExtra("flag", 1);
        startService(service);
        try {
            startService(new Intent(this, BaiduLocationService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getSession() {
        return null;
    }

    public Handler handler() {
        return this.handler;
    }

    public DatabaseHelper getDatabaseHelper() {
        if (null == databaseHelper)
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        return databaseHelper;
    }

    /**
     * 释放数据库连接
     */
    public void releaseDatabaseHelper() {
        if (null != databaseHelper) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    public void onTerminate() {
        releaseDatabaseHelper();
        try {
            executorService.shutdownNow();
        } catch (Exception e) {
        }
        super.onTerminate();
    }

    public interface Callback {
        void call();
    }
}
