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
                initializeDatabaseTables(); // Initialize tables when credentials are loaded
            }
        } catch (IOException ex) {
            System.err.println("Error reading db.properties file: " + ex.getMessage());
        }
    }

    private static void initializeDatabaseTables() {
        // This ensures the necessary tables (Users, Lost, Found) exist when the application starts
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            
            // 1. Users Table (for login/signup)
            String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                                      "id INT AUTO_INCREMENT PRIMARY KEY," +
                                      "username VARCHAR(255) NOT NULL UNIQUE," +
                                      "password VARCHAR(255) NOT NULL" +
                                      ");";
            stmt.execute(createUsersTable);

            // 2. Found Items Table (Used by ReportFoundItem.java)
            String createFoundItemsTable = "CREATE TABLE IF NOT EXISTS founditem (" +
                                           "id INT AUTO_INCREMENT PRIMARY KEY," +
                                           "name VARCHAR(255) NOT NULL," +
                                           "category VARCHAR(100)," + 
                                           "foundat VARCHAR(255) NOT NULL," +
                                           "datefound DATE," + 
                                           "description TEXT," +
                                           "contactinfo VARCHAR(255)" +
                                           ");";
            stmt.execute(createFoundItemsTable);

            // 3. Lost Items Table (Used by ReportLostItem.java)
            String createLostItemsTable = "CREATE TABLE IF NOT EXISTS Lost_Items (" +
                                          "id INT AUTO_INCREMENT PRIMARY KEY," +
                                          "user_id INT NOT NULL," +
                                          "item_name VARCHAR(255) NOT NULL," +
                                          "category VARCHAR(100)," +
                                          "description TEXT," +
                                          "lost_date DATE," +
                                          "lost_location VARCHAR(255)," +
                                          "contact_email VARCHAR(255)," +
                                          "FOREIGN KEY (user_id) REFERENCES Users(id)" +
                                          ");";
            stmt.execute(createLostItemsTable);

            System.out.println("Database tables initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Error initializing database tables: " + e.getMessage());
        }
    }

    public static Connection connect() throws SQLException {
        // Establishes a new connection to MySQL
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

    public static boolean validateUser(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if user exists
            
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
            return false;
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
     * The calling method (MyLostReports) MUST handle connection closure.
     */
    public static ResultSet autoMatchFoundItems(String itemName, String category) throws SQLException {
        
        String searchPattern = "%" + itemName.toLowerCase() + "%";
        
        String sql = "SELECT name, foundat, datefound, description, contactinfo FROM founditem WHERE " +
                     "(LOWER(name) LIKE ? OR LOWER(description) LIKE ?) " + 
                     "AND category = ? " +
                     "ORDER BY datefound DESC LIMIT 5"; 
        
        Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, searchPattern);
        pstmt.setString(2, searchPattern);
        pstmt.setString(3, category);
        
        return pstmt.executeQuery(); 
    }
    
    /**
     * SEARCH LOGIC: Used by SearchFoundItems to fetch recent or searched items.
     * The calling method (SearchFoundItems) MUST handle connection closure.
     */
    public static ResultSet searchFoundItems(String keyword, String category, Connection conn) throws SQLException {
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
    
    /**
     * DELETE LOGIC: Deletes a lost item report by its ID.
     */
    public static boolean deleteLostItem(int itemId) throws SQLException {
        String sql = "DELETE FROM Lost_Items WHERE id = ?";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting lost item: " + e.getMessage());
            throw e;
        }
    }
}
