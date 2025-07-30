package com.trekking.app.controller;

import com.trekking.app.TrekkingApp;
import com.trekking.app.model.*;
import com.trekking.app.model.data.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsController {
    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private Button generateReportButton;
    @FXML private Button exportButton;
    @FXML private TableView reportsTable;

    private TouristManager touristManager;
    private AdminManager adminManager;
    private EmergencyManager emergencyManager;
    private BookingManager bookingManager;
    private ExportService exportService;

    // Report Titles
    private static final String REPORT_TOURISTS_BY_NATIONALITY = "Tourists by Nationality";
    private static final String REPORT_FULL_TOURIST_DETAILS = "Full Tourist Details";
    private static final String REPORT_ADMIN_LIST = "List of All Admins";
    private static final String REPORT_EMERGENCY_LOG = "Emergency Incidents Log";
    private static final String REPORT_FINANCIAL_SUMMARY = "Financial Summary";

    @FXML
    public void initialize() {
        touristManager = new TouristManager();
        adminManager = new AdminManager();
        emergencyManager = new EmergencyManager();
        bookingManager = new BookingManager();
        exportService = new ExportService();

        reportTypeComboBox.getItems().addAll(
                REPORT_TOURISTS_BY_NATIONALITY,
                REPORT_FULL_TOURIST_DETAILS,
                REPORT_ADMIN_LIST,
                REPORT_EMERGENCY_LOG,
                REPORT_FINANCIAL_SUMMARY
        );

        generateReportButton.setOnAction(event -> handleGenerateReport());
        exportButton.setOnAction(event -> handleExport());
    }

    private void handleGenerateReport() {
        String selectedReport = reportTypeComboBox.getValue();
        if (selectedReport == null || selectedReport.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a report type to generate.");
            return;
        }

        switch (selectedReport) {
            case REPORT_TOURISTS_BY_NATIONALITY:
                generateNationalityReport();
                break;
            case REPORT_FULL_TOURIST_DETAILS:
                generateFullTouristReport();
                break;
            case REPORT_ADMIN_LIST:
                generateAdminsReport();
                break;
            case REPORT_EMERGENCY_LOG:
                generateEmergencyLogReport();
                break;
            case REPORT_FINANCIAL_SUMMARY:
                generateFinancialSummaryReport();
                break;
        }
    }

    private void handleExport() {
        if (reportsTable.getItems().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Data", "There is no report data to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.setInitialFileName(reportTypeComboBox.getValue().replace(" ", "_") + ".csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(TrekkingApp.getPrimaryStage());

        if (file != null) {
            String selectedReport = reportTypeComboBox.getValue();

            if (REPORT_FINANCIAL_SUMMARY.equals(selectedReport)) {
                exportService.exportFinancialReportToCsv(reportsTable, file);
            } else {
                exportService.exportTableViewToCsv(reportsTable, file);
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "Report has been exported successfully to:\n" + file.getAbsolutePath());
        }
    }

    private void generateFinancialSummaryReport() {
        reportsTable.getColumns().clear();
        reportsTable.getItems().clear();

        List<Booking> paidBookings = bookingManager.loadBookings().stream()
                .filter(booking -> "Paid".equalsIgnoreCase(booking.getPaymentStatus()))
                .collect(Collectors.toList());

        ObservableList<Booking> reportData = FXCollections.observableArrayList(paidBookings);

        TableColumn<Booking, String> bookingIdCol = new TableColumn<>("Booking ID");
        bookingIdCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        TableColumn<Booking, String> touristNameCol = new TableColumn<>("Tourist Name");
        touristNameCol.setCellValueFactory(new PropertyValueFactory<>("touristName"));
        TableColumn<Booking, String> attractionCol = new TableColumn<>("Attraction");
        attractionCol.setCellValueFactory(new PropertyValueFactory<>("attractionName"));
        TableColumn<Booking, LocalDate> dateCol = new TableColumn<>("Start Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        TableColumn<Booking, Double> revenueCol = new TableColumn<>("Revenue ($)");
        revenueCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        reportsTable.getColumns().setAll(bookingIdCol, touristNameCol, attractionCol, dateCol, revenueCol);
        reportsTable.setItems(reportData);
        exportButton.setDisable(reportData.isEmpty());
    }

    private void generateEmergencyLogReport() {
        reportsTable.getColumns().clear();
        reportsTable.getItems().clear();
        List<EmergencyIncident> incidents = new EmergencyManager().loadIncidents();
        ObservableList<EmergencyIncident> reportData = FXCollections.observableArrayList(incidents);
        TableColumn<EmergencyIncident, LocalDateTime> timestampCol = new TableColumn<>("Date/Time");
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        TableColumn<EmergencyIncident, String> bookingIdCol = new TableColumn<>("Booking ID");
        bookingIdCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        TableColumn<EmergencyIncident, String> touristNameCol = new TableColumn<>("Tourist Name");
        touristNameCol.setCellValueFactory(new PropertyValueFactory<>("touristName"));
        TableColumn<EmergencyIncident, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        reportsTable.getColumns().setAll(timestampCol, bookingIdCol, touristNameCol, descriptionCol);
        reportsTable.setItems(reportData);
        exportButton.setDisable(reportData.isEmpty());
    }

    private void generateNationalityReport() {
        reportsTable.getColumns().clear();
        reportsTable.getItems().clear();
        List<Tourist> tourists = touristManager.loadTourists();
        Map<String, Long> nationalityCounts = tourists.stream()
                .filter(t -> t.getNationality() != null && !t.getNationality().isEmpty())
                .collect(Collectors.groupingBy(Tourist::getNationality, Collectors.counting()));
        ObservableList<Map.Entry<String, Long>> reportData = FXCollections.observableArrayList(nationalityCounts.entrySet());
        TableColumn<Map.Entry<String, Long>, String> nationalityCol = new TableColumn<>("Nationality");
        nationalityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        TableColumn<Map.Entry<String, Long>, Number> countCol = new TableColumn<>("Number of Tourists");
        countCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getValue().intValue()));
        reportsTable.getColumns().setAll(nationalityCol, countCol);
        reportsTable.setItems(reportData);
        exportButton.setDisable(reportData.isEmpty());
    }

    private void generateFullTouristReport() {
        reportsTable.getColumns().clear();
        reportsTable.getItems().clear();
        List<Tourist> tourists = touristManager.loadTourists();
        ObservableList<Tourist> reportData = FXCollections.observableArrayList(tourists);
        TableColumn<Tourist, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName() + " " + cellData.getValue().getLastName()));
        TableColumn<Tourist, String> passportCol = new TableColumn<>("Passport Number");
        passportCol.setCellValueFactory(new PropertyValueFactory<>("passportNumber"));
        TableColumn<Tourist, String> emailCol = new TableColumn<>("Email Address");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<Tourist, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        reportsTable.getColumns().setAll(nameCol, passportCol, emailCol, usernameCol);
        reportsTable.setItems(reportData);
        exportButton.setDisable(reportData.isEmpty());
    }

    private void generateAdminsReport() {
        reportsTable.getColumns().clear();
        reportsTable.getItems().clear();
        List<Admin> admins = adminManager.loadAdmins();
        ObservableList<Admin> reportData = FXCollections.observableArrayList(admins);
        TableColumn<Admin, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<Admin, String> idCol = new TableColumn<>("Admin ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("adminId"));
        reportsTable.getColumns().setAll(usernameCol, idCol);
        reportsTable.setItems(reportData);
        exportButton.setDisable(reportData.isEmpty());
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}