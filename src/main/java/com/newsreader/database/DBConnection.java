package com.newsreader.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String DB_URL = "jdbc:sqlite:newsreader.db";

    /**
     * Obtains a connection to the SQLite database.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Initializes the database by creating the users table if it does not exist.
     * Also inserts a default administrator account if the table is empty.
     */
    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL CHECK(role IN ('Admin', 'User'))" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create Table
            stmt.execute(createTableSQL);
            System.out.println("Database table 'users' verified/created.");

            // Check if database is empty to seed default admin
            var rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM users;");
            if (rs.next() && rs.getInt("total") == 0) {
                // Let's seed a default admin and default user
                // We'll use SHA-256 for passwords
                String defaultAdminSQL = "INSERT INTO users (username, email, password, role) " +
                        "VALUES ('admin', 'admin@newsreader.com', '" + hashPassword("admin123") + "', 'Admin');";
                String defaultUserSQL = "INSERT INTO users (username, email, password, role) " +
                        "VALUES ('john_doe', 'john@newsreader.com', '" + hashPassword("user123") + "', 'User');";
                
                stmt.execute(defaultAdminSQL);
                stmt.execute(defaultUserSQL);
                System.out.println("Seeded database with default 'admin' (password: admin123) and 'john_doe' (password: user123).");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing SQLite database: " + e.getMessage());
        }
    }

    /**
     * Utility method to hash passwords using SHA-256.
     * @param password Plaintext password
     * @return SHA-256 hash string
     */
    public static String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error hashing password", ex);
        }
    }
}
