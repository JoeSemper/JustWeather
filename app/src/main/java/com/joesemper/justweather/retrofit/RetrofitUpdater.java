package com.joesemper.justweather.retrofit;

import android.nfc.FormatException;
import android.widget.Toast;

import com.joesemper.justweather.BuildConfig;
import com.joesemper.justweather.MainActivity;
import com.joesemper.justweather.database.Location;
import com.joesemper.justweather.forecast.WeatherParser;
import com.joesemper.justweather.forecast.openweather.OpenWeather;
import com.joesemper.justweather.interfaces.ForecastUpdater;
import com.joesemper.justweather.interfaces.WeatherRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUpdater  {

    private MainActivity mainActivity;
    private Location location;

    private WeatherRequest weatherRequest

    private static final String ID = BuildConfig.WEATHER_API_KEY;


    {
        initRetrofit();
    }

    public RetrofitUpdater(MainActivity context) {
        this.mainActivity = context;
    }


    public OpenWeather executeUpdate(Location location) {



    }

    private void initRetrofit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        weatherRequest = retrofit.create(WeatherRequest.class);
    }

    private void requestRetrofit(float lat, float lon, String exclude, String keyApi) {
        weatherRequest.loadWeather(lat, lon, exclude, keyApi)
                .enqueue(new Callback<OpenWeather>() {
                    @Override
                    public void onResponse(Call<OpenWeather> call, Response<OpenWeather> response) {
                        if (response.body() != null) {
                            openWeather = response.body();
//                            WeatherParser weatherParser = new WeatherParser(MainActivity.this, openWeather);
//                            displayWeather(weatherParser);
                            Toast.makeText(mainActivity, "Success", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<OpenWeather> call, Throwable t) {
                        Toast.makeText(mainActivity, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
