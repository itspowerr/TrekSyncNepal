package com.trekking.app.controller;

import com.trekking.app.TrekkingApp;
import com.trekking.app.model.*;
import com.trekking.app.model.data.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class BookingController {

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdCol;
    @FXML private TableColumn<Booking, String> touristNameCol;
    @FXML private TableColumn<Booking, String> attractionCol;
    @FXML private TableColumn<Booking, String> guideCol;
    @FXML private TableColumn<Booking, LocalDate> dateCol;
    @FXML private TableColumn<Booking, Double> totalCostCol;
    @FXML private TableColumn<Booking, String> statusCol;
    @FXML private TableColumn<Booking, String> paymentStatusCol;
    @FXML private TableColumn<Booking, String> discountAppliedCol;
    @FXML private ComboBox<Tourist> selectTouristComboBox;
    @FXML private ComboBox<Attraction> selectAttractionComboBox;
    @FXML private ComboBox<Guide> selectGuideComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private Button createBookingButton;
    @FXML private Button approveButton;
    @FXML private Button cancelButton;
    @FXML private Button reportEmergencyButton;
    @FXML private Button processPaymentButton;
    @FXML private Label priceDisplayLabel;

    // NEW FXML Fields for Search
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button clearSearchButton;

    private BookingManager bookingManager;
    private TouristManager touristManager;
    private AttractionManager attractionManager;
    private GuideManager guideManager;
    private DiscountService discountService;
    private List<Booking> allBookings; // Master list of all bookings
    private ObservableList<Booking> bookingList; // List for display in the table

    @FXML
    public void initialize() {
        bookingManager = new BookingManager();
        touristManager = new TouristManager();
        attractionManager = new AttractionManager();
        guideManager = new GuideManager();
        discountService = new DiscountService();

        setupTableColumns();
        refreshData();

        // Set up button actions
        createBookingButton.setOnAction(event -> createNewBooking());
        approveButton.setOnAction(event -> handleApproveAction());
        cancelButton.setOnAction(event -> handleCancelAction());
        reportEmergencyButton.setOnAction(event -> handleReportEmergency());
        processPaymentButton.setOnAction(event -> handleProcessPayment());

        // NEW: Set up search button actions
        searchButton.setOnAction(event -> handleSearch());
        clearSearchButton.setOnAction(event -> handleClearSearch());

        selectAttractionComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterGuidesForAttraction(newVal);
                priceDisplayLabel.setText(String.format("$%.2f", newVal.getPrice()));
            } else {
                selectGuideComboBox.getItems().clear();
                selectGuideComboBox.setDisable(true);
                priceDisplayLabel.setText("N/A");
            }
        });
    }

    private void setupTableColumns() {
        bookingIdCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        touristNameCol.setCellValueFactory(new PropertyValueFactory<>("touristName"));
        attractionCol.setCellValueFactory(new PropertyValueFactory<>("attractionName"));
        guideCol.setCellValueFactory(new PropertyValueFactory<>("guideName"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        paymentStatusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        discountAppliedCol.setCellValueFactory(new PropertyValueFactory<>("discountApplied"));
    }

    public void refreshData() {
        allBookings = new ArrayList<>(bookingManager.loadBookings()); // Load into master list
        bookingList = FXCollections.observableArrayList(allBookings); // Populate table list from master
        bookingsTable.setItems(bookingList);

        selectTouristComboBox.setItems(FXCollections.observableArrayList(touristManager.loadTourists()));
        selectAttractionComboBox.setItems(FXCollections.observableArrayList(attractionManager.loadAttractions()));
        selectGuideComboBox.getItems().clear();
        selectGuideComboBox.setDisable(true);
    }

    // NEW: Method to handle searching
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            bookingsTable.setItems(FXCollections.observableArrayList(allBookings));
            return;
        }

        List<Booking> filteredBookings = allBookings.stream()
                .filter(booking -> booking.getBookingId().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        bookingsTable.setItems(FXCollections.observableArrayList(filteredBookings));
    }

    // NEW: Method to clear the search and show all bookings
    private void handleClearSearch() {
        searchField.clear();
        bookingsTable.setItems(FXCollections.observableArrayList(allBookings));
    }

    private void createNewBooking() {
        Tourist selectedTourist = selectTouristComboBox.getValue();
        Attraction selectedAttraction = selectAttractionComboBox.getValue();
        Guide selectedGuide = selectGuideComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();

        if (selectedTourist == null || selectedAttraction == null || selectedGuide == null || startDate == null) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Please fill in all booking details.");
            return;
        }

        String newBookingId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Booking newBooking = new Booking(
                newBookingId,
                selectedTourist.toString(),
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
        refreshData(); // Refresh all data to include the new booking
        showAlert(Alert.AlertType.INFORMATION, "Success", "New booking has been created with ID: " + newBookingId);
    }

    private void handleProcessPayment() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a booking to process payment.");
            return;
        }

        if ("Paid".equals(selectedBooking.getPaymentStatus())) {
            showAlert(Alert.AlertType.INFORMATION, "Already Paid", "This booking has already been paid for.");
            return;
        }

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

    private void filterGuidesForAttraction(Attraction selectedAttraction) {
        selectGuideComboBox.setDisable(false);
        List<Guide> allGuides = guideManager.loadGuides();
        List<Guide> qualifiedGuides = allGuides.stream()
                .filter(guide -> guide.getAssignedAttractionIds() != null &&
                        guide.getAssignedAttractionIds().contains(selectedAttraction.getAttractionId()))
                .collect(Collectors.toList());
        selectGuideComboBox.setItems(FXCollections.observableArrayList(qualifiedGuides));
    }

    private void handleApproveAction() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a booking from the table to approve.");
            return;
        }
        selectedBooking.setStatus("Confirmed");
        bookingManager.updateBooking(selectedBooking);
        bookingsTable.refresh();
    }

    private void handleCancelAction() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a booking from the table to cancel.");
            return;
        }
        selectedBooking.setStatus("Cancelled");
        bookingManager.updateBooking(selectedBooking);
        bookingsTable.refresh();
    }

    private void handleReportEmergency() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a booking to report an emergency for.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Report Emergency");
        dialog.setHeaderText("Report an emergency for Booking ID: " + selectedBooking.getBookingId());
        dialog.setContentText("Please enter a brief description of the incident:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(description -> {
            if (!description.isEmpty()) {
                EmergencyIncident incident = new EmergencyIncident(
                        selectedBooking.getBookingId(),
                        selectedBooking.getTouristName(),
                        description
                );
                new EmergencyManager().addIncident(incident);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Emergency has been logged successfully.");
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}