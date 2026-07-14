package com.newsreader.controller;

import com.newsreader.Main;
import com.newsreader.database.DBConnection;
import com.newsreader.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    // Simple Email Regex validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    @FXML
    public void initialize() {
        // Populate system role options
        roleComboBox.setItems(FXCollections.observableArrayList("User", "Admin"));
        roleComboBox.setValue("User"); // Set default
    }

    /**
     * Executes user registration logic.
     */
    @FXML
    public void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String role = roleComboBox.getValue();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // 1. Validation Checks
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Missing Fields", "All fields are required to register.");
            return;
        }

        // Validate username format (no spaces, basic alphanumeric)
        if (username.contains(" ")) {
            AlertHelper.showWarning("Validation Error", "Invalid Username", "Username cannot contain spaces.");
            return;
        }

        // Email Validation
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            AlertHelper.showWarning("Validation Error", "Invalid Email Format", "Please enter a valid email address.");
            return;
        }

        // Password Length
        if (password.length() < 6) {
            AlertHelper.showWarning("Validation Error", "Weak Password", "Password must be at least 6 characters long.");
            return;
        }

        // Password Match Check
        if (!password.equals(confirmPassword)) {
            AlertHelper.showWarning("Validation Error", "Passwords Do Not Match", "Please ensure password confirmation matches.");
            return;
        }

        // 2. Duplicate Checks (Username and Email)
        if (isFieldDuplicate("username", username)) {
            AlertHelper.showWarning("Registration Conflict", "Username Taken", "The username '" + username + "' is already registered.");
            return;
        }

        if (isFieldDuplicate("email", email)) {
            AlertHelper.showWarning("Registration Conflict", "Email Registered", "The email address '" + email + "' is already associated with an account.");
            return;
        }

        // 3. Database Insertion with PreparedStatement to secure against injection
        String insertSQL = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?);";
        String hashedPassword = DBConnection.hashPassword(password);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, role);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                AlertHelper.showInfo("Success", "Registration Complete", "User '" + username + "' has been successfully registered!");
                // Clear and navigate to Login
                Main.switchScene("/com/newsreader/fxml/login.fxml", "AI News Reader - Sign In");
            } else {
                AlertHelper.showError("Registration Error", "System Error", "Failed to insert record into the system database.");
            }

        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Insertion Failed", "Database writing error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Swaps view back to login.
     */
    @FXML
    public void handleNavigateToLogin() {
        Main.switchScene("/com/newsreader/fxml/login.fxml", "AI News Reader - Sign In");
    }

    /**
     * Checks if a field value already exists in the database.
     */
    private boolean isFieldDuplicate(String fieldName, String value) {
        String checkSQL = "SELECT COUNT(*) AS total FROM users WHERE " + fieldName + " = ?;";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSQL)) {
            
            pstmt.setString(1, value);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Duplicate check database error for field [" + fieldName + "]: " + e.getMessage());
        }
        return false;
    }
}
