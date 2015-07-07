/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cattsoft.phone.quality.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.baidu.location.BDLocation;
import com.cattsoft.phone.quality.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.StringWriter;

import static com.cattsoft.phone.quality.utils.LogUtils.LOGE;
import static com.cattsoft.phone.quality.utils.LogUtils.makeLogTag;

/**
 * Utilities and constants related to app preferences.
 */
public class PrefUtils {
    private static final String TAG = makeLogTag("PrefUtils");

    /** Long indicating when a sync was last ATTEMPTED (not necessarily succeeded) */
    public static final String PREF_LAST_SYNC_ATTEMPTED = "pref_last_sync_attempted";

    /** Long indicating when a sync last SUCCEEDED */
    public static final String PREF_LAST_SYNC_SUCCEEDED = "pref_last_sync_succeeded";

    public static final String PREF_BANDWIDTH_TEST_URL = "pref_bandwidth_test_url";
    /** 测速服务器自动获取 */
    public static final String PREF_SPEED_SERVER_AUTO = "pref_speed_server_auto";

    /**
     * Boolean indicating whether we performed the (one-time) welcome flow.
     */
    public static final String PREF_WELCOME_DONE = "pref_welcome_done";

    /**
     * 基站信息
     */
    public static final String PREF_BASE_CELL_LAC = "pref_base_cell_lac";
    public static final String PREF_BASE_CELL_CID = "pref_base_cell_cid";
    public static final String PREF_BASE_CELL_ASU = "pref_base_cell_asu";

    /**
     * 位置信息
     */
    public static final String PREF_LOCATION_LATITUDE = "pref_location_latitude";
    public static final String PREF_LOCATION_LONGITUDE = "pref_location_longitude";
    public static final String PREF_LOCATION_ADDRESS = "pref_location_address";
    public static final String PREF_LOCATION_PROVINCE = "pref_location_province";
    public static final String PREF_LOCATION_CITY = "pref_location_city";
    public static final String PREF_LOCATION_DISTRICT = "pref_location_district";

    /**
     * 测速服务器
     */
    public static final String PREF_SERVER_ID = "pref_server_id";
    public static final String PREF_SERVER_CITIES = "pref_server_cities";
    public static final String PREF_SERVER_NAME = "pref_server_name";
    public static final String PREF_SERVER_IP = "pref_server_ip";
    public static final String PREF_SERVER_PORT = "pref_server_port";
    public static final String PREF_SERVER_LIST = "pref_server_list";


    public static void init(final Context context) {

    }

    public static boolean isWelcomeDone(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_WELCOME_DONE, false);
    }

    public static void markWelcomeDone(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_WELCOME_DONE, true).commit();
    }

    public static long getLastSyncAttemptedTime(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(PREF_LAST_SYNC_ATTEMPTED, 0L);
    }

    public static void markSyncAttemptedNow(final Context context) {
    }

    public static void markLastLocation(final Context context, final BDLocation location) {
        // 保存位置信息
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREF_LOCATION_LONGITUDE, Double.toString(location.getLongitude()))
                .putString(PREF_LOCATION_LATITUDE, Double.toString(location.getLatitude())).apply();
        if(location.hasAddr()) {
            editor.putString(PREF_LOCATION_PROVINCE, location.getProvince())
                    .putString(PREF_LOCATION_CITY, location.getCity())
                    .putString(PREF_LOCATION_DISTRICT, location.getDistrict())
                    .putString(PREF_LOCATION_ADDRESS, location.getAddrStr()).apply();
        }
        editor.commit();
    }

    public static LastLocation getLastLocation(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        // 经纬度
        LastLocation location = new LastLocation(Double.parseDouble(sp.getString(PREF_LOCATION_LONGITUDE, "-1")),
                Double.parseDouble(sp.getString(PREF_LOCATION_LATITUDE, "-1")));
        // 位置
        location.setProvince(sp.getString(PREF_LOCATION_PROVINCE, ""));
        location.setCity(sp.getString(PREF_LOCATION_CITY, ""));
        location.setDistrict(sp.getString(PREF_LOCATION_DISTRICT, ""));
        location.setAddress(sp.getString(PREF_LOCATION_ADDRESS, ""));
        return location;
    }

    public static void markBaseCellInfo(final Context context, int lac, int cid) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(PREF_BASE_CELL_LAC, lac).putInt(PREF_BASE_CELL_CID, cid).commit();
    }

    public static void markBaseCellAsu(final Context context, int asu) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(PREF_BASE_CELL_ASU, asu).commit();
    }

    public static int getLastBaseCellAsu(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(PREF_BASE_CELL_ASU, -1);
    }

    public static int[] getLastBaseCellInfo(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return new int[] {sp.getInt(PREF_BASE_CELL_LAC, -1), sp.getInt(PREF_BASE_CELL_CID, -1)};
    }

    public static void markSpeedServerAutoSelect(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_SPEED_SERVER_AUTO, true).commit();
    }

    public static void disableSpeedServerAutoSelect(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_SPEED_SERVER_AUTO, false).commit();
    }

    public static boolean isSpeedServerAutoSelect(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_SPEED_SERVER_AUTO, false);
    }

    public static void setBandWidthUrl(final Context context, String url) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_BANDWIDTH_TEST_URL, url).commit();
    }

    public static String getBandWidthUrl(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_BANDWIDTH_TEST_URL, context.getString(R.string.speed_bandwidth_url));
    }

    public static long getLastSyncSucceededTime(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(PREF_LAST_SYNC_SUCCEEDED, 0L);
    }

    public static void registerOnSharedPreferenceChangeListener(final Context context,
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnSharedPreferenceChangeListener(final Context context,
                                                                  SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static SpeedServer getSpeedServer(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String ip = sp.getString(PREF_SERVER_IP, null);
        if(Strings.isNullOrEmpty(ip))
            return saveSpeedServer(context, new SpeedServer(1503, "南京", "南京电信", "218.2.122.246", 8092));
        return new SpeedServer(sp.getInt(PREF_SERVER_ID, 0), sp.getString(PREF_SERVER_CITIES, ""), sp.getString(PREF_SERVER_NAME, ""), ip, sp.getInt(PREF_SERVER_PORT, 8080));
    }

    public static SpeedServer saveSpeedServer(Context context, SpeedServer server) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(PREF_SERVER_ID, server.getId())
                .putString(PREF_SERVER_CITIES, server.getCities())
                .putString(PREF_SERVER_NAME, server.getName())
                .putString(PREF_SERVER_IP, server.getServer())
                .putInt(PREF_SERVER_PORT, server.getPort())
                .commit();
        return server;
    }

    public static void markSpeedServerList(final Context context, JSONArray array) {
        File file = new File(context.getFilesDir(), "servers.json");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(array.toString().getBytes());
            fos.flush();
        } catch (Exception e) {
            LOGE(TAG, "服务器列表信息保存失败", e);
        } finally {
            try {
                if(null != fos)
                    fos.close();
            } catch (Exception e) {}
        }
    }

    public static SpeedServer[] getSpeedServerList(final Context context) {
        File file = new File(context.getFilesDir(), "servers.json");
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            StringWriter writer = new StringWriter();
            char[] chars = new char[1024];
            int len = -1;
            while((len = reader.read(chars)) != -1)
                writer.write(chars, 0, len);
            JSONArray array = new JSONArray(writer.toString());
            // 转换
            SpeedServer[] servers = new SpeedServer[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                servers[i] = new SpeedServer(json.getInt("hostid"), json.getString("location"),
                        json.getString("hostname"), json.getString("ip"), json.getInt("port"));
            }
            return servers;
        } catch (Exception e) {
            LOGE(TAG, "服务器列表信息读取失败", e);
        } finally {
            try {
                if(null != reader)
                    reader.close();
            } catch (Exception e) {}
        }
        return new SpeedServer[0];
    }
}
