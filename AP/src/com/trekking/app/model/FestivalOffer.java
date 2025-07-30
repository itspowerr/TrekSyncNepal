package com.trekking.app.model;

import java.time.LocalDate;

public class FestivalOffer {
    private String festivalName;
    private double discountRate; // e.g., 0.15 for 15%
    private LocalDate startDate;
    private LocalDate endDate;

    public FestivalOffer(String festivalName, double discountRate, LocalDate startDate, LocalDate endDate) {
        this.festivalName = festivalName;
        this.discountRate = discountRate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getFestivalName() { return festivalName; }
    public void setFestivalName(String festivalName) { this.festivalName = festivalName; }
    public double getDiscountRate() { return discountRate; }
    public void setDiscountRate(double discountRate) { this.discountRate = discountRate; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean isOfferApplicable(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}