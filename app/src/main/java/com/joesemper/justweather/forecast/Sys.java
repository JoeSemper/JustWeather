package com.joesemper.justweather.forecast;

public class Sys {

    private long sunrise;
    private long sunset;

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }
}
