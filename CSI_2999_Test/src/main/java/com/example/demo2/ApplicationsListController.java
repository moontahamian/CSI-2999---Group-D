package com.example.demo2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class ApplicationsListController {

    @FXML
    private VBox applicationListContainer;

    @FXML
    public void initialize() {
        loadApplications();
    }

    // Load Applications From Database
    private void loadApplications() {

        applicationListContainer.getChildren().clear();


        Integer userId = Session.getCurrentUserId();

        if (userId == null) {
            System.out.println("User not logged in.");
            return;
        }

        List<ApplicationModel> applications =
                DButils.getApplicationsByUser(userId);

        for (ApplicationModel app : applications) {
            HBox card = createApplicationCard(app);
            applicationListContainer.getChildren().add(card);
        }
    }
    // Create UI Card For Each Application
    private HBox createApplicationCard(ApplicationModel app) {

        HBox container = new HBox();
        container.setSpacing(20);
        container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        container.getStyleClass().add("app-card");

        // Add a colored left border based on status
        String statusColor = getStatusColor(app.getStatus());
        container.setStyle(
                "-fx-border-color: " + statusColor + " transparent transparent transparent;" +
                        "-fx-border-width: 0 0 0 5px;"
        );

        // Status dot
        Label statusDot = new Label("⬤");
        statusDot.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-size: 10px;");

        // Company name
        Label nameLabel = new Label(app.getCompanyName());
        nameLabel.getStyleClass().add("app-card-title");
        nameLabel.setOnMouseClicked(e -> openApplication(app));

        // Job title
        Label jobLabel = new Label(app.getJobTitle());
        jobLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");

        // Name + job stacked
        javafx.scene.layout.VBox textBox = new javafx.scene.layout.VBox(3);
        textBox.getChildren().addAll(nameLabel, jobLabel);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        // Status label
        Label statusLabel = new Label(app.getStatus());
        statusLabel.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + statusColor + ";" +
                        "-fx-background-color: " + statusColor + "22;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-padding: 3 8 3 8;"
        );

        // Edit button
        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("btn-edit");
        editBtn.setOnAction(e -> openEditPage(app));

        // Delete button
        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("btn-delete");
        deleteBtn.setOnAction(e -> {
            DButils.deleteApplication(app.getId());
            loadApplications();
        });

        container.getChildren().addAll(statusDot, textBox, statusLabel, editBtn, deleteBtn);
        return container;
    }

    private String getStatusColor(String status) {
        if (status == null) return "#94A3B8";
        return switch (status) {
            case "Applied"             -> "#9244bc";
            case "Interview Scheduled" -> "#e59a20";
            case "Interviewed"         -> "#50a650";
            case "Offer Received"      -> "#e56739";
            case "Accepted"            -> "#10B981";
            case "Rejected"            -> "#EF4444";
            case "Withdrawn"           -> "#47a4bf";
            default                    -> "#94A3B8";
        };
    }
    // Open View Application Page
    private void openApplication(ApplicationModel app) {

        try {
            BorderPane root =
                    (BorderPane) applicationListContainer.getScene().getRoot();

            MainLayoutController mainController =
                    (MainLayoutController) root.getUserData();

            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("Applications.fxml"));

            Parent content = loader.load();

            ApplicationsController controller = loader.getController();
            controller.setApplication(app);

            root.setCenter(content);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Open Edit Page
    private void openEditPage(ApplicationModel app) {

        try {
            BorderPane root =
                    (BorderPane) applicationListContainer.getScene().getRoot();

            MainLayoutController mainController =
                    (MainLayoutController) root.getUserData();

            FXMLLoader loader =
                    mainController.loadCenter("EditApplication.fxml");

            if (loader == null) return;

            EditApplicationController controller =
                    loader.getController();

            controller.setApplication(app);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add New Application Button
    @FXML
    private void handleAddApplication() {

        try {
            BorderPane root =
                    (BorderPane) applicationListContainer.getScene().getRoot();

            MainLayoutController mainController =
                    (MainLayoutController) root.getUserData();

            mainController.setCenter("Applications.fxml");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}