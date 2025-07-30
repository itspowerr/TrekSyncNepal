package com.trekking.app.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class EmergencyIncident {
    private String incidentId;
    private String bookingId;
    private String touristName;
    private String description;
    private LocalDateTime timestamp;

    public EmergencyIncident(String bookingId, String touristName, String description) {
        this.incidentId = UUID.randomUUID().toString();
        this.bookingId = bookingId;
        this.touristName = touristName;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public String getIncidentId() { return incidentId; }
    public String getBookingId() { return bookingId; }
    public String getTouristName() { return touristName; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
}