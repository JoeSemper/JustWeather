package com.joesemper.justweather;

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

//    private MainActivity mainActivity;
//
//    private static final String TAG = "WEATHER";
//    private static final String WEATHER_URL =
//            "https://api.openweathermap.org/data/2.5/weather?q=%s,%s&appid=%d";
//
//    private static final String ID = "33da5092c9df576f30d4bfe2788922a4";
//
//    private TextView city;
//    private TextView temperature;
//    private TextView pressure;
//    private TextView windSpeed;
//
//    public ForecastHolder(MainActivity mainActivity) {
//        this.mainActivity = mainActivity;
//    }
//
//    private void init() {
//        city = mainActivity.getCity();
//        temperature = mainActivity.getTemperature();
//        pressure = mainActivity.getPressure();
//        windSpeed = mainActivity.getWindSpeed();
//    }
//
//    public void updateData() {
//
//        init();
//
//        try {
//            final URL uri = new URL(String.format(WEATHER_URL, city.getText(), "RU", ID));
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
//                                displayWeather(mainForecast);
//                            }
//                        });
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
//    private String getLines(BufferedReader in) {
//        return in.lines().collect(Collectors.joining("\n"));
//    }
//
//    private void displayWeather(MainForecast mainForecast){
//        city.setText(mainForecast.getName());
//        temperature.setText(String.format("%f2", mainForecast.getMain().getTemp()));
//        pressure.setText(String.format("%d", mainForecast.getMain().getPressure()));
//        windSpeed.setText(String.format("%d", mainForecast.getWind().getSpeed()));
//    }


}

