package com.example.demo2;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SidebarController {

    @FXML
    void goToDashboard(ActionEvent event) {
        System.out.println("Navigating to Dashboard...");
        // You can repeat the logic below for Dashboard.fxml if you have one
    }

    @FXML
    void goToApplications(ActionEvent event) {
        try {
            Parent calendarRoot = FXMLLoader.load(getClass().getResource("Calendar.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(calendarRoot));
            stage.show();

            System.out.println("switched to Calendar");
        } catch (IOException e) {
            System.err.println("Error: Could not find Calendar.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    void logout(ActionEvent event) {

    }
}