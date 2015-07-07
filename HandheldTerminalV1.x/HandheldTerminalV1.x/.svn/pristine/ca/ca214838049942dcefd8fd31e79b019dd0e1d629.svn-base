package com.cattsoft.phone.quality.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import roboguice.util.Strings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xiaohong on 14-1-20.
 */
public class MobileNetType {

    /** 手机当前数据网络类型 */
    public static Map<Integer, String> PHONE_NETWORK_TYPE_MAP = new HashMap<Integer, String>() {{
        put(NetWorkType.TYPE_AIRPLANE.type, "AIRPLANE");
        put(TelephonyManager.NETWORK_TYPE_UNKNOWN, "UNKNOW");
        put(TelephonyManager.NETWORK_TYPE_GPRS, "GPRS");
        put(TelephonyManager.NETWORK_TYPE_EDGE, "EDGE");
        put(TelephonyManager.NETWORK_TYPE_UMTS, "UMTS");
        put(TelephonyManager.NETWORK_TYPE_CDMA, "CDMA");
        put(TelephonyManager.NETWORK_TYPE_EVDO_0, "EVDO");
        put(TelephonyManager.NETWORK_TYPE_EVDO_A, "EVDO");
        put(TelephonyManager.NETWORK_TYPE_EVDO_B, "EVDO");
        put(TelephonyManager.NETWORK_TYPE_1xRTT, "1xRTT");
        put(TelephonyManager.NETWORK_TYPE_HSDPA, "HSDPA");
        put(TelephonyManager.NETWORK_TYPE_HSUPA, "HSUPA");
        put(TelephonyManager.NETWORK_TYPE_HSPA, "HSPA");
        put(TelephonyManager.NETWORK_TYPE_HSPAP, "HSPAP");
        put(TelephonyManager.NETWORK_TYPE_IDEN, "IDEN");
        put(TelephonyManager.NETWORK_TYPE_LTE, "LTE");
        put(TelephonyManager.NETWORK_TYPE_EHRPD, "EHRPD");
    }};
    /** 手机制式类型 */
    public static Map<Integer, String> PHONE_TYPE_MAP = new HashMap<Integer, String>() {{
        put(TelephonyManager.PHONE_TYPE_GSM, "GSM");
        put(TelephonyManager.PHONE_TYPE_CDMA, "CDMA");
        put(TelephonyManager.PHONE_TYPE_SIP, "SIP");
        put(TelephonyManager.PHONE_TYPE_NONE, "NONE");
        put(NetWorkType.TYPE_AIRPLANE.type, "AIRPLANE");  // 添加一个飞行模式
    }};

    public static boolean isFastMobileNetwork(Context context) {
        switch (((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    public static boolean is4G(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    /**
     * 获取网络状态，wifi, wap, 2g, 3g, 4g
     *
     * @param context 上下文
     * @return int 网络状态
     */

    public static NetWorkType getNetWorkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        NetWorkType mNetWorkType = NetWorkType.TYPE_INVALID;
        if (networkInfo != null && networkInfo.isConnected()) {
            int netType = networkInfo.getType();

            if (netType == ConnectivityManager.TYPE_WIFI) {
                mNetWorkType = NetWorkType.TYPE_WIFI;
            } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                String proxyHost = android.net.Proxy.getDefaultHost();

                mNetWorkType = TextUtils.isEmpty(proxyHost)
                        ? (isFastMobileNetwork(context) ? (is4G(context) ? NetWorkType.TYPE_4G : NetWorkType.TYPE_3G) : NetWorkType.TYPE_2G)
                        : NetWorkType.TYPE_WAP;
            }
        } else {
            mNetWorkType = NetWorkType.TYPE_INVALID;
        }
        return mNetWorkType;
    }

    public static String getMobileTypeName(Context context) {
        return PHONE_NETWORK_TYPE_MAP.get(getMobileTypeValue(context));
    }

    public static int getMobileTypeValue(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType();
    }

    public static int getPhoneType(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType();
    }

    public static NetWorkType getMobileType(Context context) {
        String proxyHost = android.net.Proxy.getDefaultHost();
        return Strings.isEmpty(proxyHost)
                ? (MobileNetType.isFastMobileNetwork(context) ? (MobileNetType.is4G(context) ? MobileNetType.NetWorkType.TYPE_4G :
                MobileNetType.NetWorkType.TYPE_3G) : MobileNetType.NetWorkType.TYPE_2G)
                : MobileNetType.NetWorkType.TYPE_WAP;
    }

    public static enum NetWorkType implements Serializable {
        TYPE_NONE(-2, "N/A", "N/A"),
        TYPE_AIRPLANE(-1, "N/A", "飞行模式"),
        TYPE_INVALID(0, "N/A", "无网络"),
        TYPE_WAP(1, "WAP", "Wap"),
        TYPE_2G(2, "2G", "2G"),
        TYPE_3G(3, "3G", "3G"),
        TYPE_WIFI(4, "WIFI", "Wi-Fi"),
        TYPE_4G(5, "4G", "4G-Lte");

        public final int type;
        public final String name;
        public final String nickname;

        NetWorkType(int type, String name, String nickname) {
            this.type = type;
            this.name = name;
            this.nickname = nickname;
        }

        public static NetWorkType toType(int type) {
            switch (type) {
                case -2:
                    return TYPE_NONE;
                case -1:
                    return TYPE_AIRPLANE;
                case 1:
                    return TYPE_WAP;
                case 2:
                    return TYPE_2G;
                case 3:
                    return TYPE_3G;
                case 4:
                    return TYPE_WIFI;
                case 5:
                    return TYPE_4G;
                default:
                    return TYPE_INVALID;
            }
        }

        public boolean isMobile() {
            return this != TYPE_INVALID && this != TYPE_WIFI;
        }
    }
}
