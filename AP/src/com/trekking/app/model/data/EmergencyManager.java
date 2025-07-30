package com.trekking.app.model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.trekking.app.model.EmergencyIncident;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmergencyManager {
    private static final String FILE_PATH = "emergencies.json";
    private final Gson gson;

    public EmergencyManager() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    public List<EmergencyIncident> loadIncidents() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<EmergencyIncident>>() {}.getType();
            List<EmergencyIncident> incidents = gson.fromJson(reader, listType);
            return incidents != null ? incidents : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void saveIncidents(List<EmergencyIncident> incidents) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(incidents, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addIncident(EmergencyIncident incident) {
        List<EmergencyIncident> incidents = loadIncidents();
        incidents.add(incident);
        saveIncidents(incidents);
    }
}