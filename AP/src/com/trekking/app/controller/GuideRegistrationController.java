package com.trekking.app.controller;

import com.trekking.app.model.Attraction;
import com.trekking.app.model.Guide;
import com.trekking.app.model.data.AttractionManager;
import com.trekking.app.model.data.GuideManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuideRegistrationController {

    @FXML private TableView<Guide> guidesTable;
    @FXML private TableColumn<Guide, String> guideNameCol, contactCol;
    @FXML private TableColumn<Guide, Integer> experienceCol;
    @FXML private Label formTitleLabel, assignedAttractionsLabel;
    @FXML private TextField guideNameField, contactField, experienceField;
    @FXML private CheckBox nepaliCheck, englishCheck, hindiCheck, germanCheck, frenchCheck, japaneseCheck;
    @FXML private Button manageAssignmentsButton;
    @FXML private Button deleteButton;
    @FXML private Button newGuideButton;
    @FXML private Button saveButton;

    private GuideManager guideManager;
    private AttractionManager attractionManager;
    private ObservableList<Guide> guideList;
    private Guide currentlySelectedGuide = null;
    private List<String> tempAssignedAttractionIds = new ArrayList<>();

    @FXML
    public void initialize() {
        guideManager = new GuideManager();
        attractionManager = new AttractionManager();

        setupTable();
        refreshData();
        setupForm();
        clearFormAndSelection();

        guidesTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        populateForm(newSelection);
                    }
                }
        );
    }

    private void setupTable() {
        guideNameCol.setCellValueFactory(new PropertyValueFactory<>("guideName"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        experienceCol.setCellValueFactory(new PropertyValueFactory<>("experienceYears"));
    }

    public void refreshData() {
        guideList = FXCollections.observableArrayList(guideManager.loadGuides());
        guidesTable.setItems(guideList);
    }

    private void setupForm() {
        saveButton.setOnAction(event -> handleSaveAction());
        deleteButton.setOnAction(event -> handleDeleteAction());
        newGuideButton.setOnAction(event -> clearFormAndSelection());
        manageAssignmentsButton.setOnAction(event -> handleManageAssignments());
    }

    private void populateForm(Guide guide) {
        currentlySelectedGuide = guide;
        tempAssignedAttractionIds = new ArrayList<>(guide.getAssignedAttractionIds());

        guideNameField.setText(guide.getGuideName());
        contactField.setText(guide.getContact());
        experienceField.setText(String.valueOf(guide.getExperienceYears()));

        List<String> languages = guide.getLanguages();
        nepaliCheck.setSelected(languages.contains("Nepali"));
        englishCheck.setSelected(languages.contains("English"));
        hindiCheck.setSelected(languages.contains("Hindi"));
        germanCheck.setSelected(languages.contains("German"));
        frenchCheck.setSelected(languages.contains("French"));
        japaneseCheck.setSelected(languages.contains("Japanese"));

        updateAssignedAttractionsLabel();

        manageAssignmentsButton.setDisable(false);
        formTitleLabel.setText("Edit Guide Details");
        saveButton.setText("Update Guide");
    }

    private void handleManageAssignments() {
        if (currentlySelectedGuide == null) {
            showAlert(Alert.AlertType.WARNING, "No Guide Selected", "Please select a guide from the table first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/guide-assignment-dialog.fxml"));
            Parent root = loader.load();

            GuideAssignmentDialogController controller = loader.getController();
            controller.initData(currentlySelectedGuide, attractionManager.loadAttractions());

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Manage Assignments");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            dialogStage.showAndWait();

            if (controller.isSaved()) {
                tempAssignedAttractionIds = controller.getSelectedAttractionIds();
                updateAssignedAttractionsLabel();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateAssignedAttractionsLabel() {
        List<Attraction> allAttractions = attractionManager.loadAttractions();
        String assignedNames = allAttractions.stream()
                .filter(attr -> tempAssignedAttractionIds.contains(attr.getAttractionId()))
                .map(Attraction::getName)
                .collect(Collectors.joining(", "));

        if (assignedNames.isEmpty()) {
            assignedAttractionsLabel.setText("None. Create guide then edit to assign.");
        } else {
            assignedAttractionsLabel.setText(assignedNames);
        }
    }

    private void handleSaveAction() {
        if (guideNameField.getText().isEmpty() || experienceField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Guide Name and Experience are required.");
            return;
        }

        List<String> languages = new ArrayList<>();
        if (nepaliCheck.isSelected()) languages.add("Nepali");
        if (englishCheck.isSelected()) languages.add("English");
        if (hindiCheck.isSelected()) languages.add("Hindi");
        if (germanCheck.isSelected()) languages.add("German");
        if (frenchCheck.isSelected()) languages.add("French");
        if (japaneseCheck.isSelected()) languages.add("Japanese");

        int experience;
        try {
            experience = Integer.parseInt(experienceField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Experience must be a valid number.");
            return;
        }

        if (currentlySelectedGuide != null) {
            currentlySelectedGuide.setGuideName(guideNameField.getText());
            currentlySelectedGuide.setContact(contactField.getText());
            currentlySelectedGuide.setExperienceYears(experience);
            currentlySelectedGuide.setLanguages(languages);
            currentlySelectedGuide.setAssignedAttractionIds(tempAssignedAttractionIds);

            guideManager.updateGuide(currentlySelectedGuide);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Guide details have been updated.");

        } else {
            Guide newGuide = new Guide(guideNameField.getText(), languages, contactField.getText(), experience);
            guideManager.addGuide(newGuide);
            refreshData();
            showAlert(Alert.AlertType.INFORMATION, "Success", "New guide has been registered.");
            clearFormAndSelection();
        }

        guidesTable.refresh();
    }

    private void handleDeleteAction() {
        if (currentlySelectedGuide == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a guide from the table to delete.");
            return;
        }
        Optional<ButtonType> result = showConfirmation("Are you sure you want to delete " + currentlySelectedGuide + "?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guideManager.deleteGuide(currentlySelectedGuide);
            guideList.remove(currentlySelectedGuide);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Guide has been deleted.");
            clearFormAndSelection();
        }
    }

    private void clearFormAndSelection() {
        currentlySelectedGuide = null;
        guidesTable.getSelectionModel().clearSelection();

        guideNameField.clear();
        contactField.clear();
        experienceField.clear();

        nepaliCheck.setSelected(false);
        englishCheck.setSelected(false);
        hindiCheck.setSelected(false);
        germanCheck.setSelected(false);
        frenchCheck.setSelected(false);
        japaneseCheck.setSelected(false);

        tempAssignedAttractionIds.clear();
        updateAssignedAttractionsLabel();

        manageAssignmentsButton.setDisable(true);
        formTitleLabel.setText("Add New Guide");
        saveButton.setText("Register Guide");
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