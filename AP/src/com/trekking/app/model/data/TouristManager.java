package com.trekking.app.model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.trekking.app.model.Tourist;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TouristManager {

    private static final String FILE_PATH = "tourists.json";
    private final Gson gson;

    public TouristManager() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
    }

    public List<Tourist> loadTourists() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Tourist>>() {}.getType();
            List<Tourist> tourists = gson.fromJson(reader, listType);
            return tourists != null ? tourists : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void saveTourists(List<Tourist> tourists) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(tourists, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTourist(Tourist newTourist) {
        List<Tourist> tourists = loadTourists();
        tourists.add(newTourist);
        saveTourists(tourists);
    }

    public void updateTourist(Tourist updatedTourist) {
        List<Tourist> tourists = loadTourists();
        for (int i = 0; i < tourists.size(); i++) {
            // Assuming passportNumber is a unique identifier
            if (tourists.get(i).getPassportNumber().equals(updatedTourist.getPassportNumber())) {
                tourists.set(i, updatedTourist);
                break;
            }
        }
        saveTourists(tourists);
    }

    public void deleteTourist(Tourist touristToDelete) {
        List<Tourist> tourists = loadTourists();
        tourists.removeIf(tourist -> tourist.getPassportNumber().equals(touristToDelete.getPassportNumber()));
        saveTourists(tourists);
    }
}