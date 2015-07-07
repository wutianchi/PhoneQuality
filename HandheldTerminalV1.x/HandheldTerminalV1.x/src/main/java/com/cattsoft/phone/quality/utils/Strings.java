package com.cattsoft.phone.quality.utils;

/**
 * Created by Xiaohong on 2015/3/23.
 */
public class Strings {
    public static String nullToEmpty(String string) {
        return (string == null) ? "" : string;
    }

    public static String emptyToNull(String string) {
        return isNullOrEmpty(string) ? null : string;
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.length() == 0;
    }
}
