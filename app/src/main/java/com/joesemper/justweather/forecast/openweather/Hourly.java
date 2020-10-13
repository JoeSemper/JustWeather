package com.joesemper.justweather.forecast.openweather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Hourly implements Serializable {

    @SerializedName("dt")
    @Expose
    private long dt;
    @SerializedName("temp")
    @Expose
    private float temp;
    @SerializedName("weather")
    @Expose
    private Weather[] weather;

    public long getDt() {
        return dt;
    }

    public float getTemp() {
        return temp;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }
}
