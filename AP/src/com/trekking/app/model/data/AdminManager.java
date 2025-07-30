package com.trekking.app.model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.trekking.app.model.Admin;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AdminManager {

    private static final String FILE_PATH = "admins.json";
    private final Gson gson;

    public AdminManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public List<Admin> loadAdmins() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Admin>>() {}.getType();
            List<Admin> admins = gson.fromJson(reader, listType);
            return admins != null ? admins : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void saveAdmins(List<Admin> admins) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(admins, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addAdmin(Admin newAdmin) {
        List<Admin> admins = loadAdmins();
        admins.add(newAdmin);
        saveAdmins(admins);
    }

    public void deleteAdmin(Admin adminToDelete) {
        List<Admin> admins = loadAdmins();
        admins.removeIf(admin -> admin.getAdminId().equals(adminToDelete.getAdminId()));
        saveAdmins(admins);
    }
}