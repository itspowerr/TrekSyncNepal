package com.trekking.app.controller;

import com.trekking.app.model.FestivalOffer;
import com.trekking.app.model.data.FestivalOfferManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.Optional;

public class AdminManageFestivalsController {

    @FXML private TableView<FestivalOffer> offersTable;
    @FXML private TableColumn<FestivalOffer, String> festivalNameCol;
    @FXML private TableColumn<FestivalOffer, LocalDate> startDateCol;
    @FXML private TableColumn<FestivalOffer, LocalDate> endDateCol;
    @FXML private TableColumn<FestivalOffer, Double> discountCol;
    @FXML private TextField festivalNameField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField discountField;
    @FXML private Button addOfferButton;
    @FXML private Button deleteOfferButton;

    private FestivalOfferManager offerManager;
    private ObservableList<FestivalOffer> offerList;

    @FXML
    public void initialize() {
        offerManager = new FestivalOfferManager();
        setupTableColumns();
        refreshData();

        addOfferButton.setOnAction(event -> handleAddOffer());
        deleteOfferButton.setOnAction(event -> handleDeleteOffer());
    }

    public void refreshData() {
        offerList = FXCollections.observableArrayList(offerManager.loadOffers());
        offersTable.setItems(offerList);
    }

    private void setupTableColumns() {
        festivalNameCol.setCellValueFactory(new PropertyValueFactory<>("festivalName"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        discountCol.setCellValueFactory(new PropertyValueFactory<>("discountRate"));

        discountCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.0f%%", item * 100));
                }
            }
        });
    }

    private void handleAddOffer() {
        String name = festivalNameField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String discountText = discountField.getText();

        if (name.isEmpty() || startDate == null || endDate == null || discountText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "All fields are required.");
            return;
        }

        double discountRate;
        try {
            discountRate = Double.parseDouble(discountText) / 100.0;
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Discount must be a valid number (e.g., 15 for 15%).");
            return;
        }

        FestivalOffer newOffer = new FestivalOffer(name, discountRate, startDate, endDate);
        offerManager.addOffer(newOffer);
        offerList.add(newOffer);
        clearForm();
    }

    private void handleDeleteOffer() {
        FestivalOffer selectedOffer = offersTable.getSelectionModel().getSelectedItem();
        if (selectedOffer == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an offer to delete.");
            return;
        }

        Optional<ButtonType> result = showConfirmation("Are you sure you want to delete the '" + selectedOffer.getFestivalName() + "' offer?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            offerManager.deleteOffer(selectedOffer);
            offerList.remove(selectedOffer);
        }
    }

    private void clearForm() {
        festivalNameField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        discountField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
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