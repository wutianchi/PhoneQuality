package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * Created by Xiaohong on 2014/3/28.
 */
@DatabaseTable(tableName = "pq_geo_location")
public class GeoLocation {
    @DatabaseField(generatedId = true, dataType = DataType.LONG)
    private long id;
    /** 经度坐标 */
    @DatabaseField
    private double longitude;
    /** 纬度坐标 */
    @DatabaseField
    private double latitude;
    /** 卫星数量 - gps定位结果时，获取gps锁定用的卫星数 */
    @DatabaseField
    private int satelliteNumber;

    /**
     * 网络运营商信息 - NET
     * 移动：{@link com.baidu.location.BDLocation#OPERATORS_TYPE_MOBILE }
     * 联通：{@link com.baidu.location.BDLocation#OPERATORS_TYPE_UNICOM }
     * 电信：{@link com.baidu.location.BDLocation#OPERATORS_TYPE_TELECOMU }
     * 未知：{@link com.baidu.location.BDLocation#OPERATORS_TYPE_UNKONW }
     */
    @DatabaseField
    private int operators;

    /** 获取定位精度 */
    @DatabaseField
    private float radius;
    /** 速度，仅gps定位结果时有速度信息 */
    @DatabaseField
    private float speed;

    /** 位置详细地址信息 - NET */
    @DatabaseField
    private String address;
    /** 获取城市 */
    @DatabaseField
    private String city;
    /** 城市城市编号 */
    @DatabaseField
    private String cityCode;
    /** 获取区/县信息 */
    @DatabaseField
    private String district;
    /** 获取楼层信息,仅室内定位时有效 */
    @DatabaseField
    private String floor;
    /** 获取省份 */
    @DatabaseField
    private String province;
    /** 街道信息 */
    @DatabaseField
    private String street;
    /** 街道号码 */
    @DatabaseField
    private String streetNumber;
    /**
     * 在网络定位结果的情况下，获取网络定位结果是通过基站定位得到的还是通过wifi定位得到的
     * String : "wf"： wifi定位结果 “cl“； cell定位结果 null 没有获取到定位结果采用的类型
     */
    @DatabaseField
    private String networkLocationType;
    /** 是否有地址信息 */
    @DatabaseField
    private boolean addr;
    /**  */
    @DatabaseField
    private boolean altitude;
    /** 是否包含速度信息 */
    @DatabaseField
    private boolean conSpeed;
    /** 仅在getloctype == TypeOffLineLocationNetworkFail起作用。 */
    @DatabaseField
    private boolean cellChangeFlag;

    /** 定位类型: 参考 定位结果描述 相关的字段 */
    @DatabaseField
    private int locType;

    /** 定位更新时间 */
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime ddate;

    public GeoLocation() {
        //
    }

    public GeoLocation(int locType, double longitude, double latitude, DateTime ddate) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.ddate = ddate;
        this.locType = locType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getSatelliteNumber() {
        return satelliteNumber;
    }

    public void setSatelliteNumber(int satelliteNumber) {
        this.satelliteNumber = satelliteNumber;
    }

    public int getOperators() {
        return operators;
    }

    public void setOperators(int operators) {
        this.operators = operators;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getNetworkLocationType() {
        return networkLocationType;
    }

    public void setNetworkLocationType(String networkLocationType) {
        this.networkLocationType = networkLocationType;
    }

    public boolean isAddr() {
        return addr;
    }

    public void setAddr(boolean addr) {
        this.addr = addr;
    }

    public boolean isAltitude() {
        return altitude;
    }

    public void setAltitude(boolean altitude) {
        this.altitude = altitude;
    }

    public boolean isConSpeed() {
        return conSpeed;
    }

    public void setConSpeed(boolean conSpeed) {
        this.conSpeed = conSpeed;
    }

    public boolean isCellChangeFlag() {
        return cellChangeFlag;
    }

    public void setCellChangeFlag(boolean cellChangeFlag) {
        this.cellChangeFlag = cellChangeFlag;
    }

    public int getLocType() {
        return locType;
    }

    public void setLocType(int locType) {
        this.locType = locType;
    }

    public DateTime getDdate() {
        return ddate;
    }

    public void setDdate(DateTime ddate) {
        this.ddate = ddate;
    }
}
