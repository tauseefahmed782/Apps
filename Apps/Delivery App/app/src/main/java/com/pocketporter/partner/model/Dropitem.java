package com.pocketporter.partner.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Dropitem implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("drop_point_address")
    @Expose
    private String dropPointAddress;
    @SerializedName("drop_point_lat")
    @Expose
    private double dropPointLat;
    @SerializedName("drop_point_lng")
    @Expose
    private double dropPointLng;
    @SerializedName("drop_point_mobile")
    @Expose
    private String dropPointMobile;

    @SerializedName("drop_point_name")
    @Expose
    private String dropPointName;

    @SerializedName("drop_point_status")
    @Expose
    private String dropPointStatus;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDropPointAddress() {
        return dropPointAddress;
    }

    public void setDropPointAddress(String dropPointAddress) {
        this.dropPointAddress = dropPointAddress;
    }

    public double getDropPointLat() {
        return dropPointLat;
    }

    public void setDropPointLat(double dropPointLat) {
        this.dropPointLat = dropPointLat;
    }

    public double getDropPointLng() {
        return dropPointLng;
    }

    public void setDropPointLng(double dropPointLng) {
        this.dropPointLng = dropPointLng;
    }

    public String getDropPointMobile() {
        return dropPointMobile;
    }

    public void setDropPointMobile(String dropPointMobile) {
        this.dropPointMobile = dropPointMobile;
    }

    public String getDropPointName() {
        return dropPointName;
    }

    public void setDropPointName(String dropPointName) {
        this.dropPointName = dropPointName;
    }

    public String getDropPointStatus() {
        return dropPointStatus;
    }

    public void setDropPointStatus(String dropPointStatus) {
        this.dropPointStatus = dropPointStatus;
    }
}

