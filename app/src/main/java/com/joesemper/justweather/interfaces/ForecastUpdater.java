package com.joesemper.justweather.interfaces;

import com.joesemper.justweather.forecast.MainForecast;

@FunctionalInterface
public interface ForecastUpdater {

    MainForecast getForecast(String city);
}
