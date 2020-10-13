package com.joesemper.justweather.forecast.openweather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Weather implements Serializable {

    @SerializedName("main")
    @Expose
    private String main;
    @SerializedName("icon")
    @Expose
    private String icon;


    public String getMain() {
        return main;
    }

    public String getIcon() {
        return icon;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
