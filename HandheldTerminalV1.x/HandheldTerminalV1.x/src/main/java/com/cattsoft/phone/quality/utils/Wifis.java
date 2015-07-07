package com.cattsoft.phone.quality.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Xiaohong on 14-1-8.
 */
public class Wifis {

    private final static ArrayList<Integer> channelsFrequency = new ArrayList<Integer>(
            Arrays.asList(0, 2412, 2417, 2422, 2427, 2432, 2437, 2442, 2447,
                    2452, 2457, 2462, 2467, 2472, 2484));

    public static Integer getFrequencyFromChannel(int channel) {
        return channelsFrequency.get(channel);
    }

    public static int getChannelFromFrequency(int frequency) {
        return channelsFrequency.indexOf(Integer.valueOf(frequency));
    }

    /**
     * 创建WiFi信道图表数据
     *
     * @param channel 信道
     * @param dBm     信号强度
     * @return
     */
    public static double[] getChannelValues(int channel, int dBm) {
        double[] vals = new double[18 * 10];
        for (int i = 0; i < vals.length; i++) {
            vals[i] = -10;
        }

        int step = 5;
        int count = 200 / step;

        List<Double> list = new ArrayList<Double>();
        for (int i = 0; i < count; i++) {
            int angle = i * step;

            double rAngle = Math.toRadians(angle);
            double v = new BigDecimal(Math.sin(rAngle) * dBm).setScale(1, BigDecimal.ROUND_FLOOR).doubleValue();

            list.add(v < 0 ? 0 : v);
        }

        list.add(18, dBm - 0.01);
        list.add(20, dBm - 0.01);
        list.add(20, Double.parseDouble(Integer.toString(dBm)) - 0.01);

        list.remove(list.size() - 1);
        list.remove(list.size() - 1);
        list.remove(list.size() - 2);

        for (int i = 0; i < list.size(); i++)
            vals[i + ((channel - 1 > 0 ? channel - 1 : 0) * 10)] = list.get(i);

        return vals;
    }

    public static int[] getColors(int[] values, int size) {
        List<Integer> colors = new ArrayList<Integer>();

        if (values.length < size) {
            for (int v : values)
                colors.add(v);

            int index = 0;
            for (int i = values.length; i < size; i++) {
                index = index >= values.length ? 0 : index;

                colors.add(values[index]);

                index++;
            }

            values = new int[size];
            for (int i = 0; i < size; i++)
                values[i] = colors.get(i);
        }

        return values;
    }

//    public static int getSecurity(WifiConfiguration config) {
//        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
//            return SECURITY_PSK;
//        }
//        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) ||
//                config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
//            return SECURITY_EAP;
//        }
//        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
//    }
//
//    public static int getSecurity(ScanResult result) {
//        if (result.capabilities.contains("WEP")) {
//            return SECURITY_WEP;
//        } else if (result.capabilities.contains("PSK")) {
//            return SECURITY_PSK;
//        } else if (result.capabilities.contains("EAP")) {
//            return SECURITY_EAP;
//        }
//        return SECURITY_NONE;
//    }
}
