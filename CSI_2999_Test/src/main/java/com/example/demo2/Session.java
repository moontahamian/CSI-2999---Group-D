package com.example.demo2;

public class Session {

    private static Integer currentUserId;
    private static String currentUsername;


    // Setters


    public static void setCurrentUserId(Integer userId) {
        currentUserId = userId;
    }

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }


    // Getters


    public static Integer getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }


    // Clear session on logout


    public static void clearSession() {
        currentUserId = null;
        currentUsername = null;
    }
}