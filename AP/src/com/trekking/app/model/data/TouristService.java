package com.trekking.app.model.data;

import com.trekking.app.model.Tourist;
import java.util.List;

public class TouristService {

    private final TouristManager touristManager;
    private static Tourist currentTourist = null;

    public TouristService() {
        this.touristManager = new TouristManager();
    }

    public boolean login(String username, String password) {
        if (username == null || password == null || username.isEmpty()) {
            return false;
        }

        List<Tourist> tourists = touristManager.loadTourists();
        for (Tourist tourist : tourists) {
            // UPDATED to be null-safe: only check credentials if they exist
            if (tourist.getUsername() != null && tourist.getUsername().equals(username) &&
                    tourist.getPassword() != null && tourist.getPassword().equals(password)) {

                currentTourist = tourist;
                return true;
            }
        }
        return false;
    }

    public void logout() {
        currentTourist = null;
    }

    public static Tourist getCurrentTourist() {
        return currentTourist;
    }
}