package com.doan.thongbaodiemdung.Data;

/**
 * Created by vthha on 4/5/2017.
 */

public class Route {
    private int id;
    private Double latitude;
    private Double longitude;
    private String info;
    private int isEnable;
    private Double distance;

    public int getId() {
        return id;
    }

    public Route setId(int id) {
        this.id = id;
        return this;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Route setLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Route setLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getInfo() {
        return info;
    }

    public Route setInfo(String info) {
        this.info = info;
        return this;
    }

    public int getIsEnable() {
        return isEnable;
    }

    public Route setIsEnable(int isEnable) {
        this.isEnable = isEnable;
        return this;
    }

    public Double getDistance() {
        return distance;
    }

    public Route setDistance(Double distance) {
        this.distance = distance;
        return this;
    }

    @Override
    public String toString() {
        return "Info: " + info + ";"
                + "isEnable: " + isEnable;
    }
}
