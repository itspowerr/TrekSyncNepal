package com.trekking.app.model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.trekking.app.model.FestivalOffer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FestivalOfferManager {

    private static final String FILE_PATH = "festivals.json";
    private final Gson gson;

    public FestivalOfferManager() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
    }

    public List<FestivalOffer> loadOffers() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<FestivalOffer>>() {}.getType();
            List<FestivalOffer> offers = gson.fromJson(reader, listType);
            return offers != null ? offers : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void saveOffers(List<FestivalOffer> offers) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(offers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addOffer(FestivalOffer newOffer) {
        List<FestivalOffer> offers = loadOffers();
        offers.add(newOffer);
        saveOffers(offers);
    }

    public void deleteOffer(FestivalOffer offerToDelete) {
        List<FestivalOffer> offers = loadOffers();
        offers.removeIf(offer -> offer.getFestivalName().equals(offerToDelete.getFestivalName()));
        saveOffers(offers);
    }
}