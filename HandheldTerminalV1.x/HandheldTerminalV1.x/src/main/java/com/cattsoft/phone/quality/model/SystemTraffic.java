package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * 系统流量统计.
 * Created by Xiaohong on 2014/5/10.
 */
@DatabaseTable(tableName = "pq_system_traffic")
public class SystemTraffic {
    @DatabaseField(generatedId = true, dataType = DataType.LONG)
    private long id;
    @DatabaseField
    private long mobileRxBytes;
    @DatabaseField
    private long mobileTxBytes;

    @DatabaseField
    private long wifiRxBytes;
    @DatabaseField
    private long wifiTxBytes;

    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime ddate;

    public SystemTraffic() {
    }

    public SystemTraffic(long mobileRxBytes, long mobileTxBytes, long wifiRxBytes, long wifiTxBytes, DateTime ddate) {
        this.mobileRxBytes = mobileRxBytes;
        this.mobileTxBytes = mobileTxBytes;
        this.wifiRxBytes = wifiRxBytes;
        this.wifiTxBytes = wifiTxBytes;
        this.ddate = ddate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMobileRxBytes() {
        return mobileRxBytes;
    }

    public void setMobileRxBytes(long mobileRxBytes) {
        this.mobileRxBytes = mobileRxBytes;
    }

    public long getMobileTxBytes() {
        return mobileTxBytes;
    }

    public void setMobileTxBytes(long mobileTxBytes) {
        this.mobileTxBytes = mobileTxBytes;
    }

    public long getWifiRxBytes() {
        return wifiRxBytes;
    }

    public void setWifiRxBytes(long wifiRxBytes) {
        this.wifiRxBytes = wifiRxBytes;
    }

    public long getWifiTxBytes() {
        return wifiTxBytes;
    }

    public void setWifiTxBytes(long wifiTxBytes) {
        this.wifiTxBytes = wifiTxBytes;
    }

    public DateTime getDdate() {
        return ddate;
    }

    public void setDdate(DateTime ddate) {
        this.ddate = ddate;
    }
}
