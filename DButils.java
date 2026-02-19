package com.example.demo2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DButils {

    private static final String URL = "jdbc:sqlite:database.db";

    // Connect to database
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Create users table
    public static void createUsersTable() {

        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL
            );
            """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Users table ready.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Register new user
    public static boolean registerUser(String username, String password) {

        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false; // likely duplicate username
        }
    }

    // Validate login
    public static boolean validateLogin(String username, String password) {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // true if user found

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
