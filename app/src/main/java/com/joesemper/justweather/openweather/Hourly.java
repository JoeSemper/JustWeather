package com.joesemper.justweather.openweather;

import java.io.Serializable;

public class Hourly implements Serializable {

    private long dt;
    private float temp;
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
