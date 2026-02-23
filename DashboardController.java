package com.example.demo2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {

        String username = Session.getCurrentUser();

        if (username != null) {
            welcomeLabel.setText("WELCOME, " + username.toUpperCase());
        }
    }
// this is a log_out button controller that we will add later
//    @FXML
//    private void logoutClicked(ActionEvent event) {
//
//        Session.clear();
//
//        try {
//            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
