package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * 移动网络服务活动统计.
 * Created by Xiaohong on 2014/5/8.
 */
@DatabaseTable(tableName = "pq_service_activity")
public class MobileServiceActivity {
    @DatabaseField(generatedId = true, dataType = DataType.LONG)
    private long id;
    /** 手机制式 GSM、CDMA */
    @DatabaseField
    private int phoneType;
    /** 移动网络类型 HSDPA、GPRS */
    @DatabaseField
    private int mobileType;
    /** 数据网络类型 WIFI、3G、2G */
    @DatabaseField
    private int networkType;
    /** 网络激活时间 */
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime activate;
    /** 网络关闭时间 */
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime deactivate;

    public MobileServiceActivity() {
    }

    public MobileServiceActivity(int phoneType, int mobileType, int networkType, DateTime activate) {
        this.phoneType = phoneType;
        this.mobileType = mobileType;
        this.networkType = networkType;
        this.activate = activate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(int phoneType) {
        this.phoneType = phoneType;
    }

    public int getMobileType() {
        return mobileType;
    }

    public void setMobileType(int mobileType) {
        this.mobileType = mobileType;
    }

    public int getNetworkType() {
        return networkType;
    }

    public void setNetworkType(int networkType) {
        this.networkType = networkType;
    }

    public DateTime getActivate() {
        return activate;
    }

    public void setActivate(DateTime activate) {
        this.activate = activate;
    }

    public DateTime getDeactivate() {
        return deactivate;
    }

    public void setDeactivate(DateTime deactivate) {
        this.deactivate = deactivate;
    }
}
