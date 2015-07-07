package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * 短信记录结构数据.
 * Created by Xiaohong on 2014/5/9.
 */
@DatabaseTable(tableName = "pq_sms_structure")
public class SmsStructure {
    @DatabaseField(generatedId = true, dataType = DataType.LONG)
    private long id;
    /** 手机制式 GSM、CDMA */
    @DatabaseField
    private int phoneType;
    /** 移动网络类型 HSDPA、GPRS */
    @DatabaseField
    private int mobileType;
    /** 联系人名称 */
    @DatabaseField
    private String name;
    /** 发信人号码 */
    @DatabaseField
    private String number;
    /** 类型 */
    @DatabaseField
    private int type;
    /** 电信运营商 */
    @DatabaseField
    private int telecoms;
    /** 服务中心 */
    @DatabaseField
    private String serviceCenter;
    /** 日期 */
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime ddate;

    public SmsStructure() {
    }

    public SmsStructure(int phoneType, int mobileType, String name, String number, int type, DateTime ddate) {
        this.phoneType = phoneType;
        this.mobileType = mobileType;
        this.name = name;
        this.number = number;
        this.type = type;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTelecoms() {
        return telecoms;
    }

    public void setTelecoms(int telecoms) {
        this.telecoms = telecoms;
    }

    public String getServiceCenter() {
        return serviceCenter;
    }

    public void setServiceCenter(String serviceCenter) {
        this.serviceCenter = serviceCenter;
    }

    public DateTime getDdate() {
        return ddate;
    }

    public void setDdate(DateTime ddate) {
        this.ddate = ddate;
    }
}
