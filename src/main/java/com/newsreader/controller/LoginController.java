package com.newsreader.controller;

import com.newsreader.Main;
import com.newsreader.database.DBConnection;
import com.newsreader.model.User;
import com.newsreader.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    /**
     * Authenticates user using input credentials.
     */
    @FXML
    public void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // 1. Validation Checks
        if (email.isEmpty() || password.isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Missing Fields", "Please enter both email address and password.");
            return;
        }

        // Hash the password for verification
        String hashedInputPassword = DBConnection.hashPassword(password);

        // 2. Query Database with PreparedStatement to secure against SQL injection
        String query = "SELECT id, username, email, password, role FROM users WHERE email = ? AND password = ? LIMIT 1;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, hashedInputPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Extract fields
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String userEmail = rs.getString("email");
                    String userRole = rs.getString("role");

                    // Set user session
                    User user = new User(id, username, userEmail, "[PROTECTED]", userRole);
                    Main.setCurrentUser(user);

                    // 3. Dynamic Route by Role
                    if ("Admin".equalsIgnoreCase(userRole)) {
                        Main.switchScene("/com/newsreader/fxml/admin_dashboard.fxml", "AI News Reader - Admin Dashboard");
                    } else {
                        Main.switchScene("/com/newsreader/fxml/user_dashboard.fxml", "AI News Reader - Dashboard");
                    }
                } else {
                    AlertHelper.showError("Authentication Failed", "Invalid Credentials", "The email address or password you entered is incorrect.");
                }
            }
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Query Execution Failed", "An error occurred while connecting to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Swaps view to registration form.
     */
    @FXML
    public void handleNavigateToRegister() {
        Main.switchScene("/com/newsreader/fxml/register.fxml", "AI News Reader - Create Account");
    }
}
