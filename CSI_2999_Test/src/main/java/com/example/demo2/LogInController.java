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

public class LogInController {

    @FXML private Label notMemberLabel;
    @FXML private TextField usename_tf;
    @FXML private PasswordField password_tf;

    // LOGIN
    @FXML
        public void loginClicked(ActionEvent event) {

            String username = usename_tf.getText().trim();
            String password = password_tf.getText().trim();

            System.out.println("Trying login for: " + username);

            Integer userId = DButils.validateLogin(username, password);

            System.out.println("UserId returned: " + userId);

            if (userId == null) {
                notMemberLabel.setText("Invalid username or password.");
                return;
            }


            Session.setCurrentUserId(userId);
            Session.setCurrentUsername(username);
            NotificationChecker.start();

            System.out.println("Session after set: " + Session.getCurrentUserId());

            try {

                FXMLLoader loader =
                        new FXMLLoader(getClass().getResource("MainLayout.fxml"));

                Parent root = loader.load();

                MainLayoutController mainController =
                        loader.getController();

                mainController.setCenter("Dashboard.fxml");
                

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

    // GO TO SIGN UP
    @FXML
    public void signUpClicked(ActionEvent event) {

        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("sign_up.fxml")
            );

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