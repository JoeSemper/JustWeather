package com.joesemper.justweather.interfaces;

import com.joesemper.justweather.database.Location;
import com.joesemper.justweather.forecast.MainForecast;
import com.joesemper.justweather.forecast.WeatherParser;
import com.joesemper.justweather.forecast.openweather.OpenWeather;

public interface ForecastUpdater {

    WeatherParser getWeatherParser(Location location);
//
//    MainForecast getForecast(String city);
//    String getForecastJSON(String city);
}
