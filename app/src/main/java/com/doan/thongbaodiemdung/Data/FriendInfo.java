package com.doan.thongbaodiemdung.Data;

import java.io.Serializable;

/**
 * Created by Hong Hanh on 4/27/2017.
 */

public class FriendInfo implements Serializable{
    private String id;
    private String name;
    private String avatarURL;
    private Double latitude;
    private Double longitude;
    private String status;
    private boolean isFollowing;
    private boolean isNotifying;
    private int minDis;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public boolean isNotifying() {
        return isNotifying;
    }

    public void setNotifying(boolean notifying) {
        isNotifying = notifying;
    }

    public int getMinDis() {
        return minDis;
    }

    public void setMinDis(int minDis) {
        this.minDis = minDis;
    }
}
