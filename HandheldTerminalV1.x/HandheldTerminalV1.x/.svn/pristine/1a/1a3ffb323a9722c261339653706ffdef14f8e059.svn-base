package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * 网络活动统计.
 * Created by Xiaohong on 2014/5/3.
 */
@DatabaseTable(tableName = "pq_network_activity")
public class NetworkActivity {
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
    /** 流量消耗情况 bytes */
    @DatabaseField
    private long traffic;

    public NetworkActivity() {
        //
    }

    /**
     * @param phoneType   手机制式
     * @param mobileType  移动网络类型
     * @param networkType 数据网络类型
     * @param activate    网络激活时间
     */
    public NetworkActivity(int phoneType, int mobileType, int networkType, DateTime activate) {
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

    public long getTraffic() {
        return traffic;
    }

    public void setTraffic(long traffic) {
        this.traffic = traffic;
    }
}
