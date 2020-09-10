package com.joesemper.justweather;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

import com.google.gson.Gson;
import com.joesemper.justweather.forecast.MainForecast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class ForecastHolder {

//    private static final String TAG = "WEATHER";
//    private static final String WEATHER_URL =
//            "https://api.openweathermap.org/data/2.5/weather?q=%s,ru&appid=%s";
//
//    private static final String ID = "33da5092c9df576f30d4bfe2788922a4";
//
//    private MainForecast mainForecast;
//
//
//    public void setMainForecast(MainForecast mainForecast) {
//        this.mainForecast = new MainForecast();
//        this.mainForecast.getMain().setTemp(mainForecast.getMain().getTemp());
//        this.mainForecast.getMain().setPressure(mainForecast.getMain().getPressure());
//        this.mainForecast.getWind().setSpeed(mainForecast.getWind().getSpeed());
//    }
//
//    public MainForecast getForecast(String city) {
//        updateData(city);
//        return mainForecast;
//    }
//
//    private void updateData(String city) {
//        try {
//            final URL uri = new URL(String.format(WEATHER_URL, city, ID));
//            final Handler handler = new Handler(); // Запоминаем основной поток
//            new Thread(new Runnable() {
//                public void run() {
//                    HttpsURLConnection urlConnection = null;
//                    try {
//                        urlConnection = (HttpsURLConnection) uri.openConnection();
//                        urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
//                        urlConnection.setReadTimeout(10000); // установка таймаута - 10 000 миллисекунд
//                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
//                        String result = getLines(in);
//                        // преобразование данных запроса в модель
//                        Gson gson = new Gson();
//                        final MainForecast mainForecast = gson.fromJson(result, MainForecast.class);
//                        // Возвращаемся к основному потоку
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                setMainForecast(mainForecast);
//                            }
//                        });
//
//                    } catch (Exception e) {
//                        Log.e(TAG, "Fail connection", e);
//                        e.printStackTrace();
//                    } finally {
//                        if (null != urlConnection) {
//                            urlConnection.disconnect();
//                        }
//                    }
//                }
//            }).start();
//        } catch (MalformedURLException e) {
//            Log.e(TAG, "Fail URI", e);
//            e.printStackTrace();
//        }
//    }
//
//    @SuppressLint("NewApi")
//    private String getLines(BufferedReader in) {
//        return in.lines().collect(Collectors.joining("\n"));
//    }
}


