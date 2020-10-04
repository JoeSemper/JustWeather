package com.joesemper.justweather.maintenance;

import android.app.Application;

import androidx.room.Room;

public class App extends Application {

    private static App instance;

    private LocationDatabase db;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        db = Room.databaseBuilder(getApplicationContext(), LocationDatabase.class,
                "location_database").allowMainThreadQueries().build();
    }


    public SearchHistoryDao getLocationDao() {
        return db.getLocationDao();
    }
}
