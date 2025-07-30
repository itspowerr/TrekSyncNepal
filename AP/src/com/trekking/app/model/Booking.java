package com.trekking.app.model;

import java.time.LocalDate;

public class Booking {
    private String bookingId;
    private String touristName;
    private String attractionName;
    private String guideName;
    private LocalDate bookingDate;
    private LocalDate startDate;
    private String status;
    private double totalCost;
    private String paymentStatus;
    private String discountApplied; // NEW FIELD

    public Booking(String bookingId, String touristName, String attractionName, String guideName, LocalDate bookingDate, LocalDate startDate, String status, double totalCost, String paymentStatus) {
        this.bookingId = bookingId;
        this.touristName = touristName;
        this.attractionName = attractionName;
        this.guideName = guideName;
        this.bookingDate = bookingDate;
        this.startDate = startDate;
        this.status = status;
        this.totalCost = totalCost;
        this.paymentStatus = paymentStatus;
        this.discountApplied = "None"; // Default value
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getTouristName() { return touristName; }
    public void setTouristName(String touristName) { this.touristName = touristName; }
    public String getAttractionName() { return attractionName; }
    public void setAttractionName(String attractionName) { this.attractionName = attractionName; }
    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    // GETTER AND SETTER FOR NEW FIELD
    public String getDiscountApplied() { return discountApplied; }
    public void setDiscountApplied(String discountApplied) { this.discountApplied = discountApplied; }
}