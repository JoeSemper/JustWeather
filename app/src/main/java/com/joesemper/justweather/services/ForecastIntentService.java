package com.joesemper.justweather.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.google.gson.Gson;
import com.joesemper.justweather.ForecastUpdateExecutor;
import com.joesemper.justweather.MainActivity;
import com.joesemper.justweather.forecast.Main;
import com.joesemper.justweather.forecast.MainForecast;

public class ForecastIntentService extends IntentService {

    public static final String EXTRA_CITY = "com.joesemper.justweather.action.CITY";
    public static final String EXTRA_RESULT = "com.joesemper.justweather.action.RESULT";

    public ForecastIntentService() {
        super("ForecastIntentService");
    }

    public static void startActionUpdate(Context context, String city) {

        Intent intent = new Intent(context, ForecastIntentService.class);
        intent.putExtra(EXTRA_CITY, city);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        MainForecast mainForecast;

        String city = intent.getStringExtra(EXTRA_CITY);
        String forecastResult = executeUpdateFromServer(city);
        mainForecast = jsonToClass(forecastResult);
        sendBroadcast(mainForecast);
    }

    private String executeUpdateFromServer(String city) {
        ForecastUpdateExecutor forecastUpdateExecutor = new ForecastUpdateExecutor();
        return forecastUpdateExecutor.getForecastJSON(city);
    }

    private MainForecast jsonToClass(String json){
        Gson gson = new Gson();
        MainForecast mainForecast = gson.fromJson(json, MainForecast.class);
        return mainForecast;
    }

    private void sendBroadcast(MainForecast mainForecast){
        Intent broadcastIntent = new Intent(MainActivity.BROADCAST_ACTION_UPDATEFINISHED);
        broadcastIntent.putExtra(EXTRA_RESULT, mainForecast);
        sendBroadcast(broadcastIntent);
    }
}
