package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * Created by Xiaohong on 2014/5/10.
 */
@DatabaseTable(tableName = "pq_app_traffic")
public class AppTraffic {
    @DatabaseField(generatedId = true, dataType = DataType.LONG)
    private long id;
    /** 应用编号 */
    @DatabaseField
    private int uid;
    /** 应用名 */
    @DatabaseField
    private String appName;
    /** 包名 */
    @DatabaseField(unique = true)
    private String packageName;
    /** 是否为系统应用 */
    @DatabaseField
    private boolean system;

    @DatabaseField
    private long mobileRxBytes;
    @DatabaseField
    private long mobileTxBytes;

    @DatabaseField
    private long wifiRxBytes;
    @DatabaseField
    private long wifiTxBytes;

    @DatabaseField
    private long rxBytes;
    @DatabaseField
    private long txBytes;

    /** 统计更新时间 */
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime ddate;

    public AppTraffic() {
    }

    public AppTraffic(int uid, String appName, String packageName, DateTime ddate) {
        this.uid = uid;
        this.appName = appName;
        this.packageName = packageName;
        this.ddate = ddate;
    }

    public AppTraffic(int uid, String appName, String packageName, long rxBytes, long txBytes, DateTime ddate) {
        this.uid = uid;
        this.appName = appName;
        this.packageName = packageName;
        this.rxBytes = rxBytes;
        this.txBytes = txBytes;
        this.ddate = ddate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public long getMobileRxBytes() {
        return mobileRxBytes;
    }

    public void setMobileRxBytes(long mobileRxBytes) {
        this.mobileRxBytes = mobileRxBytes;
    }

    public long getMobileTxBytes() {
        return mobileTxBytes;
    }

    public void setMobileTxBytes(long mobileTxBytes) {
        this.mobileTxBytes = mobileTxBytes;
    }

    public long getWifiRxBytes() {
        return wifiRxBytes;
    }

    public void setWifiRxBytes(long wifiRxBytes) {
        this.wifiRxBytes = wifiRxBytes;
    }

    public long getWifiTxBytes() {
        return wifiTxBytes;
    }

    public void setWifiTxBytes(long wifiTxBytes) {
        this.wifiTxBytes = wifiTxBytes;
    }

    public long getRxBytes() {
        return rxBytes;
    }

    public void setRxBytes(long rxBytes) {
        this.rxBytes = rxBytes;
    }

    public long getTxBytes() {
        return txBytes;
    }

    public void setTxBytes(long txBytes) {
        this.txBytes = txBytes;
    }

    public DateTime getDdate() {
        return ddate;
    }

    public void setDdate(DateTime ddate) {
        this.ddate = ddate;
    }
}
