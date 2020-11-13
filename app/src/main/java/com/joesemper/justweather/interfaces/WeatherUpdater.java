package com.joesemper.justweather.interfaces;

import androidx.appcompat.app.AppCompatActivity;

import com.joesemper.justweather.database.Location;
import com.joesemper.justweather.forecast.WeatherParser;

public interface WeatherUpdater {
    void executeUpdate(AppCompatActivity context, Location location);
    WeatherParser getWeatherParser();
}
