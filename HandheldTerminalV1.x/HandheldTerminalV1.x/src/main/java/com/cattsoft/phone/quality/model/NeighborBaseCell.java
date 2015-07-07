package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 相邻基站信息.
 * Created by Xiaohong on 2014/5/11.
 */
@DatabaseTable(tableName = "pq_neighbor_cell")
public class NeighborBaseCell {
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    public BaseCell baseCell;
    @DatabaseField(generatedId = true, dataType = DataType.LONG)
    private long id;
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
    @DatabaseField
    private int psc;
    @DatabaseField
    private int mNetworkType;

    public NeighborBaseCell() {
    }

    public NeighborBaseCell(int phoneType, int mobileType, int lac, int cid, int rssi, int psc, int mNetworkType) {
        this.phoneType = phoneType;
        this.mobileType = mobileType;
        this.lac = lac;
        this.cid = cid;
        this.rssi = rssi;
        this.psc = psc;
        this.mNetworkType = mNetworkType;
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

    public int getPsc() {
        return psc;
    }

    public void setPsc(int psc) {
        this.psc = psc;
    }

    public int getmNetworkType() {
        return mNetworkType;
    }

    public void setmNetworkType(int mNetworkType) {
        this.mNetworkType = mNetworkType;
    }

    public BaseCell getBaseCell() {
        return baseCell;
    }

    public void setBaseCell(BaseCell baseCell) {
        this.baseCell = baseCell;
    }
}
