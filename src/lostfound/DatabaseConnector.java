package lostfound;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnector {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/lostandfound_db";
    private static String DB_USER;
    private static String DB_PASSWORD;

    static {
        // Reads credentials from db.properties file
        Properties prop = new Properties();
        
        try (InputStream input = DatabaseConnector.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("Fatal Error: db.properties file not found. Cannot connect to database.");
            } else {
                prop.load(input);
                DB_USER = prop.getProperty("db.user");
                DB_PASSWORD = prop.getProperty("db.password");
                // initializeDatabaseTables() logic should be present here in your code
            }
        } catch (IOException ex) {
            System.err.println("Error reading db.properties file: " + ex.getMessage());
        }
    }

    // NOTE: initializeDatabaseTables() logic is assumed to be present here in your working code.

    public static Connection connect() throws SQLException {
        if (DB_USER == null || DB_PASSWORD == null) {
            throw new SQLException("Database credentials not loaded. Check db.properties file.");
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Ensure the Connector/J JAR is in the module path.");
            throw new SQLException("JDBC Driver not found.");
        }
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }

    public static int validateUser(String username, String password) {
        String sql = "SELECT id FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1;
            
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
            return -1;
        }
    }

    public static boolean registerUser(String username, String password) {
        String sql = "INSERT INTO Users(username, password) VALUES(?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Error: Username already exists.");
            } else {
                System.err.println("Error registering user: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * AUTO-MATCH LOGIC: Searches the founditem table for matching items.
     * FIX: Accepts the active connection (conn) to maintain transaction stability.
     */
    public static ResultSet autoMatchFoundItems(String itemName, String category, Connection conn) throws SQLException {
        
        String searchPattern = "%" + itemName.toLowerCase() + "%";
        
        String sql = "SELECT name, foundat, datefound, description, contactinfo FROM founditem WHERE " +
                     "(LOWER(name) LIKE ? OR LOWER(description) LIKE ?) " + 
                     "AND category = ? " +
                     "ORDER BY datefound DESC LIMIT 5"; 
        
        // Use the connection passed in the argument
        PreparedStatement pstmt = conn.prepareStatement(sql); 
        
        pstmt.setString(1, searchPattern);
        pstmt.setString(2, searchPattern);
        pstmt.setString(3, category);
        
        return pstmt.executeQuery(); 
    }
    
    // R: Search logic for SearchFoundItems.java (Assumed to be correct)
    public static ResultSet searchFoundItems(String keyword, String category, Connection conn) throws SQLException {
        // NOTE: Implementation of this method is necessary for your SearchFoundItems.java
        String searchPattern = "%" + keyword + "%";
        
        String sql;
        if (keyword.isEmpty() && category.equals("All Categories")) {
            sql = "SELECT id, name, category, foundat, datefound, description, contactinfo FROM founditem ORDER BY datefound DESC LIMIT 10";
        } else {
            sql = "SELECT id, name, category, foundat, datefound, description, contactinfo FROM founditem WHERE " +
                  "(name LIKE ? OR description LIKE ?)";
            if (!category.equals("All Categories")) {
                sql += " AND category = ?";
            }
            sql += " ORDER BY datefound DESC";
        }

        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        if (!(keyword.isEmpty() && category.equals("All Categories"))) {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            if (!category.equals("All Categories")) {
                pstmt.setString(3, category);
            }
        }
        
        return pstmt.executeQuery();
    }
    
    // D: Delete Lost Item Report
    public static boolean deleteLostItem(int itemId, int userId) throws SQLException {
        String sql = "DELETE FROM Lost_Items WHERE id = ? AND user_id = ?";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting lost item: " + e.getMessage());
            throw e;
        }
    }
    
    // U: Update Lost Item Report (NOTE: This method is needed for full CRUD)
    public static boolean updateLostItem(int itemId, String name, String category, String location, String date, String description, String email) throws SQLException {
        String sql = "UPDATE Lost_Items SET " +
                     "item_name = ?, category = ?, lost_location = ?, lost_date = ?, description = ?, contact_email = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setString(3, location);
            pstmt.setString(4, date);
            pstmt.setString(5, description);
            pstmt.setString(6, email);
            pstmt.setInt(7, itemId); 
            
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating lost item: " + e.getMessage());
            throw e;
        }
    }
}
