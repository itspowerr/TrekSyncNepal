package com.trekking.app.model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.trekking.app.model.Guide;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GuideManager {

    private static final String FILE_PATH = "guides.json";
    private final Gson gson;

    public GuideManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public List<Guide> loadGuides() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Guide>>() {}.getType();
            List<Guide> guides = gson.fromJson(reader, listType);
            return guides != null ? guides : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void saveGuides(List<Guide> guides) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(guides, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addGuide(Guide newGuide) {
        List<Guide> guides = loadGuides();
        guides.add(newGuide);
        saveGuides(guides);
    }

    public void updateGuide(Guide updatedGuide) {
        List<Guide> guides = loadGuides();
        for (int i = 0; i < guides.size(); i++) {
            if (guides.get(i).getGuideId().equals(updatedGuide.getGuideId())) {
                guides.set(i, updatedGuide);
                break;
            }
        }
        saveGuides(guides);
    }

    public void deleteGuide(Guide guideToDelete) {
        List<Guide> guides = loadGuides();
        guides.removeIf(guide -> guide.getGuideId().equals(guideToDelete.getGuideId()));
        saveGuides(guides);
    }
}