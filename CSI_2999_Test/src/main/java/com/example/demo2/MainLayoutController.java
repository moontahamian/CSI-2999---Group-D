package com.example.demo2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class MainLayoutController {

    @FXML
    private BorderPane rootPane;

    @FXML
    public void initialize() {
        rootPane.setUserData(this);
    }

    // Standard load
    public void setCenter(String fxmlFile) {

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource(fxmlFile));

            Parent content = loader.load();
            rootPane.setCenter(content);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Advanced load (returns controller)
    public FXMLLoader loadCenter(String fxmlFile) {

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource(fxmlFile));

            Parent content = loader.load();
            rootPane.setCenter(content);

            return loader;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}