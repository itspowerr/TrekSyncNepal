package com.trekking.app.controller;

import com.trekking.app.model.EmergencyIncident;
import com.trekking.app.model.data.EmergencyManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;

public class EmergencyLogController {

    @FXML private TableView<EmergencyIncident> emergencyLogTable;
    @FXML private TableColumn<EmergencyIncident, LocalDateTime> timestampCol;
    @FXML private TableColumn<EmergencyIncident, String> bookingIdCol;
    @FXML private TableColumn<EmergencyIncident, String> touristNameCol;
    @FXML private TableColumn<EmergencyIncident, String> descriptionCol;

    private EmergencyManager emergencyManager;

    @FXML
    public void initialize() {
        emergencyManager = new EmergencyManager();
        setupTableColumns();
        refreshData();
    }

    private void setupTableColumns() {
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        bookingIdCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        touristNameCol.setCellValueFactory(new PropertyValueFactory<>("touristName"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    public void refreshData() {
        emergencyLogTable.setItems(FXCollections.observableArrayList(emergencyManager.loadIncidents()));
    }
}