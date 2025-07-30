package com.trekking.app.controller;

import com.trekking.app.model.Attraction;
import com.trekking.app.model.Guide;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GuideAssignmentDialogController {

    @FXML private Label titleLabel;
    @FXML private ListView<Attraction> attractionsListView;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private boolean saved = false;
    private final ObservableList<Attraction> allAttractions = FXCollections.observableArrayList();
    private final Set<String> selectedAttractionIds = new HashSet<>();

    @FXML
    public void initialize() {
        // This sets up the custom cell factory with the corrected logic
        attractionsListView.setCellFactory(lv -> new AttractionListCell());

        saveButton.setOnAction(event -> {
            saved = true;
            closeWindow();
        });
        cancelButton.setOnAction(event -> closeWindow());
    }

    public void initData(Guide guide, List<Attraction> attractions) {
        titleLabel.setText("Assign Attractions for: " + guide.getGuideName());
        allAttractions.setAll(attractions);
        attractionsListView.setItems(allAttractions);

        if (guide.getAssignedAttractionIds() != null) {
            selectedAttractionIds.addAll(guide.getAssignedAttractionIds());
        }
        // The .refresh() call is no longer needed with the new cell logic.
    }

    public List<String> getSelectedAttractionIds() {
        return new ArrayList<>(selectedAttractionIds);
    }

    public boolean isSaved() {
        return saved;
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    // --- NEW, ROBUST ListCell IMPLEMENTATION ---
    private class AttractionListCell extends ListCell<Attraction> {
        private final CheckBox checkBox = new CheckBox();
        private Attraction currentAttraction;

        // The listener is created only once per cell and stored in a field.
        private final ChangeListener<Boolean> listener = (obs, wasSelected, isSelected) -> {
            if (currentAttraction != null) {
                if (isSelected) {
                    selectedAttractionIds.add(currentAttraction.getAttractionId());
                } else {
                    selectedAttractionIds.remove(currentAttraction.getAttractionId());
                }
            }
        };

        @Override
        protected void updateItem(Attraction item, boolean empty) {
            super.updateItem(item, empty);
            this.currentAttraction = item;

            if (empty || item == null) {
                setGraphic(null);
            } else {
                checkBox.setText(item.getName());

                // Temporarily remove the listener while we set the state
                checkBox.selectedProperty().removeListener(listener);
                // Set the checkbox state based on the master set of IDs
                checkBox.setSelected(selectedAttractionIds.contains(item.getAttractionId()));
                // Add the listener back
                checkBox.selectedProperty().addListener(listener);

                setGraphic(checkBox);
            }
        }
    }
}