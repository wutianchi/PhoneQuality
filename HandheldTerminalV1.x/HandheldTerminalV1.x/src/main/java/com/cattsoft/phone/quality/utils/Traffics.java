package com.cattsoft.phone.quality.utils;

import android.net.TrafficStats;

/**
 * 流量统计.
 * Created by Xiaohong on 2014/5/3.
 */
public class Traffics {
    public static long getNetBytes(MobileNetType.NetWorkType networkType, long def) {
        if (networkType == MobileNetType.NetWorkType.TYPE_3G
                || networkType == MobileNetType.NetWorkType.TYPE_4G
                || networkType == MobileNetType.NetWorkType.TYPE_2G
                || networkType == MobileNetType.NetWorkType.TYPE_WAP) {
            // Mobile
            return TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes();
        } else if (networkType == MobileNetType.NetWorkType.TYPE_WIFI) {
            // WiFi
            long total = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
            return total - TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes();
        }
        return def;
    }

    public static long getNetBytes() {
        return TrafficStats.getTotalRxBytes() + TrafficStats.getMobileTxBytes();
    }
}
