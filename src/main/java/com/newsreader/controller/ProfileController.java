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
import java.util.regex.Pattern;

public class ProfileController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField roleField;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmNewPasswordField;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = Main.getCurrentUser();
        
        if (currentUser == null) {
            // Guard clause if session gets lost somehow
            handleLogout();
            return;
        }

        // Prepopulate text inputs from existing session details
        usernameField.setText(currentUser.getUsername());
        emailField.setText(currentUser.getEmail());
        roleField.setText(currentUser.getRole());
    }

    /**
     * Updates personal user profiles (username and email).
     */
    @FXML
    public void handleUpdateProfile() {
        String newUsername = usernameField.getText().trim();
        String newEmail = emailField.getText().trim();

        // 1. Validation Checks
        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Missing Fields", "Username and email cannot be empty.");
            return;
        }

        if (newUsername.contains(" ")) {
            AlertHelper.showWarning("Validation Error", "Invalid Username", "Username cannot contain spaces.");
            return;
        }

        if (!EMAIL_PATTERN.matcher(newEmail).matches()) {
            AlertHelper.showWarning("Validation Error", "Invalid Email Format", "Please enter a valid email address.");
            return;
        }

        // 2. Duplicate Checks (excluding the current user ID)
        if (isFieldDuplicateExcludingSelf("username", newUsername, currentUser.getId())) {
            AlertHelper.showWarning("Update Conflict", "Username Taken", "The username '" + newUsername + "' is already registered by another account.");
            return;
        }

        if (isFieldDuplicateExcludingSelf("email", newEmail, currentUser.getId())) {
            AlertHelper.showWarning("Update Conflict", "Email Registered", "The email '" + newEmail + "' is already registered by another account.");
            return;
        }

        // 3. Database Update
        String updateSQL = "UPDATE users SET username = ?, email = ? WHERE id = ?;";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            pstmt.setString(1, newUsername);
            pstmt.setString(2, newEmail);
            pstmt.setInt(3, currentUser.getId());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                // Update local session
                currentUser.setUsername(newUsername);
                currentUser.setEmail(newEmail);
                Main.setCurrentUser(currentUser);

                AlertHelper.showInfo("Success", "Profile Updated", "Your details have been successfully saved.");
            } else {
                AlertHelper.showError("System Error", "Update Failed", "Could not apply profile changes to the system database.");
            }
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Profile Update Failed", "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Secures and updates the password parameter.
     */
    @FXML
    public void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmNewPassword = confirmNewPasswordField.getText();

        // 1. Validation Checks
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Missing Fields", "Please complete all password fields.");
            return;
        }

        if (newPassword.length() < 6) {
            AlertHelper.showWarning("Validation Error", "Weak Password", "New password must be at least 6 characters long.");
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            AlertHelper.showWarning("Validation Error", "Mismatch", "New password confirmation does not match.");
            return;
        }

        // 2. Validate current password match
        String hashedCurrentInput = DBConnection.hashPassword(currentPassword);
        String dbPasswordHash = "";

        String selectSQL = "SELECT password FROM users WHERE id = ?;";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            pstmt.setInt(1, currentUser.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dbPasswordHash = rs.getString("password");
                }
            }
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Verification Failed", "Database error during password check: " + e.getMessage());
            return;
        }

        if (!hashedCurrentInput.equals(dbPasswordHash)) {
            AlertHelper.showWarning("Verification Error", "Incorrect Current Password", "The current password you entered is incorrect.");
            return;
        }

        // 3. Update Database with Hashed New Password
        String hashedNewPassword = DBConnection.hashPassword(newPassword);
        String updateSQL = "UPDATE users SET password = ? WHERE id = ?;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            pstmt.setString(1, hashedNewPassword);
            pstmt.setInt(2, currentUser.getId());

            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                AlertHelper.showInfo("Success", "Password Changed", "Your password has been changed successfully.");
                
                // Clear input boxes
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmNewPasswordField.clear();
            } else {
                AlertHelper.showError("System Error", "Update Failed", "Could not apply password changes to the system database.");
            }
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Password Update Failed", "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Dynamic navigation redirecting back to user dashboard.
     */
    @FXML
    public void handleNavigateToDashboard() {
        if ("Admin".equalsIgnoreCase(currentUser.getRole())) {
            Main.switchScene("/com/newsreader/fxml/admin_dashboard.fxml", "AI News Reader - Admin Dashboard");
        } else {
            Main.switchScene("/com/newsreader/fxml/user_dashboard.fxml", "AI News Reader - Dashboard");
        }
    }

    /**
     * Invalidates session and exits to login screen.
     */
    @FXML
    public void handleLogout() {
        Main.setCurrentUser(null);
        Main.switchScene("/com/newsreader/fxml/login.fxml", "AI News & Notice Reader - Sign In");
    }

    /**
     * Checks if a field exists in other user records (duplicate validation).
     */
    private boolean isFieldDuplicateExcludingSelf(String fieldName, String value, int selfId) {
        String checkSQL = "SELECT COUNT(*) AS total FROM users WHERE " + fieldName + " = ? AND id != ?;";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSQL)) {
            
            pstmt.setString(1, value);
            pstmt.setInt(2, selfId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Duplicate check error excluding self on field [" + fieldName + "]: " + e.getMessage());
        }
        return false;
    }
}
