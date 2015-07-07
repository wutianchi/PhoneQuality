package com.cattsoft.phone.quality.model;

import android.net.wifi.ScanResult;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * Wifi 扫描热点记录.
 * Created by Xiaohong on 2014/5/11.
 */
@DatabaseTable(tableName = "pq_wifi_result")
public class WifiResult {
    /**
     * 位置信息
     */
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    public GeoLocation location;
    @DatabaseField(generatedId = true, dataType = DataType.LONG)
    private long id;
    /** The network name. */
    @DatabaseField
    private String ssid;
    /** The address of the access point. */
    @DatabaseField
    private String bssid;
    /**
     * Describes the authentication, key management, and encryption schemes
     * supported by the access point.
     */
    @DatabaseField
    private String capabilities;
    /**
     * The detected signal level in dBm. At least those are the units used by
     * the TI driver.
     */
    @DatabaseField
    private int level;
    /**
     * The frequency in MHz of the channel over which the client is communicating
     * with the access point.
     */
    @DatabaseField
    private int frequency;
    /**
     * Time Synchronization Function (tsf) timestamp in microseconds when
     * this result was last seen.
     */
    @DatabaseField
    private DateTime ddate;

    public WifiResult() {
    }

    public WifiResult(ScanResult result) {
        this.ssid = result.SSID;
        this.bssid = result.BSSID;
        this.capabilities = result.capabilities;
        this.level = result.level;
        this.frequency = result.frequency;
        this.ddate = DateTime.now();
    }

    public WifiResult(String ssid, String bssid, String capabilities, int level, int frequency, DateTime ddate) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.capabilities = capabilities;
        this.level = level;
        this.frequency = frequency;
        this.ddate = ddate;
    }

    public WifiResult(String ssid, String bssid, String capabilities, int level, int frequency, DateTime ddate, GeoLocation location) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.capabilities = capabilities;
        this.level = level;
        this.frequency = frequency;
        this.ddate = ddate;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
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
