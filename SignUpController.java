package com.example.demo2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpController {

    @FXML
    private TextField name_text;

    @FXML
    private PasswordField password_text;

    @FXML
    private PasswordField confirm_password_text;

    @FXML
    private Label message_label;

    @FXML
    public void create_button_clicked(ActionEvent event) {

        String username = name_text.getText().trim();
        String password = password_text.getText().trim();
        String confirmPassword = confirm_password_text.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            message_label.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            message_label.setText("Passwords do not match.");
            return;
        }

        boolean success = DButils.registerUser(username, password);

        if (success) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("Log_in.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            message_label.setText("Username already exists.");
        }
    }

}
