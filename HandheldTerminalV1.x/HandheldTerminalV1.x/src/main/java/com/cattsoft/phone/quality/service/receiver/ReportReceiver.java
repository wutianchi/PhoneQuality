package com.cattsoft.phone.quality.service.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.*;
import com.cattsoft.phone.quality.service.ReportService;
import com.cattsoft.phone.quality.utils.DatabaseHelper;
import com.cattsoft.phone.quality.utils.Devices;
import com.cattsoft.phone.quality.utils.MobileNetType;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DataType;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.zeroturnaround.zip.ZipUtil;
import roboguice.inject.InjectResource;
import roboguice.receiver.RoboBroadcastReceiver;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 报文生成广播接收.
 * Created by Xiaohong on 2014/5/1.
 */
public class ReportReceiver extends RoboBroadcastReceiver {
    public static final String REPORT_ACTION = "com.cattsoft.phone.REPORT_ACTION";
    public static final String REPORT_CALL_ACTION = "com.cattsoft.phone.REPORT_CALL_ACTION";
    public static final String REPORT_SMS_ACTION = "com.cattsoft.phone.REPORT_SMS_ACTION";
    public static final String REPORT_UPLOAD_ACTION = "com.cattsoft.phone.REPORT_UPLOAD_ACTION";
    public static final int REQUEST_CODE_REPORT = 100;
    public static final int REQUEST_CODE_CALL = 101;
    public static final int REQUEST_CODE_SMS = 102;
    public static final String LINE_SEPARATOR = "\n";
    private static final String TAG = "report";
    @Inject
    SharedPreferences sharedPreferences;
    private Context context;
    private Map<String, String> dictionary = new HashMap<String, String>();
    /** 数据文件编码 */
    @InjectResource(R.string.report_file_encoding)
    private String encoding;

    @Override
    protected void handleReceive(Context context, Intent intent) {
        super.handleReceive(context, intent);
        this.context = context;
        String action = intent.getAction();
        try {
            if (REPORT_UPLOAD_ACTION.equals(action)) {
                // 数据上传
                upload(intent.getIntExtra("flag", 1));
            } else {
                initDictionary();
                if (REPORT_ACTION.equals(action)) {
                    // 生成wifi、网络速率、基站信息报文、上传数据
                    reportWifi();
                    reportSpeed();
                    reportCells();
                } else if (REPORT_CALL_ACTION.equals(action)) {
                    // 生成掉话率报文
                    reportDropCall();
                } else if (REPORT_SMS_ACTION.equals(action)) {
                    // 生成短信失败率报文
                    reportSms();
                }
                upload(1);
            }
        } catch (Exception e) {
            Log.e(TAG, "无法获取报文数据！", e);
        }
    }

    private void initDictionary() {
        dictionary.put("ln", "\n");
        dictionary.put("datetime", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        dictionary.put("timestamp", DateTime.now().toString("yyyyMMddHHmmssSSSS"));
        String oauth_verifier = Devices.getSerial(context);
        try {
            DatabaseHelper databaseHelper = QualityApplication.getApplication(context).getDatabaseHelper();
            // 设备认证号
            oauth_verifier = databaseHelper.getMetaDataDAO().queryForId("oauth_verifier").getValue();
        } catch (Exception e) {
        }
        dictionary.put("uid", oauth_verifier);
        dictionary.put("operator", Devices.getOperator(context));
        dictionary.put("operatorCode", Devices.getOperatorCode(context));
        dictionary.put("mnc", Devices.getMnc(context));
        dictionary.put("mcc", Devices.getMcc(context));
        dictionary.put("phoneType", Integer.toString(MobileNetType.getPhoneType(context)));
        dictionary.put("mobileType", Integer.toString(MobileNetType.getMobileTypeValue(context)));
        // 位置信息
        try {
            GeoLocation geoLocation = QualityApplication.getApplication(context).getDatabaseHelper().getGeoLocationDAO()
                    .queryForFirst(QualityApplication.getApplication(context).getDatabaseHelper().getGeoLocationDAO().queryBuilder().orderBy("ddate", false).prepare());
            dictionary.put("latitude", Double.toString(geoLocation.getLatitude()));
            dictionary.put("longitude", Double.toString(geoLocation.getLongitude()));
            dictionary.put("province", geoLocation.getProvince());
            dictionary.put("city", geoLocation.getCity());
            dictionary.put("cityCode", geoLocation.getCityCode());
            dictionary.put("district", geoLocation.getDistrict());
            dictionary.put("address", geoLocation.getAddress());
        } catch (Exception e) {
            Log.e(TAG, "没有位置信息，报文将无法设置位置信息!");
        }
    }

    /** 掉话率报文 */
    private void reportDropCall() {
        long start = sharedPreferences.getLong("report.call", sharedPreferences.getLong("app_install_date", 0));
        long end = DateTime.now().getMillis();
        List<Object[]> results = null;
        GenericRawResults<Object[]> genericRawResults = null;
        try {
            RuntimeExceptionDao<CallsStructure, Long> dao = QualityApplication.getApplication(context).getDatabaseHelper().getCallsStructureDAO();
            genericRawResults = dao.queryRaw("SELECT CAUSE cause, TYPE type, COUNT(1) count FROM PQ_CALL_STRUCTURE GROUP BY TYPE, CAUSE HAVING DDATE BETWEEN ? AND ?",
                    new DataType[]{DataType.STRING, DataType.INTEGER, DataType.INTEGER},
                    Long.toString(start), Long.toString(end));
            results = genericRawResults.getResults();
        } catch (Exception e) {
            Log.w(TAG, "掉话率数据查询失败");
        } finally {
            try {
                genericRawResults.close();
            } catch (Exception e) {
            }
        }
        if (null == results || results.size() == 0)
            return;
        List<Map<String, String>> maps = new LinkedList<Map<String, String>>();
        for (Object[] values : results) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("cause", (String) values[0]);
            map.put("ctype", Integer.toString((Integer) values[1]));
            map.put("count", Integer.toString((Integer) values[2]));
            map.put("startTime", new DateTime(start).toString("yyyy-MM-dd HH:mm:ss"));
            map.put("endTime", new DateTime(end).toString("yyyy-MM-dd HH:mm:ss"));
            maps.add(map);
        }
        if (zip(context.getString(R.string.report_type_call), context.getString(R.string.report_format_calls), maps))
            sharedPreferences.edit().putLong("report.call", end).apply();
    }

    /**
     * 短信报文
     */
    private void reportSms() {
        long start = sharedPreferences.getLong("report.sms", sharedPreferences.getLong("app_install_date", 0));
        long end = DateTime.now().getMillis();
        GenericRawResults<Object[]> genericRawResults = null;
        List<Object[]> results = null;
        try {
            RuntimeExceptionDao<SmsStructure, Long> dao = QualityApplication.getApplication(context).getDatabaseHelper().getSmsStructureDAO();
            genericRawResults = dao.queryRaw("SELECT TYPE type, COUNT(1) count FROM PQ_SMS_STRUCTURE GROUP BY TYPE HAVING DDATE BETWEEN ? AND ?",
                    new DataType[]{DataType.INTEGER, DataType.INTEGER},
                    Long.toString(start), Long.toString(end));
            results = genericRawResults.getResults();
        } catch (Exception e) {
            Log.w(TAG, "短信失败率数据查询失败");
        } finally {
            try {
                genericRawResults.close();
            } catch (Exception e) {
            }
        }
        if (null == results || results.size() == 0)
            return;
        List<Map<String, String>> maps = new LinkedList<Map<String, String>>();
        for (Object[] values : results) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("stype", Integer.toString((Integer) values[0]));
            map.put("count", Integer.toString((Integer) values[1]));
            map.put("startTime", new DateTime(start).toString("yyyy-MM-dd HH:mm:ss"));
            map.put("endTime", new DateTime(end).toString("yyyy-MM-dd HH:mm:ss"));
            maps.add(map);
        }
        if (zip(context.getString(R.string.report_type_sms), context.getString(R.string.report_format_sms), maps))
            sharedPreferences.edit().putLong("report.sms", end).apply();
    }

    /**
     * Wifi热点报文
     */
    private void reportWifi() {
        long start = sharedPreferences.getLong("report.wifi", 0);
        long end = DateTime.now().getMillis();
        List<WifiResult> results = null;
        try {
            RuntimeExceptionDao<WifiResult, Long> dao = QualityApplication.getApplication(context).getDatabaseHelper().getWifiResultDAO();
            results = dao.queryBuilder().where().gt("ddate", new DateTime(start)).query();
        } catch (Exception e) {
            Log.d(TAG, "无法查询Wifi热点数据");
        }
        if (null == results || results.size() == 0)
            return;
        List<Map<String, String>> maps = new LinkedList<Map<String, String>>();
        for (WifiResult wifi : results) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("ssid", wifi.getSsid());
            map.put("bssid", wifi.getBssid());
            map.put("capabilities", wifi.getCapabilities());
            map.put("frequency", Integer.toString(wifi.getFrequency()));
            map.put("level", Integer.toString(wifi.getLevel()));
            if (null != wifi.getLocation())
                putLocation(wifi.getLocation(), map);
            maps.add(map);
        }
        if (zip(context.getString(R.string.report_type_wifi), context.getString(R.string.report_format_wifi), maps))
            sharedPreferences.edit().putLong("report.wifi", end).apply();
    }

    /**
     * 网络速率报文
     */
    private void reportSpeed() {
        long start = sharedPreferences.getLong("report.speed", 0);
        long end = DateTime.now().getMillis();
        List<SpeedTarget> results = null;
        try {
            RuntimeExceptionDao<SpeedTarget, Long> dao = QualityApplication.getApplication(context).getDatabaseHelper().getSpeedTargetDAO();
            results = dao.queryBuilder().where().gt("ddate", new DateTime(start)).query();
        } catch (Exception e) {
            Log.d(TAG, "无法查询网络速率指标数据");
        }
        if (null == results || results.size() == 0)
            return;
        List<Map<String, String>> maps = new LinkedList<Map<String, String>>();
        for (SpeedTarget target : results) {
            if (target.getSize() == 0 || target.getDuration() == 0)
                continue;
            Map<String, String> map = new HashMap<String, String>();
            map.put("phoneType", Integer.toString(target.getPhoneType()));
            map.put("mobileType", Integer.toString(target.getMobileType()));
            map.put("networkType", Integer.toString(target.getNetworkType()));
            map.put("speed", Long.toString(target.getTraffics() / target.getSize() / (0 == target.getDuration() ? 1 : target.getDuration())));
            if (null != target.getLocation())
                putLocation(target.getLocation(), map);
            maps.add(map);
        }
        if (zip(context.getString(R.string.report_type_speed), context.getString(R.string.report_format_speed), maps))
            sharedPreferences.edit().putLong("report.speed", end).apply();
    }

    /**
     * 基站数据
     */
    private void reportCells() {
        long start = sharedPreferences.getLong("report.cells", 0);
        long end = DateTime.now().getMillis();
        List<BaseCell> results = null;
        try {
            RuntimeExceptionDao<BaseCell, Long> dao = QualityApplication.getApplication(context).getDatabaseHelper().getBaseCellDAO();
            results = dao.queryBuilder().where().gt("ddate", new DateTime(start)).query();
        } catch (Exception e) {
            Log.d(TAG, "无法查询基站数据");
        }
        if (null == results || results.size() == 0)
            return;
        List<Map<String, String>> maps = new LinkedList<Map<String, String>>();
        for (BaseCell cell : results) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("phoneType", Integer.toString(cell.getPhoneType()));
            map.put("mobileType", Integer.toString(cell.getMobileType()));
            map.put("mnc", cell.getMnc());
            map.put("mcc", cell.getMcc());
            map.put("lac", Integer.toString(cell.getLac()));
            map.put("cid", Integer.toString(cell.getCid()));
            map.put("rssi", Integer.toString(cell.getRssi()));
            if (null != cell.getLocation())
                putLocation(cell.getLocation(), map);
            // 附近的基站
            StringBuilder neighboring = new StringBuilder();
            try {
                cell.getNeighborBaseCells().refreshCollection();
            } catch (Exception e) {
                Log.d(TAG, "获取当前基站邻近基站信息异常", e);
            }
            for (NeighborBaseCell nc : cell.getNeighborBaseCells()) {
                neighboring
                        .append(cell.getMnc()).append(",")
                        .append(cell.getMcc()).append(",")
                        .append(cell.getLac()).append(",")
                        .append(Integer.toString(cell.getCid())).append(",")
                        .append(nc.getLac()).append(",")
                        .append(nc.getCid()).append(",")
                        .append(nc.getPsc()).append(",")
                        .append(nc.getmNetworkType()).append(",")
                        .append(nc.getRssi())
                        .append(LINE_SEPARATOR);
            }

            // 附近的基站字符串
            map.put("neighborings", neighboring.toString().trim());
            maps.add(map);
        }
        if (zip(context.getString(R.string.report_type_cell), context.getString(R.string.report_format_cells), maps))
            sharedPreferences.edit().putLong("report.cells", end).apply();
    }

    private boolean zip(String type, String template, List<Map<String, String>> maps) {
        if (null == maps || maps.size() == 0)
            return false;
        if (Strings.isNullOrEmpty(template))
            return false;
        StringBuffer report = new StringBuffer();
        for (Map<String, String> m : maps) {
            Map<String, String> map = new HashMap<String, String>(dictionary);
            m.put("type", type);    // 数据类型
            map.putAll(m);
            report.append(format(map, template));
        }
        // 文件名称
        Map<String, String> fileDict = new HashMap<String, String>(dictionary);
        fileDict.put("_type", type.toUpperCase());

        try {
            File source = new File(context.getFilesDir(), format(fileDict, context.getString(R.string.report_filename)));
            File zipSource = new File(context.getFilesDir(), format(fileDict, context.getString(R.string.report_filename_zip)));

            Files.write(report, source, Charset.forName(encoding));
            // 压缩文件
            ZipUtil.packEntry(source, zipSource);
            // 删除源文件
            source.delete();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "报文数据写入失败", e);
        }
        return false;
    }

    private void putLocation(GeoLocation location, Map<String, String> map) {
        map.put("latitude", Double.toString(location.getLatitude()));
        map.put("longitude", Double.toString(location.getLongitude()));
        map.put("province", location.getProvince());
        map.put("city", location.getCity());
        map.put("cityCode", location.getCityCode());
        map.put("district", location.getDistrict());
        map.put("address", location.getAddress());
    }

    public String format(Map<String, String> dict, String expression) {
        //根据表达式处理数据
        Matcher matcher = Pattern.compile("\\{(\\w*)\\}").matcher(expression);
        String message = expression;
        try {
            while (matcher.find()) {
                String key = matcher.group(1);
                String macro = matcher.group(0);
                String value = dict.get(key);
                message = message.replace(macro, (null == value) ? "" : value);
            }
        } catch (Exception e) {
            //处理内容表达式时出现错误
            Log.e(TAG, "格式化数据报文时出现异常!", e);
        }
        return message;
    }

    private void upload(int flag) {
        boolean report = false;
        MobileNetType.NetWorkType netType = MobileNetType.getNetWorkType(context);
        if (netType == MobileNetType.NetWorkType.TYPE_WIFI) {
            report = true;
        } else if (netType.isMobile()) {
            long lastReport = sharedPreferences.getLong("lastReport", -1); // 上一次报文上报时间
            if (lastReport == -1) {
                report = true;  //第一次数据报文上传
            } else if (new Duration(new DateTime(lastReport), DateTime.now()).getStandardHours() >= 6) {
                Log.d(TAG, "当前移动网络，距离上次数据上传已过去6小时，数据将使用当前网络上传");
                report = true;
            }
        }
        if (report)
            context.getApplicationContext().startService(new Intent(context.getApplicationContext(), ReportService.class));
    }
}
