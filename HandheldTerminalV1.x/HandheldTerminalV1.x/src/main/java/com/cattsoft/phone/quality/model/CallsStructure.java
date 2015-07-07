package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * 通话记录结构数据.
 * Created by Xiaohong on 2014/5/9.
 */
@DatabaseTable(tableName = "pq_call_structure")
public class CallsStructure {
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
    /** 号码 */
    @DatabaseField
    private String number;
    /** 通话类型 */
    @DatabaseField
    private int type;
    @DatabaseField
    private boolean cnew;
    /** 断开原因 */
    @DatabaseField
    private String cause;
    /** 通话时长 */
    @DatabaseField
    private long duration;
    /** 通话时间 */
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime ddate;

    public CallsStructure() {
    }

    public CallsStructure(int phoneType, int mobileType, String name, String number, int type, String cause, long duration, DateTime ddate) {
        this.name = name;
        this.number = number;
        this.type = type;
        this.cause = cause;
        this.duration = duration;
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

    public boolean isCnew() {
        return cnew;
    }

    public void setCnew(boolean cnew) {
        this.cnew = cnew;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public DateTime getDdate() {
        return ddate;
    }

    public void setDdate(DateTime ddate) {
        this.ddate = ddate;
    }
}
