package com.example.demo2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.time.LocalDate;

public class EditApplicationController {

    @FXML private TextField companyField;
    @FXML private TextField titleField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private DatePicker deadlinePicker;
    @FXML private TextField locationField;
    @FXML private TextArea descriptionArea;
    @FXML private TextArea notesArea;

    private ApplicationModel currentApplication;

    @FXML
    public void initialize() {

        statusCombo.getItems().addAll(
                "Applied",
                "Interview Scheduled",
                "Interviewed",
                "Offer Received",
                "Accepted",
                "Rejected",
                "Withdrawn"
        );
    }

    // Receive Application From List Page
    public void setApplication(ApplicationModel application) {

        this.currentApplication = application;

        companyField.setText(application.getCompanyName());
        titleField.setText(application.getJobTitle());
        locationField.setText(application.getLocation());
        descriptionArea.setText(application.getDescription());
        notesArea.setText(application.getNotes());

        if (application.getDateApplied() != null) {
            datePicker.setValue(LocalDate.parse(application.getDateApplied()));
        }

        if (application.getDeadline() != null) {
            deadlinePicker.setValue(LocalDate.parse(application.getDeadline()));
        }

        statusCombo.setValue(application.getStatus());
    }

    // Save Changes
    @FXML
    private void handleSaveChanges(ActionEvent event) {

        if (currentApplication == null) return;

        String company = companyField.getText().trim();
        String title = titleField.getText().trim();
        String location = locationField.getText().trim();
        String description = descriptionArea.getText().trim();
        String notes = notesArea.getText().trim();
        String status = statusCombo.getValue();

        LocalDate appliedDate = datePicker.getValue();
        LocalDate deadline = deadlinePicker.getValue();

        if (company.isEmpty() || title.isEmpty()) {
            showAlert("Company and Job Title are required.");
            return;
        }

        DButils.updateApplication(
                currentApplication.getId(),
                company,
                title,
                appliedDate != null ? appliedDate.toString() : null,
                deadline != null ? deadline.toString() : null,
                status,
                location,
                description,
                notes
        );
        NotificationChecker.checkNow();
        goToPage("ApplicationsList.fxml");
    }

    // Cancel
    @FXML
    private void handleCancel(ActionEvent event) {
        goToPage("ApplicationsList.fxml");
    }

    // Navigation (MainLayout)
    private void goToPage(String fxmlFile) {

        BorderPane root =
                (BorderPane) companyField.getScene().getRoot();

        MainLayoutController mainController =
                (MainLayoutController) root.getUserData();

        mainController.setCenter(fxmlFile);
    }

    // Utility
    private void showAlert(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Application");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}