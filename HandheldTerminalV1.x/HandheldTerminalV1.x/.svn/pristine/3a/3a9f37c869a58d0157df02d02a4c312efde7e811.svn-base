package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * 伪基站短信息.
 * Created by Xiaohong on 2014/4/11.
 */
@DatabaseTable(tableName = "pq_pseudo_sms")
public class PseudoSms {
    @DatabaseField(generatedId = true, dataType = DataType.LONG)
    private long id;

    @DatabaseField
    private String address;

    @DatabaseField
    private String name;

    @DatabaseField
    private int status;

    @DatabaseField
    private String service;

    @DatabaseField
    private int protocol;

    @DatabaseField
    private String content;

    /** 日期时间 */
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime ddate;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private GeoLocation location;

    public PseudoSms() {
        //
    }

    public PseudoSms(String address, String name, int status, String service, int protocol, String content, DateTime ddate) {
        this.address = address;
        this.name = name;
        this.status = status;
        this.service = service;
        this.protocol = protocol;
        this.content = content;
        this.ddate = ddate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DateTime getDdate() {
        return ddate;
    }

    public void setDdate(DateTime ddate) {
        this.ddate = ddate;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }
}
