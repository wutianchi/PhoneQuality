package com.cattsoft.phone.quality.service.receiver;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.model.AppTraffic;
import com.cattsoft.phone.quality.utils.Numbers;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import org.joda.time.DateTime;
import roboguice.receiver.RoboBroadcastReceiver;

/**
 * 应用更改广播接收.
 * Created by Xiaohong on 2014/5/9.
 */
public class AppChangedReceiver extends RoboBroadcastReceiver {
    private static final String TAG = "app";

    @Override
    protected void handleReceive(Context context, Intent intent) {
        super.handleReceive(context, intent);
        PackageManager pm = context.getPackageManager();
        String packageName = intent.getData().getSchemeSpecificPart();
        RuntimeExceptionDao<AppTraffic, Long> dao = QualityApplication.getApplication(context).getDatabaseHelper().getAppTrafficDAO();
        if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction()) ||
                "android.intent.action.PACKAGE_REPLACED".equals(intent.getAction())) {

            // 安装/替换 应用
            try {
                PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.SIGNATURE_MATCH);
                int uid = packageInfo.applicationInfo.uid;
                String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                boolean system = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                if (PackageManager.PERMISSION_GRANTED == pm.checkPermission(Manifest.permission.INTERNET, packageInfo.packageName)) {
                    if (null == dao.queryForFirst(dao.queryBuilder().where().eq("packageName", packageName).prepare()))
                        dao.create(new AppTraffic(uid, appName, packageName, Numbers.getNegativeZero(TrafficStats.getUidRxBytes(uid)),
                                Numbers.getNegativeZero(TrafficStats.getUidTxBytes(uid)), DateTime.now()));
                }
            } catch (Exception e) {
                Log.d(TAG, "获取应用信息时出现异常", e);
            }
        } else if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
            // 卸载
            try {
                DeleteBuilder<AppTraffic, Long> preparedDelete = dao.deleteBuilder();
                preparedDelete.where().eq("packageName", packageName);
                preparedDelete.delete();
            } catch (Exception e) {
                Log.w(TAG, "无法删除应用流量统计记录:" + packageName, e);
            }
        }
    }
}
