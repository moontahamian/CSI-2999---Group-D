package com.example.demo2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        DButils.createUsersTable();          // ensure table exists


        FXMLLoader fxmlLoader = new FXMLLoader(
                Launcher.class.getResource("Log_in.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("NextStep");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(Launcher.class, args);
    }
}
