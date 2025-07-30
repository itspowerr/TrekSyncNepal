package com.trekking.app.controller;

import com.trekking.app.TrekkingApp;
import com.trekking.app.model.Booking;
import com.trekking.app.model.Guide;
import com.trekking.app.model.data.BookingManager;
import com.trekking.app.model.data.GuideManager;
import com.trekking.app.model.data.TouristService;
import com.trekking.app.model.data.WeatherService;
import com.trekking.app.model.weather.WeatherResponse;
import com.trekking.app.model.Tourist;
import com.trekking.app.util.UTF8Control;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TouristDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private ChoiceBox<String> languageChoice;
    @FXML private CheckBox darkModeToggle;

    @FXML private ImageView headerWeatherIcon;
    @FXML private Label headerWeatherLabel;

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> attractionCol;
    @FXML private TableColumn<Booking, LocalDate> startDateCol;
    @FXML private TableColumn<Booking, Double> totalCostCol;
    @FXML private TableColumn<Booking, String> statusCol;
    @FXML private TableColumn<Booking, String> paymentStatusCol;
    @FXML private TableColumn<Booking, String> guideNameCol;
    @FXML private TableColumn<Booking, String> discountAppliedCol;
    @FXML private Button payNowButton;
    @FXML private VBox guideDetailsPane;
    @FXML private Label guideNameLabel;
    @FXML private Label guideContactLabel;
    @FXML private Label guideExperienceLabel;

    private TouristService touristService;
    private BookingManager bookingManager;
    private GuideManager guideManager;
    private WeatherService weatherService;

    @FXML
    public void initialize() {
        touristService = new TouristService();
        bookingManager = new BookingManager();
        guideManager = new GuideManager();
        weatherService = new WeatherService();

        setupWelcomeLabel();
        setupTable();
        loadMyBookings();

        guideDetailsPane.setVisible(false);
        payNowButton.setOnAction(event -> handlePayNow());

        bookingsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && "Confirmed".equals(newSelection.getStatus()) && "Unpaid".equals(newSelection.getPaymentStatus())) {
                payNowButton.setDisable(false);
            } else {
                payNowButton.setDisable(true);
            }

            if (newSelection != null) {
                displayGuideDetails(newSelection);
            }
        });

        languageChoice.getItems().addAll("en", "ne", "ja");
        languageChoice.setValue(Locale.getDefault().getLanguage());
        languageChoice.setOnAction(this::changeLanguage);
        darkModeToggle.setSelected(TrekkingApp.isDarkModeActive());
        darkModeToggle.setOnAction(this::toggleDarkMode);

        loadHeaderWeather();
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

    private void setupTable() {
        attractionCol.setCellValueFactory(new PropertyValueFactory<>("attractionName"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        paymentStatusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        guideNameCol.setCellValueFactory(new PropertyValueFactory<>("guideName"));
        discountAppliedCol.setCellValueFactory(new PropertyValueFactory<>("discountApplied"));
    }

    private void loadMyBookings() {
        Tourist currentTourist = TouristService.getCurrentTourist();
        if (currentTourist == null) return;

        List<Booking> allBookings = bookingManager.loadBookings();
        List<Booking> myBookings = allBookings.stream()
                .filter(booking -> currentTourist.toString().equals(booking.getTouristName()))
                .collect(Collectors.toList());

        ObservableList<Booking> bookingList = FXCollections.observableArrayList(myBookings);
        bookingsTable.setItems(bookingList);
    }

    private void handlePayNow() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) { return; }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/payment-dialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Process Payment");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(TrekkingApp.getPrimaryStage());
            Scene scene = new Scene(page);
            if (TrekkingApp.isDarkModeActive()) {
                scene.getStylesheets().add(TrekkingApp.class.getResource("/dark-styles.css").toExternalForm());
            } else {
                scene.getStylesheets().add(TrekkingApp.class.getResource("/styles.css").toExternalForm());
            }
            dialogStage.setScene(scene);

            PaymentDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setBooking(selectedBooking);
            dialogStage.showAndWait();

            if (controller.isPaymentSuccessful()) {
                selectedBooking.setPaymentStatus("Paid");
                bookingManager.updateBooking(selectedBooking);
                bookingsTable.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Payment for booking " + selectedBooking.getBookingId() + " was successful.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void bookTour() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("lang.lang", Locale.getDefault(), new UTF8Control());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/tourist-booking.fxml"), bundle);
            Parent root = loader.load();

            Stage bookingStage = new Stage();
            bookingStage.setTitle("New Booking Request");
            bookingStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            if (TrekkingApp.isDarkModeActive()) {
                scene.getStylesheets().add(TrekkingApp.class.getResource("/dark-styles.css").toExternalForm());
            } else {
                scene.getStylesheets().add(TrekkingApp.class.getResource("/styles.css").toExternalForm());
            }
            bookingStage.setScene(scene);
            bookingStage.showAndWait();
            loadMyBookings();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupWelcomeLabel() {
        Tourist currentTourist = TouristService.getCurrentTourist();
        if (currentTourist != null) {
            welcomeLabel.setText("Welcome, " + currentTourist.getFirstName() + "!");
        } else {
            welcomeLabel.setText("Welcome, Guest!");
        }
    }

    private void displayGuideDetails(Booking selectedBooking) {
        String guideName = selectedBooking.getGuideName();
        if (guideName == null || guideName.isEmpty()) {
            guideDetailsPane.setVisible(false);
            return;
        }

        Guide assignedGuide = guideManager.loadGuides().stream()
                .filter(guide -> guide.getGuideName().equals(guideName))
                .findFirst()
                .orElse(null);

        if (assignedGuide != null) {
            guideNameLabel.setText(assignedGuide.getGuideName());
            guideContactLabel.setText(assignedGuide.getContact());
            guideExperienceLabel.setText(assignedGuide.getExperienceYears() + " years");
            guideDetailsPane.setVisible(true);
        } else {
            guideDetailsPane.setVisible(false);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogoutButtonAction(ActionEvent event) {
        touristService.logout();
        TrekkingApp.showLoginScreen();
    }

    @FXML private void changeLanguage(ActionEvent event) {
        String lang = languageChoice.getValue();
        Locale.setDefault(new Locale(lang));
        TrekkingApp.showTouristDashboard(TrekkingApp.isDarkModeActive());
    }

    @FXML private void toggleDarkMode(ActionEvent event) {
        // UPDATED LOGIC: Reload the dashboard with the new mode setting
        boolean isSelected = darkModeToggle.isSelected();
        TrekkingApp.isDarkModeActive = isSelected;
        TrekkingApp.showTouristDashboard(isSelected);
    }
}