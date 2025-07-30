package com.trekking.app.controller;

import com.trekking.app.model.*;
import com.trekking.app.model.data.*;
import com.trekking.app.model.weather.WeatherResponse;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TouristBookingController {

    @FXML private ComboBox<Attraction> selectAttractionComboBox;
    @FXML private ComboBox<Guide> selectGuideComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private Button submitBookingButton;

    @FXML private VBox weatherPanel;
    @FXML private ImageView weatherIconView;
    @FXML private Label weatherCityLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label weatherDescriptionLabel;
    @FXML private Label feelsLikeLabel;
    @FXML private Label humidityLabel;
    @FXML private Label windSpeedLabel;

    private BookingManager bookingManager;
    private GuideManager guideManager;
    private DiscountService discountService;
    private WeatherService weatherService;

    @FXML
    public void initialize() {
        bookingManager = new BookingManager();
        guideManager = new GuideManager();
        discountService = new DiscountService();
        weatherService = new WeatherService();
        AttractionManager attractionManager = new AttractionManager();

        selectAttractionComboBox.setItems(FXCollections.observableArrayList(attractionManager.loadAttractions()));
        selectGuideComboBox.setDisable(true);
        submitBookingButton.setOnAction(event -> handleSubmitBooking());

        selectAttractionComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterGuidesForAttraction(newVal);
                fetchWeatherForAttraction(newVal);
            } else {
                selectGuideComboBox.getItems().clear();
                selectGuideComboBox.setDisable(true);
                weatherPanel.setVisible(false);
            }
        });
    }

    private void fetchWeatherForAttraction(Attraction attraction) {
        weatherPanel.setVisible(true);
        weatherCityLabel.setText("Weather in " + attraction.getName());
        temperatureLabel.setText("--°C");
        weatherDescriptionLabel.setText("Loading...");
        feelsLikeLabel.setText("--°C");
        humidityLabel.setText("--%");
        windSpeedLabel.setText("-- m/s");
        weatherIconView.setImage(null);

        Task<WeatherResponse> weatherTask = new Task<>() {
            @Override
            protected WeatherResponse call() {
                return weatherService.getWeatherForCity(attraction.getName());
            }
        };

        weatherTask.setOnSucceeded(event -> {
            WeatherResponse response = weatherTask.getValue();
            if (response != null && response.weather != null && !response.weather.isEmpty()) {
                String description = response.weather.get(0).description;
                String iconCode = response.weather.get(0).icon;
                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

                temperatureLabel.setText(String.format("%.1f°C", response.main.temp));
                weatherDescriptionLabel.setText(description);
                feelsLikeLabel.setText(String.format("%.1f°C", response.main.feels_like));
                humidityLabel.setText(response.main.humidity + "%");
                windSpeedLabel.setText(String.format("%.1f m/s", response.wind.speed));
                weatherIconView.setImage(new Image(iconUrl));
            } else {
                weatherDescriptionLabel.setText("Weather data not found.");
            }
        });

        weatherTask.setOnFailed(event -> {
            weatherDescriptionLabel.setText("Failed to load weather.");
        });

        new Thread(weatherTask).start();
    }

    private void filterGuidesForAttraction(Attraction selectedAttraction) {
        selectGuideComboBox.setDisable(false);
        List<Guide> allGuides = guideManager.loadGuides();
        List<Guide> qualifiedGuides = allGuides.stream()
                .filter(guide -> guide.getAssignedAttractionIds() != null &&
                        guide.getAssignedAttractionIds().contains(selectedAttraction.getAttractionId()))
                .collect(Collectors.toList());
        selectGuideComboBox.setItems(FXCollections.observableArrayList(qualifiedGuides));
    }

    private void handleSubmitBooking() {
        Tourist currentTourist = TouristService.getCurrentTourist();
        Attraction selectedAttraction = selectAttractionComboBox.getValue();
        Guide selectedGuide = selectGuideComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();

        if (currentTourist == null || selectedAttraction == null || selectedGuide == null || startDate == null) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Please complete all fields.");
            return;
        }

        if (selectedAttraction.getAltitude() > 3000) {
            showAlert(Alert.AlertType.WARNING, "Safety Alert", "Altitude Sickness Warning: This trek is above 3,000m. Ensure proper acclimatization.");
        }

        Month month = startDate.getMonth();
        if ((month == Month.JUNE || month == Month.JULY || month == Month.AUGUST) && selectedAttraction.getAltitude() > 3000) {
            showAlert(Alert.AlertType.ERROR, "Booking Rejected", "This high-altitude trek cannot be booked during the monsoon season (June-August).");
            return;
        }

        String newBookingId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Booking newBooking = new Booking(
                newBookingId,
                currentTourist.toString(),
                selectedAttraction.toString(),
                selectedGuide.toString(),
                LocalDate.now(),
                startDate,
                "Pending",
                selectedAttraction.getPrice(),
                "Unpaid"
        );

        discountService.applyDiscountIfApplicable(newBooking, selectedAttraction);

        bookingManager.addBooking(newBooking);

        String successMessage = "Your booking request has been submitted! An admin will review it shortly.";
        if (!"None".equals(newBooking.getDiscountApplied())) {
            successMessage += "\n\nCongratulations! The following discount was applied: " + newBooking.getDiscountApplied();
        }
        showAlert(Alert.AlertType.INFORMATION, "Success", successMessage);

        Stage stage = (Stage) submitBookingButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}