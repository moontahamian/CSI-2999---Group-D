package com.example.demo2;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.sql.*;

public class DashboardController {

    @FXML
    private Label totalAppsLabel;

    @FXML
    private Label totalInterviewLabel;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private PieChart pieChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    public void initialize() {
        loadStats();
        loadBarChart();
        loadPieChart();
    }

    // SQLite connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:NextStepDB.db");
    }

    // Generic count query helper
    private int countQuery(String sql) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Application statistics
    private int totalApplications() {
        return countQuery("SELECT COUNT(*) FROM applications");
    }

    private int totalApplied() {
        return countQuery("SELECT COUNT(*) FROM applications WHERE status='Applied'");
    }

    private int totalInterviewing() {
        return countQuery("SELECT COUNT(*) FROM applications WHERE status='Interviewing'");
    }

    private int totalOffer() {
        return countQuery("SELECT COUNT(*) FROM applications WHERE status='Offer'");
    }

    private int totalRejected() {
        return countQuery("SELECT COUNT(*) FROM applications WHERE status='Rejected'");
    }

    // Update the top-level labels
    private void loadStats() {
        totalAppsLabel.setText(String.valueOf(totalApplications()));
        totalAppsLabel.setTextFill(Color.web("#22c55e")); // green

        totalInterviewLabel.setText(String.valueOf(totalInterviewing()));
        totalInterviewLabel.setTextFill(Color.web("#06b6d4")); // blue
    }

    // Populate the BarChart
    private void loadBarChart() {
        barChart.getData().clear();
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Applied", totalApplied()));
        series.getData().add(new XYChart.Data<>("Interviewing", totalInterviewing()));
        series.getData().add(new XYChart.Data<>("Offer", totalOffer()));
        series.getData().add(new XYChart.Data<>("Rejected", totalRejected()));

        barChart.getData().add(series);

        // Set xAxis categories
        xAxis.setCategories(FXCollections.observableArrayList("Applied", "Interviewing", "Offer", "Rejected"));
    }

    // Populate the PieChart
    private void loadPieChart() {
        pieChart.getData().clear();

        PieChart.Data applied = new PieChart.Data("Applied", totalApplied());
        PieChart.Data interviewing = new PieChart.Data("Interviewing", totalInterviewing());
        PieChart.Data offer = new PieChart.Data("Offer", totalOffer());
        PieChart.Data rejected = new PieChart.Data("Rejected", totalRejected());

        pieChart.setData(FXCollections.observableArrayList(applied, interviewing, offer, rejected));

        //set slice colors
        applied.getNode().setStyle("-fx-pie-color: #4ade80;");       // green
        interviewing.getNode().setStyle("-fx-pie-color: #60a5fa;");  // blue
        offer.getNode().setStyle("-fx-pie-color: #facc15;");         // yellow
        rejected.getNode().setStyle("-fx-pie-color: #f87171;");      // red
    }
}

