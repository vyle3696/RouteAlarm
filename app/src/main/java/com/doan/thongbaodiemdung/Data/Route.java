package com.doan.thongbaodiemdung.Data;

import java.io.Serializable;

/**
 * Created by vthha on 4/5/2017.
 */

public class Route implements Serializable{
    private int id;
    private Double latitude;
    private Double longitude;
    private String name;
    private String info;
    private int isEnable;
    private Double distance;
    private int minDistance;
    private String ringtone;
    private String ringtonePath;

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

    public String getName() {
        return name;
    }

    public Route setName(String name) {
        this.name = name;
        return this;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public Route setMinDistance(int minDistance) {
        this.minDistance = minDistance;
        return this;
    }

    public String getRingtone() {
        return ringtone;
    }

    public Route setRingtone(String ringtone) {
        this.ringtone = ringtone;
        return this;
    }

    public String getRingtonePath() {
        return ringtonePath;
    }

    public Route setRingtonePath(String ringtonePath) {
        this.ringtonePath = ringtonePath;
        return this;
    }



    @Override
    public String toString() {
        return "Info: " + info + ";"
                + "isEnable: " + isEnable;
    }
}
