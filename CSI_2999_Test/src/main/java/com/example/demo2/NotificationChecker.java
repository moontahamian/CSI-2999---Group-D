package com.example.demo2;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationChecker {

    private static final int DAYS_BEFORE =2;
    private static ScheduledExecutorService scheduler;

    public static void start() {

        // Set app name for Windows notifications
        Toolkit.getDefaultToolkit();
        System.setProperty("java.awt.appName", "NextStep");

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "notification-checker");
            t.setDaemon(true);
            return t;
        });

        // Run immediately, then every hour
        scheduler.scheduleAtFixedRate(
                NotificationChecker::checkAndNotify,
                0, 1, TimeUnit.HOURS
        );
    }

    public static void stop() {
        if (scheduler != null) scheduler.shutdown();
    }

    public static void checkNow() {
        checkAndNotify();
    }

    private static void checkAndNotify() {

        System.out.println("Checker running...");

        Integer userId = Session.getCurrentUserId();
        System.out.println("UserId in checker: " + userId);

        if (userId == null) return;

        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(DAYS_BEFORE);
        String targetStr = targetDate.toString();

        List<ApplicationModel> apps = DButils.getApplicationsByUser(userId);

        for (ApplicationModel app : apps) {

            // Interview Scheduled
            if ("Interview Scheduled".equals(app.getStatus())) {
                if (targetStr.equals(DAYS_BEFORE)) {
                    String msg = "Interview in 2 days: " + app.getCompanyName()
                            + " – " + app.getJobTitle();
                    DButils.insertNotification(userId, msg);
                    sendDesktopNotification("Interview Reminder", msg);
                }
            }

            // Application Deadline
            if (app.getDeadline() != null && targetStr.equals(app.getDeadline())) {
                String msg = "Deadline in 2 days: " + app.getCompanyName()
                        + " – " + app.getJobTitle();
                DButils.insertNotification(userId, msg);
                sendDesktopNotification("Deadline Reminder", msg);
            }
        }
    }

    private static void sendDesktopNotification(String title, String message) {
        if (!SystemTray.isSupported()) return;

        try {
            SystemTray tray = SystemTray.getSystemTray();

            // Create a simple colored icon ( currently not working and idk why)
            java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = image.createGraphics();
            g2d.setColor(java.awt.Color.decode("#6366F1"));
            g2d.fillOval(0, 0, 16, 16);
            g2d.dispose();

            TrayIcon trayIcon = new TrayIcon(image, "NextStep");
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);

            trayIcon.displayMessage(title, message, MessageType.INFO);

            // Remove icon after a delay
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                tray.remove(trayIcon);
            }, 3, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}