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
import org.kordamp.ikonli.javafx.FontIcon;

public class SidebarController {

    @FXML private Button homeBtn;
    @FXML private Button applicationsBtn;
    @FXML private Button calendarBtn;
    @FXML private Button logoutBtn;
    @FXML private Button notificationsBtn;
    @FXML private Button resumeBtn;

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

    // Helper to get FontIcon from a Button's graphic
    private FontIcon getIcon(Button button) {
        return (FontIcon) button.getGraphic();
    }

    private void setActive(String page) {

        // Reset all to white
        getIcon(homeBtn).setIconColor(Color.WHITE);
        getIcon(applicationsBtn).setIconColor(Color.WHITE);
        getIcon(calendarBtn).setIconColor(Color.WHITE);
        getIcon(notificationsBtn).setIconColor(Color.WHITE);
        getIcon(resumeBtn).setIconColor(Color.WHITE);

        // Highlight selected
        switch (page) {
            case "dashboard"      -> getIcon(homeBtn).setIconColor(Color.web("#ffb200"));
            case "applications"   -> getIcon(applicationsBtn).setIconColor(Color.web("#ffb200"));
            case "calendar"       -> getIcon(calendarBtn).setIconColor(Color.web("#ffb200"));
            case "notifications"  -> getIcon(notificationsBtn).setIconColor(Color.web("#ffb200"));
            case "resume" -> getIcon(resumeBtn).setIconColor(Color.web("#ffb200"));
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

    public void goToNotifications(ActionEvent event) {
        changeCenter(event, "Notifications.fxml", "notifications");
    }


    public void goToResume(ActionEvent event) {
        changeCenter(event, "Resume.fxml", "resume");
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