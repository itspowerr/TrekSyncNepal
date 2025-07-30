package com.trekking.app.model;

import java.util.UUID;

public class Attraction {
    private String attractionId;
    private String name;
    private String type;
    private int altitude;
    private double price; // The price for the attraction

    // Updated constructor to include price
    public Attraction(String name, String type, int altitude, double price) {
        this.attractionId = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.altitude = altitude;
        this.price = price;
    }

    // Getters and Setters
    public String getAttractionId() { return attractionId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getAltitude() { return altitude; }
    public void setAltitude(int altitude) { this.altitude = altitude; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return name;
    }
}