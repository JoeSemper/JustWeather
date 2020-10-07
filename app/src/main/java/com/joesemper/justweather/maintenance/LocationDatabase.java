package com.joesemper.justweather.maintenance;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Location.class}, version = 1)
public abstract class LocationDatabase extends RoomDatabase {
    public abstract SearchHistoryDao getLocationDao();
}
