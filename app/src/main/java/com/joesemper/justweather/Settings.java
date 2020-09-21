package com.joesemper.justweather;

import java.util.LinkedList;
import java.util.List;

public class Settings {

    private static Settings instance;
    private List<String> locationsHistory;
    private String currentLocation;


    private Settings() {
        locationsHistory = new LinkedList<>();
        locationsHistory.add("Moscow");
        setCurrentLocation("Moscow");
    }

    public static synchronized Settings getInstance(){
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public List<String> getLocationsHistory() {
        return locationsHistory;
    }

    public void addLocation(String location){
        setCurrentLocation(location);
        for (int i = 0; i <locationsHistory.size() ; i++) {
            if (locationsHistory.get(i).equals(location)){
                return;
            }
        }
        locationsHistory.add(location);
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }
}
