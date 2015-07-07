package com.cattsoft.phone.quality.utils;

import java.io.Serializable;

/**
 * Created by Xiaohong on 2015/4/15.
 */
public class LastLocation implements Serializable {
    private double longitude;
    private double latitude;
    private String province;
    private String city;
    private String district;
    private String address;

    public LastLocation(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public LastLocation(String province, String city, String district, String address) {
        this.province = province;
        this.city = city;
        this.district = district;
        this.address = address;
    }

    public LastLocation(double longitude, double latitude, String province, String city, String district, String address) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.province = province;
        this.city = city;
        this.district = district;
        this.address = address;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getAddress() {
        return address;
    }
}
