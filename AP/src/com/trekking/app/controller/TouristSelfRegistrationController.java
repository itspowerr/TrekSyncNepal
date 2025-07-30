package com.trekking.app.controller;

import com.trekking.app.model.Tourist;
import com.trekking.app.model.data.TouristManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class TouristSelfRegistrationController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField passportField;
    @FXML private Button registerButton;
    @FXML private Button cancelButton;

    private TouristManager touristManager;

    @FXML
    public void initialize() {
        touristManager = new TouristManager();
        registerButton.setOnAction(event -> handleRegister());
        cancelButton.setOnAction(event -> closeWindow());
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String firstName = firstNameField.getText();
        String passport = passportField.getText();

        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || passport.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Username, Password, First Name, and Passport Number are required.");
            return;
        }

        List<Tourist> allTourists = touristManager.loadTourists();
        boolean usernameExists = allTourists.stream().anyMatch(t -> t.getUsername() != null && t.getUsername().equals(username));
        if (usernameExists) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "This username is already taken. Please choose another.");
            return;
        }

        Tourist newTourist = new Tourist(
                username,
                password,
                firstName,
                lastNameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                "",
                passport,
                null,
                "",
                "", ""
        );

        touristManager.addTourist(newTourist);
        showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Your account has been created. You can now log in.");

        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}