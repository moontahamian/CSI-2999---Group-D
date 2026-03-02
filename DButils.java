package com.example.demo2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DButils {
    public static int current_user_id = -1;
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

            if (rs.next()) {
                current_user_id = rs.getInt("id");
                System.out.println("Login successful. Current User ID: " + current_user_id);
                return true;
            } 

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        return false;
    }


//calendar data stuff
    public record Calendar_Entry(String title_text, String notes_text) {} //this is so we can return 2 strings of data
    //Create Calendar data table
    public static void create_calendar_table() {
        String sql = """
        CREATE TABLE IF NOT EXISTS calendar_data (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            date_string TEXT,
            title_text TEXT,
            notes_text TEXT,
            UNIQUE(user_id, date_string)
        );
        """;

        try (Connection connection = connect();
        Statement statement = connection.createStatement()) {
        
        statement.execute(sql);
        System.out.println("Calendar data table is ready.");

        } catch (SQLException e) {
            System.out.println("Error creating calendar table: " + e.getMessage());
        }
    }

    public static void update_calendar_date(int user_id, String date, String title_text, String notes_text) {
        String sql = "INSERT OR REPLACE INTO calendar_data(user_id, date_string, title_text, notes_text) VALUES(?, ?, ?, ?)";

        try (Connection connection = connect();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, user_id);
            pstmt.setString(2, date);
            pstmt.setString(3, title_text);
            pstmt.setString(4, notes_text);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Calendar_Entry get_calendar_data(int user_id, String date) {
        String sql = "SELECT title_text, notes_text FROM calendar_data WHERE user_id = ? AND date_string = ?";
            
        try (Connection connection = connect();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, user_id);
            pstmt.setString(2, date);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) { //if data found, return an entry with both title and notes
                return new Calendar_Entry(rs.getString("title_text"), rs.getString("notes_text"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Calendar_Entry("", "");
    }
}
