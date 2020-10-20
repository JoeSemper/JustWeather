package com.joesemper.justweather.retrofit;

import android.content.SharedPreferences;
import android.widget.Toast;

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

    private MainActivity mainActivity;
    private Location location;

    private String currentUnits;

    private OpenWeather openWeather;
    private static WeatherParser weatherParser;

    private WeatherRequest weatherRequest;

    private static final String ID = BuildConfig.WEATHER_API_KEY;


    {
        initRetrofit();
    }

    public RetrofitUpdater(MainActivity context) {
        this.mainActivity = context;
    }


    public void executeUpdate() {

        Location location = mainActivity.getCurrentLocation();
        float lat = location.lat;
        float lon = location.lon;
        String exclude = "minutely";
        loadPreferences();

        updateWeather(lat, lon, currentUnits, exclude, ID);


    }

    private void initRetrofit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        weatherRequest = retrofit.create(WeatherRequest.class);
    }

    private void updateWeather(float lat, float lon, String units, String exclude, String keyApi) {
        weatherRequest.loadWeather(lat, lon, units, exclude, keyApi)
                .enqueue(new Callback<OpenWeather>() {
                    @Override
                    public void onResponse(Call<OpenWeather> call, Response<OpenWeather> response) {
                        if (response.body() != null) {
                            openWeather = response.body();

                            weatherParser = new WeatherParser(mainActivity, openWeather);
                            mainActivity.displayWeather(weatherParser);
                            mainActivity.initRecyclerView();
                            Toast.makeText(mainActivity, "Success", Toast.LENGTH_SHORT).show();

                        }
                    }
                    @Override
                    public void onFailure(Call<OpenWeather> call, Throwable t) {
                        Toast.makeText(mainActivity, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences(SETTINGS, MODE_PRIVATE);
        currentUnits = sharedPreferences.getString(UNITS, METRIC);
    }

    public static WeatherParser getWeatherParser() {
        return weatherParser;
    }
}
