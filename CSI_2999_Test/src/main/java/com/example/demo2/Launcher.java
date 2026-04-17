package com.example.demo2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Create all tables
        DButils.createUsersTable();
        DButils.createApplicationsTable();
        DButils.createCalendarTable();
        DButils.createNotificationsTable();
        DButils.createResumesTable();


        FXMLLoader fxmlLoader =
                new FXMLLoader(Launcher.class.getResource("Log_in.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(
                Launcher.class.getResource("style.css").toExternalForm()
        );
        stage.setTitle("NextStep");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(Launcher.class, args);
    }
}