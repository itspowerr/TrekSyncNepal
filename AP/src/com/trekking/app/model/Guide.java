package com.trekking.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Guide {
    private String guideId;
    private String guideName;
    private List<String> languages;
    private String contact;
    private int experienceYears;
    private List<String> assignedAttractionIds; // NEW FIELD

    public Guide(String guideName, List<String> languages, String contact, int experienceYears) {
        this.guideId = UUID.randomUUID().toString();
        this.guideName = guideName;
        this.languages = languages;
        this.contact = contact;
        this.experienceYears = experienceYears;
        this.assignedAttractionIds = new ArrayList<>(); // Initialize empty list
    }

    // Getters and Setters
    public String getGuideId() { return guideId; }
    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }
    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    // GETTER AND SETTER FOR NEW FIELD
    public List<String> getAssignedAttractionIds() { return assignedAttractionIds; }
    public void setAssignedAttractionIds(List<String> assignedAttractionIds) { this.assignedAttractionIds = assignedAttractionIds; }

    @Override
    public String toString() {
        return guideName;
    }
}