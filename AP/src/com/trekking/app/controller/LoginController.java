package com.trekking.app.controller;

import com.trekking.app.TrekkingApp;
import com.trekking.app.model.data.AdminService;
import com.trekking.app.model.data.TouristService;
import com.trekking.app.util.UTF8Control;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ImageView logoImageView;

    private AdminService adminService;
    private TouristService touristService;

    @FXML
    public void initialize() {
        try {
            // CORRECTED PATH: Removed "/resources"
            Image logo = new Image(getClass().getResourceAsStream("/images/trek-sync-logo.png"));
            logoImageView.setImage(logo);
        } catch (Exception e) {
            System.err.println("Error loading logo image.");
            e.printStackTrace();
        }

        adminService = new AdminService();
        touristService = new TouristService();
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (adminService.login(username, password)) {
            TrekkingApp.showAdminDashboard(TrekkingApp.isDarkModeActive());
        } else if (touristService.login(username, password)) {
            TrekkingApp.showTouristDashboard(TrekkingApp.isDarkModeActive());
        } else {
            showAlert("Login Failed", "Invalid Credentials", "Please check your username and password and try again.");
        }
    }

    @FXML
    private void handleRegisterLinkAction(ActionEvent event) {
        try {
            // CORRECTED PATH: Removed "resources."
            ResourceBundle bundle = ResourceBundle.getBundle("lang.lang", Locale.getDefault(), new UTF8Control());
            // CORRECTED PATH: Removed "/resources"
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/tourist-self-registration.fxml"), bundle);
            Parent root = loader.load();

            Stage registrationStage = new Stage();
            registrationStage.setTitle("Tourist Account Registration");
            registrationStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);

            if (TrekkingApp.isDarkModeActive()) {
                // CORRECTED PATH: Removed "/resources"
                scene.getStylesheets().add(TrekkingApp.class.getResource("/dark-styles.css").toExternalForm());
            } else {
                // CORRECTED PATH: Removed "/resources"
                scene.getStylesheets().add(TrekkingApp.class.getResource("/styles.css").toExternalForm());
            }

            registrationStage.setScene(scene);
            registrationStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}