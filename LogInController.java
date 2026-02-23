package com.example.demo2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LogInController {
    @FXML

    private Label notMemberLabel;

    @FXML
    private Button login_button;

    @FXML
    private ImageView logo_image;

    @FXML
    private Label logo_description_label;

    @FXML
    private Button Signup_button;
    @FXML
    private TextField usename_tf;
    @FXML
    private PasswordField password_tf;

    @FXML
    public void loginClicked(ActionEvent actionEvent) {

        String username = usename_tf.getText().trim();
        String password = password_tf.getText().trim();

        System.out.println("Username entered: " + username);
        System.out.println("Password entered: " + password);

        if (DButils.validateLogin(username, password)) {

            // Store logged in user
            Session.setCurrentUser(username);

            try {
                Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            notMemberLabel.setText("Invalid username or password.");
        }

    }
    @FXML
    public void signUpClicked(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("sign_up.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
