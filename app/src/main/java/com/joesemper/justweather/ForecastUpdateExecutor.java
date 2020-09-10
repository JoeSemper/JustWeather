package com.joesemper.justweather;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

import com.google.gson.Gson;
import com.joesemper.justweather.forecast.Main;
import com.joesemper.justweather.forecast.MainForecast;
import com.joesemper.justweather.interfaces.ForecastUpdater;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class ForecastUpdateExecutor implements ForecastUpdater {

    private static final String TAG = "WEATHER";
    private static final String WEATHER_URL =
            "https://api.openweathermap.org/data/2.5/weather?q=%s,ru&appid=%s";
    private static final String ID = BuildConfig.WEATHER_API_KEY;

    private MainForecast mainForecast;

    private void setMainForecast(MainForecast mainForecast) {
        this.mainForecast = mainForecast;
    }

    @Override
    public MainForecast getForecast(String city) {
        updateData(city);
        return mainForecast;
    }

    private void updateData(String city) {
        try {
            final URL uri = new URL(String.format(WEATHER_URL, city, ID));

            Updater updater = new Updater(uri);
            updater.start();
            updater.join();

        } catch (MalformedURLException e) {
            Log.e(TAG, "Fail URI", e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private class Updater extends Thread {

        URL uri;

        public Updater(URL uri){
            this.uri = uri;
        }

        @Override
        public void run() {
            HttpsURLConnection urlConnection = null;
            try {
                urlConnection = (HttpsURLConnection) uri.openConnection();
                urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
                urlConnection.setReadTimeout(10000); // установка таймаута - 10 000 миллисекунд
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
                final String result = getLines(in);
                Gson gson = new Gson();
                final MainForecast mainForecast = gson.fromJson(result, MainForecast.class);

                setMainForecast(mainForecast);

            } catch (Exception e) {
                Log.e(TAG, "Fail connection", e);
                e.printStackTrace();
            } finally {
                if (null != urlConnection) {
                    urlConnection.disconnect();
                }
            }
        }

        @SuppressLint("NewApi")
        private String getLines(BufferedReader in) {
            return in.lines().collect(Collectors.joining("\n"));
        }
    }


}


