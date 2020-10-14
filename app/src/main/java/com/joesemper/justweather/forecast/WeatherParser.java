package com.joesemper.justweather.forecast;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.joesemper.justweather.MainActivity;
import com.joesemper.justweather.MapsActivity;
import com.joesemper.justweather.R;
import com.joesemper.justweather.forecast.openweather.Current;
import com.joesemper.justweather.forecast.openweather.OpenWeather;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class WeatherParser {

    public static final String STANDARD = "STANDARD";
    public static String METRIC = "METRIC";
    public static String IMPERIAL = "IMPERIAL";

    private String tempUnits;
    private String windUnits;
    private String pressureUnits;

    private OpenWeather openWeather;
    private MainActivity mainActivity;

    private Date date = new Date();

    public WeatherParser(MainActivity context, OpenWeather openWeather) {
        this.openWeather = openWeather;
        this.mainActivity = context;
        loadPreferences();
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("Settings", MODE_PRIVATE);

        String currentUnits = sharedPreferences.getString("Units", STANDARD);

        if (currentUnits.equals(STANDARD)) {
            tempUnits = mainActivity.getString(R.string.units_temp_kelvin);
            windUnits = mainActivity.getString(R.string.units_wind_meter);
        } else if (currentUnits.equals(METRIC)) {
            tempUnits = mainActivity.getString(R.string.units_temp_celsius);
            windUnits = mainActivity.getString(R.string.units_wind_meter);
        } else if (currentUnits.equals(IMPERIAL)) {
            tempUnits = mainActivity.getString(R.string.units_temp_far);
            windUnits = mainActivity.getString(R.string.units_wind_miles);
        }

        pressureUnits = mainActivity.getString(R.string.units_pressure);
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
        sb.append(hours[0]).append(":").append(hours[1]);
        return sb.toString();
    }

    private Date getDateByMs(long ms) {
        date.setTime(ms);
        return date;
    }

    @SuppressLint("DefaultLocale")
    public String getCurrentMinMaxTemp() {
        return (String.format("%.0f%s/%.0f%s",
                openWeather.getDaily()[0].getTemp().getMin(), tempUnits,
                openWeather.getDaily()[0].getTemp().getMax(), tempUnits));
    }
}



























