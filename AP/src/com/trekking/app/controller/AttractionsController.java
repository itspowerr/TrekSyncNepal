package com.trekking.app.controller;

import com.trekking.app.model.Attraction;
import com.trekking.app.model.data.AttractionManager;
import com.trekking.app.model.data.WeatherService;
import com.trekking.app.model.weather.WeatherResponse;
import com.trekking.app.util.UTF8Control;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class AttractionsController {

    @FXML private TableView<Attraction> attractionsTable;
    @FXML private TableColumn<Attraction, String> attractionNameCol;
    @FXML private TableColumn<Attraction, String> typeCol;
    @FXML private TableColumn<Attraction, Integer> altitudeCol;
    @FXML private TableColumn<Attraction, Double> priceCol;
    @FXML private Label formTitleLabel;
    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private TextField altitudeField;
    @FXML private TextField priceField;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;
    @FXML private Button saveButton;

    @FXML private VBox weatherPanel;
    @FXML private ImageView weatherIconView;
    @FXML private Label weatherCityLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label weatherDescriptionLabel;
    @FXML private Label feelsLikeLabel;
    @FXML private Label humidityLabel;
    @FXML private Label windSpeedLabel;

    private AttractionManager attractionManager;
    private WeatherService weatherService;
    private ObservableList<Attraction> attractionList;
    private Attraction currentlySelectedAttraction = null;
    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle("lang.lang", Locale.getDefault(), new UTF8Control());
        attractionManager = new AttractionManager();
        weatherService = new WeatherService();

        setupTable();
        refreshData();
        setupForm();

        attractionsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        populateForm(newSelection);
                        fetchWeatherForAttraction(newSelection);
                    } else {
                        weatherPanel.setVisible(false);
                    }
                }
        );
    }

    public void refreshData() {
        attractionList = FXCollections.observableArrayList(attractionManager.loadAttractions());
        attractionsTable.setItems(attractionList);
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
            protected WeatherResponse call() throws Exception {
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

    private void setupTable() {
        attractionNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        altitudeCol.setCellValueFactory(new PropertyValueFactory<>("altitude"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void setupForm() {
        saveButton.setOnAction(event -> handleSaveAction());
        deleteButton.setOnAction(event -> handleDeleteAction());
        clearButton.setOnAction(event -> clearFormAndSelection());
    }

    private void populateForm(Attraction attraction) {
        currentlySelectedAttraction = attraction;
        nameField.setText(attraction.getName());
        typeField.setText(attraction.getType());
        altitudeField.setText(String.valueOf(attraction.getAltitude()));
        priceField.setText(String.format("%.2f", attraction.getPrice()));
        formTitleLabel.setText("Edit Attraction Details");
        saveButton.setText("Update Attraction");
    }

    private void handleSaveAction() {
        if (nameField.getText().isEmpty() || altitudeField.getText().isEmpty() || priceField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Name, Altitude, and Price are required.");
            return;
        }

        int altitude;
        double price;
        try {
            altitude = Integer.parseInt(altitudeField.getText());
            price = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Altitude and Price must be valid numbers.");
            return;
        }

        if (currentlySelectedAttraction != null) {
            currentlySelectedAttraction.setName(nameField.getText());
            currentlySelectedAttraction.setType(typeField.getText());
            currentlySelectedAttraction.setAltitude(altitude);
            currentlySelectedAttraction.setPrice(price);
            attractionManager.updateAttraction(currentlySelectedAttraction);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Attraction details have been updated.");
        } else {
            Attraction newAttraction = new Attraction(nameField.getText(), typeField.getText(), altitude, price);
            attractionManager.addAttraction(newAttraction);
            attractionList.add(newAttraction);
            showAlert(Alert.AlertType.INFORMATION, "Success", "New attraction has been added.");
        }

        attractionsTable.refresh();
        clearFormAndSelection();
    }

    private void handleDeleteAction() {
        if (currentlySelectedAttraction == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an attraction from the table to delete.");
            return;
        }

        Optional<ButtonType> result = showConfirmation("Are you sure you want to delete " + currentlySelectedAttraction.getName() + "?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            attractionManager.deleteAttraction(currentlySelectedAttraction);
            attractionList.remove(currentlySelectedAttraction);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Attraction has been deleted.");
            clearFormAndSelection();
        }
    }

    private void clearFormAndSelection() {
        currentlySelectedAttraction = null;
        attractionsTable.getSelectionModel().clearSelection();
        nameField.clear();
        typeField.clear();
        altitudeField.clear();
        priceField.clear();
        formTitleLabel.setText("Add New Attraction");
        saveButton.setText("Save Attraction");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmation(String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}