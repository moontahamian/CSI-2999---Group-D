package com.example.demo2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ApplicationsController {

    @FXML private TextField companyField;
    @FXML private TextField titleField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private DatePicker deadlinePicker;
    @FXML private TextField locationField;
    @FXML private TextArea descriptionArea;
    @FXML private TextArea notesArea;
    @FXML private Button addApplicationButton;

    private ApplicationModel currentApplication;
    private boolean viewMode = false;

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

        statusCombo.setValue("Applied");
    }

    // Cancel
    @FXML
    private void handleCancel(ActionEvent event) {
        goToPage("ApplicationsList.fxml");
    }
    // VIEW MODE
    public void setApplication(ApplicationModel application) {

        this.currentApplication = application;
        this.viewMode = true;

        companyField.setText(application.getCompanyName());
        titleField.setText(application.getJobTitle());
        locationField.setText(application.getLocation());
        descriptionArea.setText(application.getDescription());
        notesArea.setText(application.getNotes());

        if (application.getDateApplied() != null) {
            try {
                datePicker.setValue(LocalDate.parse(application.getDateApplied()));
            } catch (Exception e) {
                datePicker.setValue(null);
            }
        }

        if (application.getDeadline() != null) {
            try {
                deadlinePicker.setValue(LocalDate.parse(application.getDeadline()));
            } catch (Exception e) {
                deadlinePicker.setValue(null);
            }
        }

        statusCombo.setValue(application.getStatus());

        // Disable editing
        companyField.setEditable(false);
        titleField.setEditable(false);
        locationField.setEditable(false);
        descriptionArea.setEditable(false);
        notesArea.setEditable(false);
        datePicker.setDisable(true);
        deadlinePicker.setDisable(true);
        statusCombo.setDisable(true);

        addApplicationButton.setVisible(false);
    }

    // ADD MODE
    @FXML
    private void handleAddApplication() {

        Integer userId = Session.getCurrentUserId();
        if (userId == null) return;

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

        DButils.insertApplication(
                userId,
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



    // Utility

    private void clearFields() {
        companyField.clear();
        titleField.clear();
        locationField.clear();
        descriptionArea.clear();
        notesArea.clear();
        datePicker.setValue(null);
        deadlinePicker.setValue(null);
        statusCombo.setValue("Applied");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Application");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void goBackToList() {

        BorderPane root =
                (BorderPane) companyField.getScene().getRoot();

        MainLayoutController mainController =
                (MainLayoutController) root.getUserData();

        mainController.setCenter("ApplicationsList.fxml");
    }
    private void goToPage(String fxmlFile) {

        BorderPane root =
                (BorderPane) companyField.getScene().getRoot();

        MainLayoutController mainController =
                (MainLayoutController) root.getUserData();

        mainController.setCenter(fxmlFile);
    }
}