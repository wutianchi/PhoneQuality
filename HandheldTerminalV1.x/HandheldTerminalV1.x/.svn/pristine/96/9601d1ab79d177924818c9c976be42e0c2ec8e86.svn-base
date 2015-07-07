package com.cattsoft.phone.quality.utils;

/**
 * Created by Xiaohong on 2014/5/26.
 */
public class Speeds {
    /**
     * 转换速率单位显示
     *
     * @param bytes 字节数
     * @param si    是否格式化为bit
     * @param unit  格式化单位：%.1f %sb/s
     * @return
     */
    public static String humanReadableByteCount(double bytes, boolean si, String unit) {
        int kb = 1024;
        if (bytes < kb) return bytes + (si ? " b" : " B");
        int exp = (int) (Math.log(bytes * (si ? 8 : 1)) / Math.log(kb));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format(null == unit ? "%.1f %s" + (si ? "b" : "B") : unit, bytes / Math.pow(kb, exp) * (si ? 8 : 1), pre);
    }
}
