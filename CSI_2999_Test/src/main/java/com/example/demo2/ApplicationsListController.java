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
        container.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 15;"
        );

        // LEFT SIDE - Company Name
        Label nameLabel = new Label(app.getCompanyName());
        nameLabel.setStyle(
                "-fx-font-size: 16;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        nameLabel.setOnMouseClicked(e -> openApplication(app));

        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        // EDIT BUTTON
        Button editBtn = new Button("Edit");
        editBtn.setStyle(
                "-fx-background-color: #6366F1;" +
                        "-fx-text-fill: white;"+
                        "-fx-cursor: hand;"
        );

        editBtn.setOnAction(e -> openEditPage(app));

        // DELETE BUTTON
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle(
                "-fx-background-color: #EF4444;" +
                        "-fx-text-fill: white;"+
                        "-fx-cursor: hand;"
        );

        deleteBtn.setOnAction(e -> {
            DButils.deleteApplication(app.getId());
            loadApplications();
        });

        container.getChildren().addAll(nameLabel, editBtn, deleteBtn);

        return container;
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