package com.joesemper.justweather.retrofit;

import com.joesemper.justweather.BuildConfig;
import com.joesemper.justweather.database.Location;

import static com.joesemper.justweather.SettingsActivity.METRIC;

public class UpdateData {

    private String ID;
    private String units;
    private String exclude;
    private float lat;
    private float lon;

    private UpdateData() {
    }

    public static class Builder {

        private UpdateData updateData;

        public Builder(Location location) {
            updateData = new UpdateData();
            updateData.lat = location.lat;
            updateData.lon = location.lon;
            updateData.exclude = "minutely";
            updateData.units = METRIC;
            updateData.ID = BuildConfig.WEATHER_API_KEY;
        }

        public Builder setUnits(String units) {
            updateData.units = units;
            return this;
        }

        public Builder setExclude(String exclude) {
            updateData.exclude = exclude;
            return this;
        }

        public UpdateData build(){
            return updateData;
        }

    }

    public String getID() {
        return ID;
    }

    public String getUnits() {
        return units;
    }

    public String getExclude() {
        return exclude;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }
}
