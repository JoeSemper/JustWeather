package com.joesemper.justweather.retrofit;

import android.content.Context;
import android.nfc.FormatException;
import android.widget.Toast;

import com.joesemper.justweather.BuildConfig;
import com.joesemper.justweather.MainActivity;
import com.joesemper.justweather.database.Location;
import com.joesemper.justweather.forecast.WeatherParser;
import com.joesemper.justweather.forecast.openweather.OpenWeather;
import com.joesemper.justweather.interfaces.ForecastUpdater;
import com.joesemper.justweather.interfaces.WeatherRequest;

import androidx.annotation.NonNull;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUpdater  {

    private MainActivity mainActivity;
    private Location location;

    private OpenWeather openWeather;
    private WeatherParser weatherParser;
    private WorkManager workManager;

    private WeatherRequest weatherRequest;

    private static final String ID = BuildConfig.WEATHER_API_KEY;


    {
        initRetrofit();
    }

    public RetrofitUpdater(MainActivity context) {
        this.mainActivity = context;
    }


    public void executeUpdate(Location location) {

        float lat = location.lat;
        float lon = location.lon;
        String exclude = "minutely";

        updateWeather(lat, lon, exclude, ID);

        workManager = WorkManager.getInstance(mainActivity);



    }

    private void initRetrofit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        weatherRequest = retrofit.create(WeatherRequest.class);
    }

    private void updateWeather(float lat, float lon, String exclude, String keyApi) {
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

    private static class UpdateWorker extends Worker {

        public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }


        @NonNull
        @Override
        public Result doWork() {
            return null;
        }
    }


}
