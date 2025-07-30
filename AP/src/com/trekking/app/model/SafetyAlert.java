package com.trekking.app.model;

public class SafetyAlert {
    private String alertMessage;
    private int minAltitude;

    public SafetyAlert(String alertMessage, int minAltitude) {
        this.alertMessage = alertMessage;
        this.minAltitude = minAltitude;
    }

    public String getAlertMessage() { return alertMessage; }
    public void setAlertMessage(String alertMessage) { this.alertMessage = alertMessage; }
    public int getMinAltitude() { return minAltitude; }
    public void setMinAltitude(int minAltitude) { this.minAltitude = minAltitude; }

    public boolean checkAlert(Attraction attraction) {
        return attraction.getAltitude() >= this.minAltitude;
    }
}