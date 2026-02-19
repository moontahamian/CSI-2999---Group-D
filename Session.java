package com.example.demo2;

public class Session {

    private static String currentUser;

    public static void setCurrentUser(String username) {
        currentUser = username;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}
