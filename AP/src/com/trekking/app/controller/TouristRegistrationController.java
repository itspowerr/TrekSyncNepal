package com.trekking.app.controller;

import com.trekking.app.TrekkingApp;
import com.trekking.app.model.Tourist;
import com.trekking.app.model.data.TouristManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class TouristRegistrationController {

    @FXML private TableView<Tourist> touristsTable;
    @FXML private TableColumn<Tourist, String> firstNameCol;
    @FXML private TableColumn<Tourist, String> lastNameCol;
    @FXML private TableColumn<Tourist, String> nationalityCol;
    @FXML private TableColumn<Tourist, String> passportCol;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private TouristManager touristManager;
    private ObservableList<Tourist> touristList;

    @FXML
    private void initialize() {
        touristManager = new TouristManager();
        setupTable();
        refreshData();

        addButton.setOnAction(event -> handleAdd());
        editButton.setOnAction(event -> handleEdit());
        deleteButton.setOnAction(event -> handleDelete());
    }

    private void setupTable() {
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        nationalityCol.setCellValueFactory(new PropertyValueFactory<>("nationality"));
        passportCol.setCellValueFactory(new PropertyValueFactory<>("passportNumber"));
    }

    public void refreshData() {
        touristList = FXCollections.observableArrayList(touristManager.loadTourists());
        touristsTable.setItems(touristList);
    }

    private void handleAdd() {
        Tourist newTourist = new Tourist("", "", "", "", "", "", "", "", null, "", "", "");
        boolean saveClicked = showTouristEditDialog(newTourist);
        if (saveClicked) {
            touristManager.addTourist(newTourist);
            refreshData();
        }
    }

    private void handleEdit() {
        Tourist selectedTourist = touristsTable.getSelectionModel().getSelectedItem();
        if (selectedTourist != null) {
            boolean saveClicked = showTouristEditDialog(selectedTourist);
            if (saveClicked) {
                touristManager.updateTourist(selectedTourist);
                touristsTable.refresh();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a tourist from the table to edit.");
        }
    }

    private void handleDelete() {
        Tourist selectedTourist = touristsTable.getSelectionModel().getSelectedItem();
        if (selectedTourist == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a tourist from the table to delete.");
            return;
        }

        Optional<ButtonType> result = showConfirmation("Are you sure you want to delete " + selectedTourist + "?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            touristManager.deleteTourist(selectedTourist);
            touristList.remove(selectedTourist);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Tourist has been deleted.");
        }
    }

    private boolean showTouristEditDialog(Tourist tourist) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/tourist-edit-dialog.fxml"));

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Tourist Details");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(TrekkingApp.getPrimaryStage());
            dialogStage.setScene(new Scene(loader.load()));

            TouristEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTourist(tourist);

            dialogStage.showAndWait();

            return controller.isSaved();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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