package com.joesemper.justweather.openweather;

import java.io.Serializable;

public class Weather implements Serializable {
    private String main;
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
