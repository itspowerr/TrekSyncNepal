package com.trekking.app.model.data;

import com.trekking.app.model.Admin;

import java.util.List;

public class AdminService {

    private final AdminManager adminManager;
    private static Admin currentAdmin = null; // To track the logged-in admin

    public AdminService() {
        this.adminManager = new AdminManager();
        createDefaultAdminIfNoneExist();
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        List<Admin> admins = adminManager.loadAdmins();
        for (Admin admin : admins) {
            if (admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
                currentAdmin = admin; // Set the current admin on successful login
                return true;
            }
        }
        return false;
    }

    public void logout() {
        currentAdmin = null;
    }

    public static Admin getCurrentAdmin() {
        return currentAdmin;
    }

    private void createDefaultAdminIfNoneExist() {
        List<Admin> admins = adminManager.loadAdmins();
        if (admins.isEmpty()) {
            System.out.println("No admins found. Creating default admin user.");
            Admin defaultAdmin = new Admin("admin", "admin");
            admins.add(defaultAdmin);
            adminManager.saveAdmins(admins);
        }
    }
}