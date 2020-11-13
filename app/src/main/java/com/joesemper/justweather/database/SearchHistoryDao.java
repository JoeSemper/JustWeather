package com.joesemper.justweather.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLocation(Location location);

    @Update
    void updateLocation(Location location);

    @Delete
    void deleteLocation(Location location);

    @Query("SELECT * FROM location")
    List<Location> getAllLocations();

    @Query("DELETE FROM location")
    void deleteAllLocations();

    @Query("SELECT COUNT() FROM location")
    long getCountLocations();
}
