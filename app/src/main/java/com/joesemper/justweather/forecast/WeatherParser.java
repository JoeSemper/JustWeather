package com.joesemper.justweather.forecast;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import com.joesemper.justweather.R;
import com.joesemper.justweather.forecast.openweather.OpenWeather;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.joesemper.justweather.SettingsActivity.IMPERIAL;
import static com.joesemper.justweather.SettingsActivity.METRIC;
import static com.joesemper.justweather.SettingsActivity.SETTINGS;
import static com.joesemper.justweather.SettingsActivity.UNITS;

public class WeatherParser {


    private String tempUnits;
    private String windUnits;
    private String pressureUnits;

    private OpenWeather openWeather;
    private AppCompatActivity context;

//    private Date date = new Date();

    public WeatherParser(AppCompatActivity context, OpenWeather openWeather) {
        this.openWeather = openWeather;
        this.context = context;
        loadPreferences();
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SETTINGS, MODE_PRIVATE);

        String currentUnits = sharedPreferences.getString(UNITS, METRIC);

        switch (currentUnits) {
            case METRIC:
                tempUnits = context.getString(R.string.units_temp_celsius);
                windUnits = context.getString(R.string.units_wind_meter);
                pressureUnits = context.getString(R.string.units_pressure);
                break;
            case IMPERIAL:
                tempUnits = context.getString(R.string.units_temp_far);
                windUnits = context.getString(R.string.units_wind_miles);
                pressureUnits = context.getString(R.string.units_pressure);
                break;
            default:
                tempUnits = context.getString(R.string.units_temp_kelvin);
                windUnits = context.getString(R.string.units_wind_meter);
                pressureUnits = context.getString(R.string.units_pressure);
        }
    }

    @SuppressLint("DefaultLocale")
    public String getCurrentTemperature() {
        return (String.format("%.0f%s", openWeather.getCurrent().getTemp(), tempUnits));
    }

    @SuppressLint("DefaultLocale")
    public String getCurrentPressure() {
        return (String.format("%d %s", openWeather.getCurrent().getPressure(), pressureUnits));
    }

    @SuppressLint("DefaultLocale")
    public String getCurrentWindSpeed() {
        return (String.format("%.1f %s", openWeather.getCurrent().getWind_speed(), windUnits));
    }

    public String getCurrentWeather() {
        return openWeather.getCurrent().getWeather()[0].getMain();
    }

    @SuppressLint("DefaultLocale")
    public String getCurrentFeelsLike() {
        return (String.format("%.0f %s", openWeather.getCurrent().getFeels_like(), tempUnits));
    }

    @SuppressLint("DefaultLocale")
    public String getSunriseSunset() {
        return (String.format("%s/%s",
                getHoursAndMinutes(getDateByMs(openWeather.getCurrent().getSunrise())),
                getHoursAndMinutes(getDateByMs(openWeather.getCurrent().getSunset()))));
    }

    private String getHoursAndMinutes(Date d) {
        StringBuilder sb = new StringBuilder();
        String[] date = d.toString().split(" ", 4);
        String[] hours = date[3].split(":", 3);
        sb.append(hours[0]).append(":").append("00");
        return sb.toString();
    }

    private Date getDateByMs(long ms) {
        return new Date(ms);
    }

    @SuppressLint("DefaultLocale")
    public String getCurrentMinMaxTemp() {
        return (String.format("%.0f%s/%.0f%s",
                openWeather.getDaily()[0].getTemp().getMin(), tempUnits,
                openWeather.getDaily()[0].getTemp().getMax(), tempUnits));
    }

    @SuppressLint("DefaultLocale")
    public String getCurrentHumidity() {
        return (String.format("%d %s", openWeather.getCurrent().getHumidity(), "%"));
    }

    @SuppressLint("DefaultLocale")
    public String getCurrentCloudiness() {
        return (String.format("%d %s", openWeather.getCurrent().getClouds(), "%"));
    }

    public int getMainWeatherIcon() {
        switch (openWeather.getCurrent().getWeather()[0].getIcon()) {
            case "01d":
                return R.drawable.sunny;
            case "02d":
                return R.drawable.partly_cloudy;
            case "03d":
                return R.drawable.clouds;
            case "04d":
                return R.drawable.clouds;
            case "09d":
                return R.drawable.heavy_rain;
            case "10d":
                return R.drawable.rain;
            case "11d":
                return R.drawable.storm;
            case "13d":
                return R.drawable.snow;
            default:
                return R.drawable.partly_cloudy;
        }
    }

    public String getDayWeather(int i) {
        return openWeather.getDaily()[i].getWeather()[0].getMain();
    }

    public int getDayWeatherIcon(int i) {
        switch (openWeather.getDaily()[i].getWeather()[0].getIcon()) {
            case "01d":
                return R.drawable.sunny;
            case "02d":
                return R.drawable.partly_cloudy;
            case "03d":
                return R.drawable.clouds;
            case "04d":
                return R.drawable.clouds;
            case "09d":
                return R.drawable.heavy_rain;
            case "10d":
                return R.drawable.rain;
            case "11d":
                return R.drawable.storm;
            case "13d":
                return R.drawable.snow;
            default:
                return R.drawable.partly_cloudy;
        }
    }

    @SuppressLint("DefaultLocale")
    public String getDayMinMaxTemp(int i) {
        return (String.format("%.0f%s/%.0f%s",
                openWeather.getDaily()[i].getTemp().getMin(), tempUnits,
                openWeather.getDaily()[i].getTemp().getMax(), tempUnits));
    }


    @SuppressLint("DefaultLocale")
    public String getDayWindSpeed(int i) {
        return (String.format("%.1f %s", openWeather.getDaily()[i].getWind_speed(), windUnits));
    }

    @SuppressLint("DefaultLocale")
    public String getDayPressure(int i) {
        return (String.format("%d %s", openWeather.getDaily()[i].getPressure(), pressureUnits));
    }

    @SuppressLint("DefaultLocale")
    public String getDayPop(int i) {
        return (String.format("%.0f %s", openWeather.getDaily()[i].getPop() * 100, "%"));
    }

    @SuppressLint("DefaultLocale")
    public String getDayCloudiness(int i) {
        return (String.format("%d %s", openWeather.getDaily()[i].getClouds(), "%"));
    }


    @SuppressLint("DefaultLocale")
    public String getMornTemp(int i) {
        return (String.format("%.0f %s", openWeather.getDaily()[i].getTemp().getMorn(), tempUnits));
    }

    @SuppressLint("DefaultLocale")
    public String getDayTemp(int i) {
        return (String.format("%.0f %s", openWeather.getDaily()[i].getTemp().getDay(), tempUnits));
    }

    @SuppressLint("DefaultLocale")
    public String getEveTemp(int i) {
        return (String.format("%.0f %s", openWeather.getDaily()[i].getTemp().getEve(), tempUnits));
    }

    @SuppressLint("DefaultLocale")
    public String getNightTemp(int i) {
        return (String.format("%.0f %s", openWeather.getDaily()[i].getTemp().getNight(), tempUnits));
    }


    public int getHourlyWeatherIcon(int i) {
        switch (openWeather.getHourly()[i].getWeather()[0].getIcon()) {
            case "01d":
                return R.drawable.sunny;
            case "02d":
                return R.drawable.partly_cloudy;
            case "03d":
                return R.drawable.clouds;
            case "04d":
                return R.drawable.clouds;
            case "09d":
                return R.drawable.heavy_rain;
            case "10d":
                return R.drawable.rain;
            case "11d":
                return R.drawable.storm;
            case "13d":
                return R.drawable.snow;
            default:
                return R.drawable.partly_cloudy;
        }
    }

    @SuppressLint("DefaultLocale")
    public String getHourlyTemp(int i) {
        return (String.format("%.0f %s", openWeather.getHourly()[i].getTemp(), tempUnits));
    }

    public String getTime(int i) {
        long hours = 3600000 * i;
        Date time = new Date();
        time.setTime(time.getTime() + hours);
        return (String.format("%s", getHoursAndMinutes(time)));
    }
}