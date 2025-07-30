package com.trekking.app.model.data;

import com.google.gson.Gson;
import com.trekking.app.model.weather.WeatherResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherService {

    private static final String API_KEY = "0a8f67b90c37747221d4690636fe6136";

    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";
    private final HttpClient client;
    private final Gson gson;

    public WeatherService() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public WeatherResponse getWeatherForCity(String city) {
        // Sanitize city name for URL (e.g., "Everest Base Camp" -> "Everest%20Base%20Camp")
        String formattedCity = city.replace(" ", "%20");
        String url = String.format(API_URL, formattedCity, API_KEY);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Use Gson to parse the JSON string into our WeatherResponse object
                return gson.fromJson(response.body(), WeatherResponse.class);
            } else {
                System.err.println("Error fetching weather data. Status code: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}