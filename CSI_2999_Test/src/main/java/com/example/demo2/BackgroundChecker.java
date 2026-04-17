package com.example.demo2;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.sql.*;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BackgroundChecker {

    private static final String DB_URL = DButils.getDatabaseUrl();
    private static final int DAYS_BEFORE = 2;

    public static void main(String[] args) {

        System.out.println("BackgroundChecker running...");

        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray not supported.");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(DAYS_BEFORE);
        String targetStr = targetDate.toString();
        String todayStr = today.toString();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            // Get all users
            PreparedStatement usersPs = conn.prepareStatement("SELECT id FROM users");
            ResultSet usersRs = usersPs.executeQuery();

            while (usersRs.next()) {

                int userId = usersRs.getInt("id");

                // Check applications
                PreparedStatement appsPs = conn.prepareStatement(
                        "SELECT company_name, job_title, status, date_applied, deadline FROM applications WHERE user_id=?"
                );
                appsPs.setInt(1, userId);
                ResultSet appsRs = appsPs.executeQuery();

                while (appsRs.next()) {

                    String company = appsRs.getString("company_name");
                    String jobTitle = appsRs.getString("job_title");
                    String status = appsRs.getString("status");
                    String dateApplied = appsRs.getString("date_applied");
                    String deadline = appsRs.getString("deadline");

                    // Interview Scheduled
                    if ("Interview Scheduled".equals(status) && targetStr.equals(dateApplied)) {
                        String msg = "Interview in 2 days: " + company + " – " + jobTitle;
                        insertNotificationIfNew(conn, userId, msg, todayStr);
                        sendDesktopNotification("Interview Reminder", msg);
                    }

                    // Deadline
                    if (deadline != null && targetStr.equals(deadline)) {
                        String msg = "Deadline in 2 days: " + company + " – " + jobTitle;
                        insertNotificationIfNew(conn, userId, msg, todayStr);
                        sendDesktopNotification("Deadline Reminder", msg);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Wait a few seconds so tray notification has time to show
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("BackgroundChecker done.");
    }

    private static void insertNotificationIfNew(Connection conn, int userId, String message, String today) {
        try {
            // Check if already exists today
            PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM notifications WHERE user_id=? AND message=? AND created_at=?"
            );
            check.setInt(1, userId);
            check.setString(2, message);
            check.setString(3, today);
            ResultSet rs = check.executeQuery();
            if (rs.next()) return; // already exists

            // Insert
            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO notifications (user_id, message, created_at) VALUES (?, ?, ?)"
            );
            insert.setInt(1, userId);
            insert.setString(2, message);
            insert.setString(3, today);
            insert.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendDesktopNotification(String title, String message) {
        try {
            SystemTray tray = SystemTray.getSystemTray();

            java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                    16, 16, java.awt.image.BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = image.createGraphics();
            g2d.setColor(java.awt.Color.decode("#6366F1"));
            g2d.fillOval(0, 0, 16, 16);
            g2d.dispose();

            TrayIcon trayIcon = new TrayIcon(image, "NextStep");
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);

            trayIcon.displayMessage(title, message, MessageType.INFO);

            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                tray.remove(trayIcon);
            }, 5, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}