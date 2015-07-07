package com.cattsoft.phone.quality.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * 基础基站信息.
 * Created by Xiaohong on 2014/5/11.
 */
@DatabaseTable(tableName = "pq_base_cell")
public class BaseCell {
    @DatabaseField(generatedId = true, dataType = DataType.LONG)
    private long id;
    /** 移动国家代码 */
    @DatabaseField
    private String mcc;
    /** 移动网络号码 */
    @DatabaseField
    private String mnc;
    /** 手机制式 GSM、CDMA */
    @DatabaseField
    private int phoneType;
    /** 移动网络类型 HSDPA、GPRS */
    @DatabaseField
    private int mobileType;
    /** 位置区编码 */
    @DatabaseField
    private int lac;
    /** 基站编号，是个16位的数据（范围是0到65535） */
    @DatabaseField
    private int cid;
    /** 信号强度 */
    @DatabaseField
    private int rssi;
    /** 基站位置信息 */
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private GeoLocation location;
    /** 相邻基站 */
    @ForeignCollectionField()
    private ForeignCollection<NeighborBaseCell> neighborBaseCells;
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime ddate;

    public BaseCell() {
    }

    public BaseCell(String mcc, String mnc, int phoneType, int mobileType, int lac, int cid, int rssi, GeoLocation location, DateTime ddate) {
        this.mcc = mcc;
        this.mnc = mnc;
        this.phoneType = phoneType;
        this.mobileType = mobileType;
        this.lac = lac;
        this.cid = cid;
        this.rssi = rssi;
        this.location = location;
        this.ddate = ddate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
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

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    public ForeignCollection<NeighborBaseCell> getNeighborBaseCells() {
        return neighborBaseCells;
    }

    public void setNeighborBaseCells(ForeignCollection<NeighborBaseCell> neighborBaseCells) {
        this.neighborBaseCells = neighborBaseCells;
    }

    public DateTime getDdate() {
        return ddate;
    }

    public void setDdate(DateTime ddate) {
        this.ddate = ddate;
    }
}
