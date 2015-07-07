package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * Created by Xiaohong on 2014/5/5.
 */
@DatabaseTable(tableName = "pq_speed_result")
public class SpeedTestResult {
    /** 位置信息 */
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    public GeoLocation location;
    @DatabaseField(generatedId = true, dataType = DataType.LONG)
    private long id;
    /** 手机运营商代码 */
    @DatabaseField
    private int operator;
    /** 手机运营商名称 */
    @DatabaseField
    private String operatorName;
    /** 手机制式 GSM、CDMA */
    @DatabaseField
    private int phoneType;
    /** 移动网络类型 HSDPA、GPRS */
    @DatabaseField
    private int mobileType;
    /** 数据网络类型 WIFI、3G、2G */
    @DatabaseField
    private int networkType;
    /** 测速类型（标记网络测速，带宽测速） */
    @DatabaseField
    private int type;
    /** 服务器地址 */
    @DatabaseField
    private String server;
    /** 服务器名称 */
    @DatabaseField
    private String serverName;
    /** 网络延迟 */
    @DatabaseField(dataType = DataType.FLOAT)
    private float ping;
    /** 上传速率(平均) */
    @DatabaseField(dataType = DataType.DOUBLE)
    private double upload;
    /** 下载速率(平均) */
    @DatabaseField(dataType = DataType.DOUBLE)
    private double download;
    /** 是否测试失败，0：正常，1：上传失败，2：下载失败, -1：用户取消 */
    @DatabaseField
    private int failure;
    /** 测试日期 */
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime ddate;
    /** 是否已分享到网络 */
    @DatabaseField
    private boolean shared;

    public SpeedTestResult() {
    }

    public SpeedTestResult(int phoneType, int mobileType, int networkType, String server, String serverName, DateTime ddate) {
        this.phoneType = phoneType;
        this.mobileType = mobileType;
        this.networkType = networkType;
        this.server = server;
        this.serverName = serverName;
        this.ddate = ddate;
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

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public float getPing() {
        return ping;
    }

    public void setPing(float ping) {
        this.ping = ping;
    }

    public double getUpload() {
        return upload;
    }

    public void setUpload(double upload) {
        this.upload = upload;
    }

    public double getDownload() {
        return download;
    }

    public void setDownload(double download) {
        this.download = download;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public DateTime getDdate() {
        return ddate;
    }

    public void setDdate(DateTime ddate) {
        this.ddate = ddate;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    public int getOperator() {
        return operator;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
