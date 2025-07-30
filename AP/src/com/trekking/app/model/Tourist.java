package com.trekking.app.model;

import java.time.LocalDate;

public class Tourist {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String nationality;
    private String passportNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String emergencyContactName;
    private String emergencyContactNumber;

    // Constructor for tourists WITH an account
    public Tourist(String username, String password, String firstName, String lastName, String email, String phoneNumber, String nationality, String passportNumber, LocalDate dateOfBirth, String gender, String emergencyContactName, String emergencyContactNumber) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.nationality = nationality;
        this.passportNumber = passportNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactNumber = emergencyContactNumber;
    }

    // Constructor for walk-in tourists WITHOUT an account
    public Tourist(String firstName, String lastName, String email, String phoneNumber, String passportNumber) {
        this.username = null;
        this.password = null;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passportNumber = passportNumber;
        this.nationality = "";
        this.dateOfBirth = null;
        this.gender = "";
        this.emergencyContactName = "";
        this.emergencyContactNumber = "";
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    public String getEmergencyContactNumber() { return emergencyContactNumber; }
    public void setEmergencyContactNumber(String emergencyContactNumber) { this.emergencyContactNumber = emergencyContactNumber; }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}