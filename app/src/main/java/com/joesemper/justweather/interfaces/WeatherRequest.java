package com.joesemper.justweather.interfaces;

import com.joesemper.justweather.forecast.openweather.OpenWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface WeatherRequest {

    @GET("data/2.5/onecall")
    Call<OpenWeather> loadWeather(@Query("lat") float lat,
                                  @Query("lon") float lon,
                                  @Query("units") String units,
                                  @Query("exclude") String exclude,
                                  @Query("appid") String keyApi);
}
