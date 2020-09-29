package com.joesemper.justweather.openweather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OpenWeather implements Serializable {

    @SerializedName("lat")
    @Expose
    private float lat;
    @SerializedName("lon")
    @Expose
    private float lon;

    @SerializedName("current")
    @Expose
    private Current current;

    @SerializedName("hourly")
    @Expose
    private Hourly[] hourly;

    @SerializedName("daily")
    @Expose
    private Daily[] daily;

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public Current getCurrent() {
        return current;
    }

    public Hourly[] getHourly() {
        return hourly;
    }

    public Daily[] getDaily() {
        return daily;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public void setHourly(Hourly[] hourly) {
        this.hourly = hourly;
    }

    public void setDaily(Daily[] daily) {
        this.daily = daily;
    }
}
