package com.joesemper.justweather.openweather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Daily implements Serializable {

    @SerializedName("dt")
    @Expose
    private long dt;

    @SerializedName("temp")
    @Expose
    private Temp temp;

    @SerializedName("pressure")
    @Expose
    private int pressure;
    @SerializedName("wind_speed")
    @Expose
    private float wind_speed;
    @SerializedName("wind_deg")
    @Expose
    private float wind_deg;

    @SerializedName("weather")
    @Expose
    private Weather[] weather;

    public long getDt() {
        return dt;
    }

    public Temp getTemp() {
        return temp;
    }

    public int getPressure() {
        return pressure;
    }

    public float getWind_speed() {
        return wind_speed;
    }

    public float getWind_deg() {
        return wind_deg;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public void setTemp(Temp temp) {
        this.temp = temp;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public void setWind_speed(float wind_speed) {
        this.wind_speed = wind_speed;
    }

    public void setWind_deg(float wind_deg) {
        this.wind_deg = wind_deg;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }
}
