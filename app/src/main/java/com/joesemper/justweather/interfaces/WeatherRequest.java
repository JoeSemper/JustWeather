package com.joesemper.justweather.interfaces;

import com.joesemper.justweather.forecast.MainForecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface WeatherRequest {
    @GET("data/2.5/weather")
    Call<MainForecast> loadWeather(@Query("q") String cityCountry, @Query("appid") String keyApi);
}
