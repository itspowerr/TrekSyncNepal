package com.trekking.app.model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.trekking.app.model.Booking;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingManager {

    private static final String FILE_PATH = "bookings.json";
    private final Gson gson;

    public BookingManager() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
    }

    public List<Booking> loadBookings() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Booking>>() {}.getType();
            List<Booking> bookings = gson.fromJson(reader, listType);
            return bookings != null ? bookings : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void saveBookings(List<Booking> bookings) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(bookings, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addBooking(Booking newBooking) {
        List<Booking> bookings = loadBookings();
        bookings.add(newBooking);
        saveBookings(bookings);
    }

    public void updateBooking(Booking updatedBooking) {
        List<Booking> bookings = loadBookings();
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getBookingId().equals(updatedBooking.getBookingId())) {
                bookings.set(i, updatedBooking);
                break;
            }
        }
        saveBookings(bookings);
    }

    public void deleteBooking(Booking bookingToDelete) {
        List<Booking> bookings = loadBookings();
        bookings.removeIf(booking -> booking.getBookingId().equals(bookingToDelete.getBookingId()));
        saveBookings(bookings);
    }
}