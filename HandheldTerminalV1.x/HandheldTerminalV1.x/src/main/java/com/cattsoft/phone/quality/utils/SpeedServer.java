package com.cattsoft.phone.quality.utils;

/**
 * Created by Xiaohong on 2015/3/12.
 */
public class SpeedServer {
    private int id;
    private String region;
    private String cities;
    private String name;
    private String server;
    private int port;

    public SpeedServer(int id, String cities, String name, String server, int port) {
        this.id = id;
        this.cities = cities;
        this.name = name;
        this.server = server;
        this.port = port;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public int getId() {
        return id;
    }

    public String getCities() {
        return cities;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }
}
