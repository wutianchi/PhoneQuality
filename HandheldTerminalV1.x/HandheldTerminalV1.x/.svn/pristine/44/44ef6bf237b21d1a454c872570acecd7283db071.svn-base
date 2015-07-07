package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * 网络速率指标.
 * Created by Xiaohong on 2014/5/11.
 */
@DatabaseTable(tableName = "pq_speed_target")
public class SpeedTarget {
    /** 位置信息 */
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    public GeoLocation location;
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
    /** 指标长度 */
    @DatabaseField
    private int size;
    /** 流量统计 */
    @DatabaseField
    private long traffics;
    /** 指标统计持续时长（秒） */
    @DatabaseField
    private long duration;
    /** 指标记录时间 */
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime ddate;

    public SpeedTarget() {
    }

    public SpeedTarget(int phoneType, int mobileType, int networkType, int size, long traffics, DateTime ddate) {
        this.phoneType = phoneType;
        this.mobileType = mobileType;
        this.networkType = networkType;
        this.size = size;
        this.traffics = traffics;
        this.ddate = ddate;
    }

    public SpeedTarget(int phoneType, int mobileType, int networkType, int size, long traffics, long duration, DateTime ddate, GeoLocation location) {
        this.phoneType = phoneType;
        this.mobileType = mobileType;
        this.networkType = networkType;
        this.size = size;
        this.traffics = traffics;
        this.duration = duration;
        this.ddate = ddate;
        this.location = location;
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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTraffics() {
        return traffics;
    }

    public void setTraffics(long traffics) {
        this.traffics = traffics;
    }

    public DateTime getDdate() {
        return ddate;
    }

    public void setDdate(DateTime ddate) {
        this.ddate = ddate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }
}
