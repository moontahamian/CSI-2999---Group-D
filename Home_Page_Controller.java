package com.example.demo2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Home_Page_Controller {

    @FXML
    private Label welcome_label;

    @FXML
    public void initialize() {

        String username = Session.getCurrentUser();

        if (username != null) {
            welcome_label.setText("WELCOME, " + username.toUpperCase());
        }
    }
}
