package com.joesemper.justweather.forecast.openweather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Current implements Serializable {

    @SerializedName("dt")
    @Expose
    private long dt;
    @SerializedName("sunrise")
    @Expose
    private long sunrise;
    @SerializedName("sunset")
    @Expose
    private long sunset;
    @SerializedName("temp")
    @Expose
    private float temp;
    @SerializedName("feels_like")
    @Expose
    private float feels_like;
    @SerializedName("pressure")
    @Expose
    private long pressure;
    @SerializedName("humidity")
    @Expose
    private int humidity;
    @SerializedName("wind_speed")
    @Expose
    private float wind_speed;
    @SerializedName("wind_deg")
    @Expose
    private int wind_deg;
    @SerializedName("weather")
    @Expose
    private Weather[] weather;

    public long getDt() {
        return dt;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public float getTemp() {
        return temp;
    }

    public float getFeels_like() {
        return feels_like;
    }

    public long getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public float getWind_speed() {
        return wind_speed;
    }

    public int getWind_deg() {
        return wind_deg;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public void setFeels_like(float feels_like) {
        this.feels_like = feels_like;
    }

    public void setPressure(long pressure) {
        this.pressure = pressure;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setWind_speed(float wind_speed) {
        this.wind_speed = wind_speed;
    }

    public void setWind_deg(int wind_deg) {
        this.wind_deg = wind_deg;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }
}
