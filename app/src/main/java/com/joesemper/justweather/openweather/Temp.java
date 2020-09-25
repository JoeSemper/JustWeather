package com.joesemper.justweather.openweather;

import java.io.Serializable;

public class Temp implements Serializable {
    private float day;
    private float min;
    private float max;
    private float night;
    private float eve;
    private float morn;

    public float getDay() {
        return day;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getNight() {
        return night;
    }

    public float getEve() {
        return eve;
    }

    public float getMorn() {
        return morn;
    }

    public void setDay(float day) {
        this.day = day;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public void setNight(float night) {
        this.night = night;
    }

    public void setEve(float eve) {
        this.eve = eve;
    }

    public void setMorn(float morn) {
        this.morn = morn;
    }
}
