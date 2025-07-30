package com.trekking.app.model.weather;

import java.util.List;

// This is the main container for the entire API response
public class WeatherResponse {
    public Main main;
    public List<Weather> weather;
    public Wind wind; // New field for wind data
    public String name; // City name
}