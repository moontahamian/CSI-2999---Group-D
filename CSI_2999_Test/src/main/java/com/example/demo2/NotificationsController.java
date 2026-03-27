package com.example.demo2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class NotificationsController {

    @FXML private VBox notificationsContainer;

    @FXML
    public void initialize() {

        Integer userId = Session.getCurrentUserId();
        if (userId == null) return;

        List<DButils.Notification> notifications =
                DButils.getNotifications(userId);

        if (notifications.isEmpty()) {
            Label empty = new Label("No notifications yet.");
            empty.setStyle("-fx-text-fill: #888; -fx-font-size: 14;");
            notificationsContainer.getChildren().add(empty);
        } else {
            for (DButils.Notification n : notifications) {
                notificationsContainer.getChildren().add(createCard(n));
            }
        }

        // Mark all as read when page is opened
        DButils.markAllNotificationsRead(userId);
    }
    @FXML
    private void handleClearAll() {
        Integer userId = Session.getCurrentUserId();
        if (userId == null) return;
        DButils.clearAllNotifications(userId);
        notificationsContainer.getChildren().clear();
        Label empty = new Label("No notifications yet.");
        empty.setStyle("-fx-text-fill: #888; -fx-font-size: 14;");
        notificationsContainer.getChildren().add(empty);
    }

    private HBox createCard(DButils.Notification n) {

        HBox card = new HBox();
        card.setSpacing(10);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 15;"
        );

        VBox textBox = new VBox(4);

        Label message = new Label(n.getMessage());
        message.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: " + (n.isRead() ? "normal" : "bold") + ";" +
                        "-fx-text-fill: #1a1a2e;"
        );
        message.setWrapText(true);

        Label date = new Label(n.getCreatedAt());
        date.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");

        // Unread indicator
        if (!n.isRead()) {
            Label unreadDot = new Label("● NEW");
            unreadDot.setStyle(
                    "-fx-font-size: 10px;" +
                            "-fx-text-fill: #6366F1;" +
                            "-fx-font-weight: bold;"
            );
            textBox.getChildren().addAll(message, unreadDot, date);
        } else {
            textBox.getChildren().addAll(message, date);
        }

        HBox.setHgrow(textBox, javafx.scene.layout.Priority.ALWAYS);
        card.getChildren().add(textBox);

        return card;
    }
}