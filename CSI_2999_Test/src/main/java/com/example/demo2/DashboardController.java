package com.example.demo2;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.util.Map;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalAppsLabel;
    @FXML private Label totalInterviewLabel;
    @FXML private BarChart<String, Number> statusBarChart;
    @FXML private PieChart statusPieChart;

    @FXML
    public void initialize() {

        String username = Session.getCurrentUsername();

        if (username != null && welcomeLabel != null) {
            welcomeLabel.setText("WELCOME, " + username.toUpperCase());
        }

        Integer userId = Session.getCurrentUserId();
        if (userId == null) return;

        // Top statistics
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

            // BAR CHART
            XYChart.Data<String, Number> dataPoint =
                    new XYChart.Data<>(status, count);

            series.getData().add(dataPoint);

            dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {

                if (newNode != null) {

                    String color;

                    switch (status) {

                        case "Applied":
                            color = "#9244bc";
                            break;

                        case "Interview Scheduled":
                            color = "#e59a20";
                            break;

                        case "Interviewed":
                            color = "#50a650";
                            break;

                        case "Offer Received":
                            color = "#e56739";
                            break;

                        case "Accepted":
                            color = "#b83f5f";
                            break;

                        case "Rejected":
                            color = "#4458bc";
                            break;

                        case "Withdrawn":
                            color = "#47a4bf";
                            break;

                        default:
                            color = "#94A3B8";
                    }

                    newNode.setStyle("-fx-bar-fill: " + color + ";");
                }
            });

            // PIE CHART
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