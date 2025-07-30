package com.trekking.app.controller;

import com.trekking.app.model.Admin;
import com.trekking.app.model.data.AdminManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;

public class AdminManageAdminsController {

    @FXML private TableView<Admin> adminsTable;
    @FXML private TableColumn<Admin, String> adminUsernameCol;
    @FXML private TableColumn<Admin, String> adminIdCol;
    @FXML private TextField newAdminUsernameField;
    @FXML private PasswordField newAdminPasswordField;
    @FXML private Button addAdminButton;
    @FXML private Button deleteAdminButton;

    private AdminManager adminManager;
    private ObservableList<Admin> adminList;

    @FXML
    public void initialize() {
        adminManager = new AdminManager();
        setupTableColumns();
        refreshData();

        addAdminButton.setOnAction(event -> handleAddAdmin());
        deleteAdminButton.setOnAction(event -> handleDeleteAdmin());
    }

    private void setupTableColumns() {
        adminUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        adminIdCol.setCellValueFactory(new PropertyValueFactory<>("adminId"));
    }

    public void refreshData() {
        adminList = FXCollections.observableArrayList(adminManager.loadAdmins());
        adminsTable.setItems(adminList);
    }

    private void handleAddAdmin() {
        String username = newAdminUsernameField.getText();
        String password = newAdminPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Username and password cannot be empty.");
            return;
        }

        Admin newAdmin = new Admin(username, password);
        adminManager.addAdmin(newAdmin);
        refreshData(); // Refresh the table to show the new admin

        newAdminUsernameField.clear();
        newAdminPasswordField.clear();
        showAlert(Alert.AlertType.INFORMATION, "Success", "New admin '" + username + "' has been added.");
    }

    private void handleDeleteAdmin() {
        Admin selectedAdmin = adminsTable.getSelectionModel().getSelectedItem();
        if (selectedAdmin == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an admin from the table to delete.");
            return;
        }

        Optional<ButtonType> result = showConfirmation("Are you sure you want to delete the admin '" + selectedAdmin.getUsername() + "'?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            adminManager.deleteAdmin(selectedAdmin);
            adminList.remove(selectedAdmin);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Admin has been deleted.");
        }
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