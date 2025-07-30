package com.trekking.app.model;

import java.util.UUID;

public class Admin {
    private String adminId;
    private String username;
    private String password;

    public Admin(String username, String password) {
        this.adminId = UUID.randomUUID().toString(); // Generate a unique ID
        this.username = username;
        this.password = password;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return username;
    }
}