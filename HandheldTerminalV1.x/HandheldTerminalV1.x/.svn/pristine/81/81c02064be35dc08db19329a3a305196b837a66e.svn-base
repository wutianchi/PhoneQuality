package com.cattsoft.phone.quality.service.runnable;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.GeoLocation;
import com.cattsoft.phone.quality.model.SpeedTarget;
import com.cattsoft.phone.quality.utils.DatabaseHelper;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.cattsoft.phone.quality.utils.Numbers;
import com.cattsoft.phone.quality.utils.Traffics;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 网络速率计算.
 * Created by Xiaohong on 2014/5/3.
 */
public class NetSpeedRunnable implements Runnable {
    private static final String TAG = "speed";
    private Context context;

    /** 是否连续的 */
    private boolean consequent;
    /** 连续流量指标最大连续值 */
    private int limit;
    /** 最小流量指标数量 */
    private int tlimit;

    private DateTime activate;

    private List<Long> targets = new LinkedList<Long>();

    private Handler handler = new Handler();

    /** 当前已消耗的流量 */
    private long last;

    public NetSpeedRunnable(Context context) {
        this.context = context;
        last = Traffics.getNetBytes();
        limit = context.getResources().getInteger(R.integer.speed_consequent_limit);
        tlimit = context.getResources().getInteger(R.integer.speed_consequent_targets);
    }

    public void start() {
        last = Traffics.getNetBytes();
        handler.postDelayed(this, TimeUnit.SECONDS.toMillis(1));
    }

    public void stop() {
        handler.removeCallbacks(this);
        targets.clear();
    }

    @Override
    public void run() {
        // 每秒统计
        handler.postDelayed(this, TimeUnit.SECONDS.toMillis(1));
        long traffic = Traffics.getNetBytes();
        long bytes = traffic - last;
        double kbs = bytes / 1024.0f;
        double kbps = kbs * 8d;
        if ((consequent = bytes > 0)) targets.add(bytes);
        if ((consequent && targets.size() >= limit || (!consequent && targets.size() > tlimit))) {
            // 统计流量指标
            DatabaseHelper databaseHelper = QualityApplication.getApplication(context).getDatabaseHelper();
            GeoLocation location = null;
            try {
                location = databaseHelper.getGeoLocationDAO().queryForFirst(databaseHelper.getGeoLocationDAO().queryBuilder().orderBy("ddate", false).prepare());
            } catch (Exception e) {
            }
            try {
                long duration = new Duration(activate, DateTime.now()).getStandardSeconds();
                List<Long> _targets = targets.subList(3, targets.size() - 2);
                databaseHelper.getSpeedTargetDAO().create(new SpeedTarget(MobileNetType.getPhoneType(context),
                        MobileNetType.getMobileTypeValue(context), MobileNetType.getNetWorkType(context).type,
                        _targets.size(), Numbers.sumLong(_targets), duration, DateTime.now(), location));
            } catch (Exception e) {
                Log.w(TAG, "无法记录连续流量指标数据", e);
            }
            targets.clear();
            activate = DateTime.now();
        }
        if (!consequent) {
            targets.clear();
            activate = DateTime.now();
        }
        last = traffic;
    }
}
