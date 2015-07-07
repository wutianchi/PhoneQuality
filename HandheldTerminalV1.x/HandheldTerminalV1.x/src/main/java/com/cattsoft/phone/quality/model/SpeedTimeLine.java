package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * 在线分享时间线数据
 * Created by Xiaohong on 2014/4/30.
 */
@DatabaseTable(tableName = "pq_speed_timeline")
public class SpeedTimeLine {
    @DatabaseField(generatedId = true, dataType = DataType.LONG)
    private long id;
    /** 测速服务器名称 */
    @DatabaseField
    private String server;
    /** 手机运营商代码 */
    @DatabaseField
    private int operator;
    /** 手机运营商名称 */
    @DatabaseField
    private String operatorName;
    /** 网络类型 */
    @DatabaseField
    private int netType;
    /** 地址 */
    @DatabaseField
    private String address;
    /** 测速类型 */
    @DatabaseField
    private int type;
    /** Ping 延迟 */
    @DatabaseField
    private float ping;
    /** 下载速率 */
    @DatabaseField
    private long download;
    /** 上传速率 */
    @DatabaseField
    private long upload;
    /** 测试日期 */
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime ddate;
    @DatabaseField
    private String message;
    /** 分享日期 */
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime sdate;

    public SpeedTimeLine() {
    }

    public SpeedTimeLine(String server, int netType, float ping, long download, long upload, DateTime ddate, DateTime sdate) {
        this.server = server;
        this.netType = netType;
        this.ping = ping;
        this.download = download;
        this.upload = upload;
        this.ddate = ddate;
        this.sdate = sdate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getNetType() {
        return netType;
    }

    public void setNetType(int netType) {
        this.netType = netType;
    }

    public float getPing() {
        return ping;
    }

    public void setPing(float ping) {
        this.ping = ping;
    }

    public long getDownload() {
        return download;
    }

    public void setDownload(long download) {
        this.download = download;
    }

    public long getUpload() {
        return upload;
    }

    public void setUpload(long upload) {
        this.upload = upload;
    }

    public DateTime getDdate() {
        return ddate;
    }

    public void setDdate(DateTime ddate) {
        this.ddate = ddate;
    }

    public DateTime getSdate() {
        return sdate;
    }

    public void setSdate(DateTime sdate) {
        this.sdate = sdate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
