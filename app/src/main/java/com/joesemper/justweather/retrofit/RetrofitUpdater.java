package com.joesemper.justweather.retrofit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.joesemper.justweather.BuildConfig;
import com.joesemper.justweather.MainActivity;
import com.joesemper.justweather.database.Location;
import com.joesemper.justweather.forecast.WeatherParser;
import com.joesemper.justweather.forecast.openweather.OpenWeather;
import com.joesemper.justweather.interfaces.WeatherRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.joesemper.justweather.SettingsActivity.METRIC;
import static com.joesemper.justweather.SettingsActivity.SETTINGS;
import static com.joesemper.justweather.SettingsActivity.UNITS;

public class RetrofitUpdater  {

    private AppCompatActivity context;

    private static WeatherParser weatherParser;

    private WeatherRequest weatherRequest;

    public static final String UPDATE_FINISHED = "com.joesemper.justweather.updatefinished";
    public static final String UPDATE_RESULT = "com.joesemper.justweather.updateresult";
    public static final int UPDATE_RESULT_OK = 0;


    {
        initRetrofit();
    }

    public RetrofitUpdater(AppCompatActivity context) {
        this.context = context;
    }

    public void executeUpdate(Location location) {
        UpdateData updateData = buildUpdateData(location);
        updateWeather(updateData);
    }

    private void initRetrofit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        weatherRequest = retrofit.create(WeatherRequest.class);
    }

    private void updateWeather(UpdateData updateData) {
        weatherRequest.loadWeather(
                updateData.getLat(), updateData.getLon(),
                updateData.getUnits(), updateData.getExclude(), updateData.getID())
                .enqueue(new Callback<OpenWeather>() {
                    @Override
                    public void onResponse(Call<OpenWeather> call, Response<OpenWeather> response) {
                        if (response.body() != null) {
                            OpenWeather openWeather = response.body();
                            weatherParser = new WeatherParser(context, openWeather);
                            sendBroadcastUpdate();
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<OpenWeather> call, Throwable t) {
                        Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private UpdateData buildUpdateData(Location location) {
        return new UpdateData.Builder(location).setUnits(getUnitsFromPreferences()).build();
    }

    private String getUnitsFromPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SETTINGS, MODE_PRIVATE);
        return sharedPreferences.getString(UNITS, METRIC);
    }

    private void sendBroadcastUpdate() {
        Intent broadcastIntent = new Intent(UPDATE_FINISHED);
        broadcastIntent.putExtra(UPDATE_RESULT, UPDATE_RESULT_OK);
        context.sendBroadcast(broadcastIntent);
    }

    public static WeatherParser getWeatherParser() {
        return weatherParser;
    }
}
