package com.trekking.app.controller;

import com.trekking.app.model.Tourist;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class TouristEditDialogController {

    @FXML private Label formTitleLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField passportField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> nationalityComboBox;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Stage dialogStage;
    private Tourist tourist;
    private boolean isSaved = false;

    @FXML
    private void initialize() {
        nationalityComboBox.getItems().addAll("Nepal", "India", "China", "USA", "UK", "Other");
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        saveButton.setOnAction(event -> handleSave());
        cancelButton.setOnAction(event -> handleCancel());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTourist(Tourist tourist) {
        this.tourist = tourist;
        if (tourist.getPassportNumber() != null && !tourist.getPassportNumber().isEmpty()) {
            formTitleLabel.setText("Edit Tourist");
            usernameField.setText(tourist.getUsername());
            passwordField.setText(tourist.getPassword());
            firstNameField.setText(tourist.getFirstName());
            lastNameField.setText(tourist.getLastName());
            emailField.setText(tourist.getEmail());
            phoneField.setText(tourist.getPhoneNumber());
            nationalityComboBox.setValue(tourist.getNationality());
            passportField.setText(tourist.getPassportNumber());
            dobPicker.setValue(tourist.getDateOfBirth());
            genderComboBox.setValue(tourist.getGender());
            passportField.setDisable(true);
            if (tourist.getUsername() != null && !tourist.getUsername().isEmpty()) {
                usernameField.setDisable(true);
            } else {
                usernameField.setDisable(false);
            }
        } else {
            formTitleLabel.setText("Add New Tourist");
        }
    }

    public boolean isSaved() {
        return isSaved;
    }

    @FXML
    private void handleSave() {
        if (firstNameField.getText().isEmpty() || passportField.getText().isEmpty()) {
            showAlert("Form Error", "First Name and Passport Number are required.");
            return;
        }

        tourist.setUsername(usernameField.getText());
        tourist.setPassword(passwordField.getText());
        tourist.setFirstName(firstNameField.getText());
        tourist.setLastName(lastNameField.getText());
        tourist.setEmail(emailField.getText());
        tourist.setPhoneNumber(phoneField.getText());
        tourist.setNationality(nationalityComboBox.getValue());
        tourist.setPassportNumber(passportField.getText());
        tourist.setDateOfBirth(dobPicker.getValue());
        tourist.setGender(genderComboBox.getValue());

        isSaved = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}