package com.example.demo2;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalAppsLabel;
    @FXML private Label totalInterviewLabel;
    @FXML private BarChart<String, Number> statusBarChart;
    @FXML private PieChart statusPieChart;
    @FXML private VBox upcomingEventsContainer;

    @FXML
    public void initialize() {

        String username = Session.getCurrentUsername();
        if (username != null && welcomeLabel != null) {
            welcomeLabel.setText("WELCOME, " + username.toUpperCase());
        }

        Integer userId = Session.getCurrentUserId();
        if (userId == null) return;

        if (totalAppsLabel != null) {
            totalAppsLabel.setText(
                    String.valueOf(DButils.getTotalApplications(userId))
            );
        }

        if (totalInterviewLabel != null) {
            totalInterviewLabel.setText(
                    String.valueOf(DButils.getTotalInterviews(userId))
            );
        }

        loadCharts(userId);
        loadUpcomingEvents(userId);
    }

    private void loadUpcomingEvents(int userId) {
        if (upcomingEventsContainer == null) return;

        upcomingEventsContainer.getChildren().clear();

        List<ApplicationModel> apps = DButils.getApplicationsByUser(userId);
        LocalDate today = LocalDate.now();
        LocalDate lookAhead = today.plusDays(14); // show events in next 14 days

        // Collect events
        List<String[]> events = new ArrayList<>(); // [label, color, daysAway]

        for (ApplicationModel app : apps) {

            // Interview Scheduled
            if ("Interview Scheduled".equals(app.getStatus())
                    && app.getDateApplied() != null) {
                try {
                    LocalDate interviewDate =
                            LocalDate.parse(app.getDateApplied());
                    if (!interviewDate.isBefore(today)
                            && !interviewDate.isAfter(lookAhead)) {
                        long days = ChronoUnit.DAYS.between(today, interviewDate);
                        String label = days == 0
                                ? "Today — Interview: " + app.getCompanyName() + " (" + app.getJobTitle() + ")"
                                : "In " + days + " day(s) — Interview: " + app.getCompanyName() + " (" + app.getJobTitle() + ")";
                        events.add(new String[]{label, "#6366F1", String.valueOf(days)});
                    }
                } catch (Exception ignored) {}
            }

            // Deadline
            if (app.getDeadline() != null) {
                try {
                    LocalDate deadlineDate = LocalDate.parse(app.getDeadline());
                    if (!deadlineDate.isBefore(today)
                            && !deadlineDate.isAfter(lookAhead)) {
                        long days = ChronoUnit.DAYS.between(today, deadlineDate);
                        String label = days == 0
                                ? "Today — Deadline: " + app.getCompanyName() + " (" + app.getJobTitle() + ")"
                                : "In " + days + " day(s) — Deadline: " + app.getCompanyName() + " (" + app.getJobTitle() + ")";
                        events.add(new String[]{label, "#EF4444", String.valueOf(days)});
                    }
                } catch (Exception ignored) {}
            }
        }

        // Sort by days away
        events.sort((a, b) ->
                Integer.parseInt(a[2]) - Integer.parseInt(b[2])
        );

        if (events.isEmpty()) {
            Label empty = new Label("No upcoming events in the next 14 days.");
            empty.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");
            upcomingEventsContainer.getChildren().add(empty);
            return;
        }

        for (String[] event : events) {
            upcomingEventsContainer.getChildren().add(
                    createEventRow(event[0], event[1])
            );
        }
    }

    private HBox createEventRow(String text, String color) {
        HBox row = new HBox(10);
        row.setStyle(
                "-fx-background-color: " + color + "22;" + // light tint
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 12 8 12;"
        );

        // Colored dot
        Label dot = new Label("⬤");
        dot.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 10px;");

        // Event text
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-weight: bold;"
        );
        HBox.setHgrow(label, Priority.ALWAYS);

        row.getChildren().addAll(dot, label);
        return row;
    }

    private void loadCharts(int userId) {

        Map<String, Integer> data =
                DButils.getApplicationStatusCounts(userId);

        if (statusBarChart != null)
            statusBarChart.getData().clear();

        if (statusPieChart != null)
            statusPieChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Applications");

        for (Map.Entry<String, Integer> entry : data.entrySet()) {

            String status = entry.getKey();
            Integer count = entry.getValue();

            XYChart.Data<String, Number> dataPoint =
                    new XYChart.Data<>(status, count);

            series.getData().add(dataPoint);

            dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    String color;
                    switch (status) {
                        case "Applied":           color = "#9244bc"; break;
                        case "Interview Scheduled": color = "#e59a20"; break;
                        case "Interviewed":       color = "#50a650"; break;
                        case "Offer Received":    color = "#e56739"; break;
                        case "Accepted":          color = "#b83f5f"; break;
                        case "Rejected":          color = "#4458bc"; break;
                        case "Withdrawn":         color = "#47a4bf"; break;
                        default:                  color = "#94A3B8";
                    }
                    newNode.setStyle("-fx-bar-fill: " + color + ";");
                }
            });

            if (statusPieChart != null) {
                statusPieChart.getData().add(
                        new PieChart.Data(status, count)
                );
            }
        }

        if (statusBarChart != null)
            statusBarChart.getData().add(series);
    }
}