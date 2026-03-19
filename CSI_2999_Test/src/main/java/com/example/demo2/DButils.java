package com.example.demo2;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DButils {

    private static final String URL = "jdbc:sqlite:newdatabase.db";

    // CONNECTION
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL);
    }

    // USERS
    public static void createUsersTable() {

        String query = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL
                );
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean registerUser(String username, String password) {

        String query = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            return false; // duplicate username
        }
    }

    public static Integer validateLogin(String username, String password) {

        String query = "SELECT id FROM users WHERE username=? AND password=?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // APPLICATIONS
    public static void createApplicationsTable() {

        String query = """
            CREATE TABLE IF NOT EXISTS applications (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                company_name TEXT NOT NULL,
                job_title TEXT NOT NULL,
                date_applied TEXT,
                deadline TEXT,
                status TEXT,
                location TEXT,
                description TEXT,
                notes TEXT,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            );
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertApplication(
            int userId,
            String companyName,
            String jobTitle,
            String dateApplied,
            String deadline,
            String status,
            String location,
            String description,
            String notes) {

        String query = """
            INSERT INTO applications
            (user_id, company_name, job_title, date_applied, deadline, status, location, description, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.setString(2, companyName);
            ps.setString(3, jobTitle);
            ps.setString(4, dateApplied);
            ps.setString(5, deadline);
            ps.setString(6, status);
            ps.setString(7, location);
            ps.setString(8, description);
            ps.setString(9, notes);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<ApplicationModel> getApplicationsByUser(int userId) {

        List<ApplicationModel> applications = new ArrayList<>();

        String query = "SELECT * FROM applications WHERE user_id=?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                ApplicationModel app = new ApplicationModel(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("company_name"),
                        rs.getString("job_title"),
                        rs.getString("date_applied"),
                        rs.getString("deadline"),
                        rs.getString("status"),
                        rs.getString("location"),
                        rs.getString("description"),
                        rs.getString("notes")
                );

                applications.add(app);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return applications;
    }

    public static void deleteApplication(int applicationId) {

        String query = "DELETE FROM applications WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, applicationId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateApplication(
            int id,
            String companyName,
            String jobTitle,
            String dateApplied,
            String deadline,
            String status,
            String location,
            String description,
            String notes) {

        String query = """
            UPDATE applications SET
                company_name=?,
                job_title=?,
                date_applied=?,
                deadline=?,
                status=?,
                location=?,
                description=?,
                notes=?
            WHERE id=?;
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, companyName);
            ps.setString(2, jobTitle);
            ps.setString(3, dateApplied);
            ps.setString(4, deadline);
            ps.setString(5, status);
            ps.setString(6, location);
            ps.setString(7, description);
            ps.setString(8, notes);
            ps.setInt(9, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CALENDAR
    public static void createCalendarTable() {

        String query = """
            CREATE TABLE IF NOT EXISTS calendar_entries (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                date TEXT NOT NULL,
                title TEXT,
                notes TEXT,
                UNIQUE(user_id, date)
            );
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Calendar Entry Record
    public static class CalendarEntry {

        private String title;
        private String notes;

        public CalendarEntry(String title, String notes) {
            this.title = title;
            this.notes = notes;
        }

        public String getTitle() {
            return title;
        }

        public String getNotes() {
            return notes;
        }
    }

    public static CalendarEntry getCalendarData(int userId, String date) {

        String query = "SELECT title, notes FROM calendar_entries WHERE user_id=? AND date=?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.setString(2, date);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new CalendarEntry(
                        rs.getString("title"),
                        rs.getString("notes")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CalendarEntry("", "");
    }

    // RESUMES
    public static void createResumesTable() {

        String query = """
            CREATE TABLE IF NOT EXISTS resumes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                file_name TEXT NOT NULL,
                pdf_data BLOB NOT NULL,
                uploaded_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            );
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ResumeEntry {

        private final int id;
        private final int userId;
        private final String fileName;
        private final byte[] pdfData;
        private final String uploadedAt;

        public ResumeEntry(int id,
                           int userId,
                           String fileName,
                           byte[] pdfData,
                           String uploadedAt) {
            this.id = id;
            this.userId = userId;
            this.fileName = fileName;
            this.pdfData = pdfData;
            this.uploadedAt = uploadedAt;
        }

        public int getId() {
            return id;
        }

        public int getUserId() {
            return userId;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getPdfData() {
            return pdfData;
        }

        public String getUploadedAt() {
            return uploadedAt;
        }
    }

    public static boolean insertResume(int userId,
                                       String fileName,
                                       byte[] pdfData) {

        String query = """
            INSERT INTO resumes (user_id, file_name, pdf_data)
            VALUES (?, ?, ?);
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.setString(2, fileName);
            ps.setBytes(3, pdfData);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<ResumeEntry> getResumesByUser(int userId) {

        List<ResumeEntry> resumes = new ArrayList<>();

        String query = """
            SELECT id, user_id, file_name, pdf_data, uploaded_at
            FROM resumes
            WHERE user_id=?
            ORDER BY uploaded_at DESC, id DESC;
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resumes.add(new ResumeEntry(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("file_name"),
                        rs.getBytes("pdf_data"),
                        rs.getString("uploaded_at")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resumes;
    }

    public static void updateCalendarDate(int userId,
                                          String date,
                                          String title,
                                          String notes) {

        String query = """
            INSERT INTO calendar_entries (user_id, date, title, notes)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(user_id, date)
            DO UPDATE SET
                title=excluded.title,
                notes=excluded.notes;
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.setString(2, date);
            ps.setString(3, title);
            ps.setString(4, notes);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TOTAL APPLICATIONS
    public static int getTotalApplications(int userId) {

        String query = "SELECT COUNT(*) FROM applications WHERE user_id=?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


// TOTAL INTERVIEWS
public static int getTotalInterviews(int userId) {

        String query = """
        SELECT COUNT(*) FROM applications
        WHERE user_id=? AND status IN
        ('Interview Scheduled','Interviewed');
    """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
    public static Map<String, Integer> getApplicationStatusCounts(int userId) {

        Map<String, Integer> result = new HashMap<>();

        String query = """
        SELECT status, COUNT(*) as total
        FROM applications
        WHERE user_id=?
        GROUP BY status
    """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                result.put(
                        rs.getString("status"),
                        rs.getInt("total")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
