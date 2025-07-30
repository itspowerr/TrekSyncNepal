package com.trekking.app.controller;

import com.trekking.app.TrekkingApp;
import com.trekking.app.model.Admin;
import com.trekking.app.model.data.AdminService;
import com.trekking.app.model.data.WeatherService;
import com.trekking.app.model.weather.WeatherResponse;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Locale;

public class AdminDashboardController {

    @FXML private TabPane adminTabPane;
    @FXML private Label loggedInUserLabel;
    @FXML private ChoiceBox<String> languageChoice;
    @FXML private CheckBox darkModeToggle;
    @FXML private ImageView headerWeatherIcon;
    @FXML private Label headerWeatherLabel;

    // Tabs
    @FXML private Tab overviewTab;
    @FXML private Tab touristTab;
    @FXML private Tab guideTab;
    @FXML private Tab attractionTab;
    @FXML private Tab bookingTab;
    @FXML private Tab festivalTab;
    @FXML private Tab adminTab;
    @FXML private Tab emergencyTab;

    // Injected Controllers
    @FXML private AdminOverviewController overviewContentController;
    @FXML private TouristRegistrationController touristContentController;
    @FXML private GuideRegistrationController guideContentController;
    @FXML private AttractionsController attractionContentController;
    @FXML private BookingController bookingContentController;
    @FXML private AdminManageFestivalsController festivalContentController;
    @FXML private AdminManageAdminsController adminContentController;
    @FXML private EmergencyLogController emergencyContentController;

    private AdminService adminService;
    private WeatherService weatherService;

    @FXML
    public void initialize() {
        adminService = new AdminService();
        weatherService = new WeatherService();

        setupUserDetails();
        setupTopBarControls();
        setupTabListeners();
        loadHeaderWeather();
    }

    private void setupUserDetails() {
        Admin currentUser = AdminService.getCurrentAdmin();
        if (currentUser != null) {
            loggedInUserLabel.setText("User: " + currentUser.getUsername());
        }
    }

    private void setupTopBarControls() {
        languageChoice.getItems().addAll("en", "ne", "ja");
        languageChoice.setValue(Locale.getDefault().getLanguage());
        languageChoice.setOnAction(this::changeLanguage);
        darkModeToggle.setSelected(TrekkingApp.isDarkModeActive());
        darkModeToggle.setOnAction(this::toggleDarkMode);
    }

    private void setupTabListeners() {
        overviewTab.setOnSelectionChanged(event -> {
            if (overviewTab.isSelected() && overviewContentController != null) {
                overviewContentController.refreshData();
            }
        });
        touristTab.setOnSelectionChanged(event -> {
            if (touristTab.isSelected() && touristContentController != null) {
                touristContentController.refreshData();
            }
        });
        guideTab.setOnSelectionChanged(event -> {
            if (guideTab.isSelected() && guideContentController != null) {
                guideContentController.refreshData();
            }
        });
        attractionTab.setOnSelectionChanged(event -> {
            if (attractionTab.isSelected() && attractionContentController != null) {
                attractionContentController.refreshData();
            }
        });
        bookingTab.setOnSelectionChanged(event -> {
            if (bookingTab.isSelected() && bookingContentController != null) {
                bookingContentController.refreshData();
            }
        });
        festivalTab.setOnSelectionChanged(event -> {
            if (festivalTab.isSelected() && festivalContentController != null) {
                festivalContentController.refreshData();
            }
        });
        adminTab.setOnSelectionChanged(event -> {
            if (adminTab.isSelected() && adminContentController != null) {
                adminContentController.refreshData();
            }
        });
        emergencyTab.setOnSelectionChanged(event -> {
            if (emergencyTab.isSelected() && emergencyContentController != null) {
                emergencyContentController.refreshData();
            }
        });
    }

    private void loadHeaderWeather() {
        Task<WeatherResponse> weatherTask = new Task<>() {
            @Override
            protected WeatherResponse call() {
                return weatherService.getWeatherForCity("Pokhara");
            }
        };

        weatherTask.setOnSucceeded(event -> {
            WeatherResponse response = weatherTask.getValue();
            if (response != null && response.weather != null && !response.weather.isEmpty()) {
                String iconCode = response.weather.get(0).icon;
                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                headerWeatherIcon.setImage(new Image(iconUrl));
                headerWeatherLabel.setText(String.format("%.0f°C", response.main.temp));
            }
        });

        new Thread(weatherTask).start();
    }

    @FXML
    private void handleLogoutButtonAction(ActionEvent event) {
        adminService.logout();
        TrekkingApp.showLoginScreen();
    }

    @FXML
    private void changeLanguage(ActionEvent event) {
        String lang = languageChoice.getValue();
        Locale.setDefault(new Locale(lang));
        TrekkingApp.showAdminDashboard(TrekkingApp.isDarkModeActive());
    }

    @FXML
    private void toggleDarkMode(ActionEvent event) {
        boolean isSelected = darkModeToggle.isSelected();
        TrekkingApp.isDarkModeActive = isSelected;
        TrekkingApp.showAdminDashboard(isSelected);
    }

    @FXML private void showOverview() { adminTabPane.getSelectionModel().select(overviewTab); }
    @FXML private void showTouristRegistration() { adminTabPane.getSelectionModel().select(touristTab); }
    @FXML private void showGuideRegistration() { adminTabPane.getSelectionModel().select(guideTab); }
    @FXML private void showAttractions() { adminTabPane.getSelectionModel().select(attractionTab); }
    @FXML private void showBooking() { adminTabPane.getSelectionModel().select(bookingTab); }
    @FXML private void showFestivalOffers() { adminTabPane.getSelectionModel().select(festivalTab); }
    @FXML private void showAdminManagement() { adminTabPane.getSelectionModel().select(adminTab); }
    @FXML private void showEmergencyLog() { adminTabPane.getSelectionModel().select(emergencyTab); }
    @FXML private void showReports() { adminTabPane.getSelectionModel().select(8); }
}