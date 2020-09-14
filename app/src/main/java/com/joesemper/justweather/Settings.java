package com.joesemper.justweather;

import java.util.LinkedList;
import java.util.List;

public class Settings {

    private static Settings instance;
    private static List<String> locationsHistory;
    private static String currentLocation;


    private Settings() {
        locationsHistory = new LinkedList<>();
        locationsHistory.add("Moscow");
        setCurrentLocation("Moscow");
    }

    public static Settings getInstance(){
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public static List<String> getLocationsHistory() {
        return locationsHistory;
    }

    public static void addLocation(String location){
        setCurrentLocation(location);
        for (int i = 0; i <locationsHistory.size() ; i++) {
            if (locationsHistory.get(i).equals(location)){
                return;
            }
        }
        locationsHistory.add(location);
    }

    public static void setCurrentLocation(String currentLocation) {
        Settings.currentLocation = currentLocation;
    }

    public static String getCurrentLocation() {
        return currentLocation;
    }
}
