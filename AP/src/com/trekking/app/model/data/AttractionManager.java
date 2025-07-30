package com.trekking.app.model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.trekking.app.model.Attraction;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AttractionManager {

    private static final String FILE_PATH = "attractions.json";
    private final Gson gson;

    public AttractionManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public List<Attraction> loadAttractions() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Attraction>>() {}.getType();
            List<Attraction> attractions = gson.fromJson(reader, listType);
            return attractions != null ? attractions : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void saveAttractions(List<Attraction> attractions) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(attractions, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addAttraction(Attraction newAttraction) {
        List<Attraction> attractions = loadAttractions();
        attractions.add(newAttraction);
        saveAttractions(attractions);
    }

    public void updateAttraction(Attraction updatedAttraction) {
        List<Attraction> attractions = loadAttractions();
        for (int i = 0; i < attractions.size(); i++) {
            if (attractions.get(i).getAttractionId().equals(updatedAttraction.getAttractionId())) {
                attractions.set(i, updatedAttraction);
                break;
            }
        }
        saveAttractions(attractions);
    }

    public void deleteAttraction(Attraction attractionToDelete) {
        List<Attraction> attractions = loadAttractions();
        attractions.removeIf(attraction -> attraction.getAttractionId().equals(attractionToDelete.getAttractionId()));
        saveAttractions(attractions);
    }
}