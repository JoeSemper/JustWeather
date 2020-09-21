package com.joesemper.justweather.interfaces;

import com.joesemper.justweather.forecast.MainForecast;

public interface ForecastUpdater {

    MainForecast getForecast(String city);
    String getForecastJSON(String city);
}
