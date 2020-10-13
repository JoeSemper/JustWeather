package com.joesemper.justweather.forecast;

import android.annotation.SuppressLint;

import com.joesemper.justweather.forecast.openweather.Current;
import com.joesemper.justweather.forecast.openweather.OpenWeather;

public class WeatherParser {

    public static final String STANDARD = "STANDARD";
    public static String METRIC = "METRIC";
    public static String IMPERIAL = "IMPERIAL";

    private String tempUnits;
    private String windUnits;
    private String pressureUnits = "hPa";

    private OpenWeather openWeather;

    public WeatherParser(OpenWeather openWeather, String units) {
        this.openWeather = openWeather;

        if (units.equals(STANDARD)) {
            tempUnits = "°K";
            windUnits = "m/s";
        } else if (units.equals(METRIC)) {
            tempUnits = "°C";
            windUnits = "m/s";
        } else if (units.equals(IMPERIAL)) {
            tempUnits = "°F";
            windUnits = "m/h";
        }

    }

    @SuppressLint("DefaultLocale")
    public String getCurrentTemperature() {
        return (String.format("%.0f%s", openWeather.getCurrent().getTemp(), tempUnits));
    }

    @SuppressLint("DefaultLocale")
    public String getCurrentPressure() {
        return (String.format("%d %s", openWeather.getCurrent().getPressure(), pressureUnits));
    }



}






























