package com.example.demo2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SidebarController {

    @FXML private Button homeBtn;
    @FXML private Button applicationsBtn;
    @FXML private Button calendarBtn;
    @FXML private Button logoutBtn;

    // Navigation Helper
    private void changeCenter(ActionEvent event,
                              String fxmlFile,
                              String activePage) {

        BorderPane root =
                (BorderPane) ((Node) event.getSource())
                        .getScene()
                        .getRoot();

        MainLayoutController mainController =
                (MainLayoutController) root.getUserData();

        mainController.setCenter(fxmlFile);

        setActive(activePage);
    }

    // Active Button Highlight
    private void setActive(String page) {

        // Reset all to white
        homeBtn.setTextFill(Color.WHITE);
        applicationsBtn.setTextFill(Color.WHITE);
        calendarBtn.setTextFill(Color.WHITE);

        // Highlight selected
        switch (page) {
            case "dashboard" -> homeBtn.setTextFill(Color.web("#ffb200"));
            case "applications" -> applicationsBtn.setTextFill(Color.web("#ffb200"));
            case "calendar" -> calendarBtn.setTextFill(Color.web("#ffb200"));
        }
    }

    // Buttons
    public void goToDashboard(ActionEvent event) {
        changeCenter(event, "Dashboard.fxml", "dashboard");
    }

    public void goToApplications(ActionEvent event) {
        changeCenter(event, "ApplicationsList.fxml", "applications");
    }

    public void goToCalendar(ActionEvent event) {
        changeCenter(event, "Calendar.fxml", "calendar");
    }
    @FXML
    public void logout(ActionEvent event) {

        Session.clearSession();

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("Log_in.fxml"));

            Parent root = loader.load();

            Stage stage =
                    (Stage) ((Node) event.getSource())
                            .getScene()
                            .getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}