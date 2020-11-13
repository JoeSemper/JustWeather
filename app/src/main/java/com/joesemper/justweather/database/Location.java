package com.joesemper.justweather.database;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Location {

    public Location(String city, float lat, float lon) {
        this.city = city;
        this.lat = lat;
        this.lon = lon;
    }

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "city")
    public String city;

    @ColumnInfo(name = "lat")
    public float lat;

    @ColumnInfo(name = "lon")
    public float lon;
}
