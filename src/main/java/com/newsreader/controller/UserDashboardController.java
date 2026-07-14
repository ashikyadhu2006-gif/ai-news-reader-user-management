package com.newsreader.controller;

import com.newsreader.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        if (Main.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome back, " + Main.getCurrentUser().getUsername() + "!");
        }
    }

    /**
     * Redirects to the Profile Settings view.
     */
    @FXML
    public void handleNavigateToProfile() {
        Main.switchScene("/com/newsreader/fxml/profile.fxml", "AI News Reader - Account Profile");
    }

    /**
     * Handles logout and session invalidation.
     */
    @FXML
    public void handleLogout() {
        Main.setCurrentUser(null);
        Main.switchScene("/com/newsreader/fxml/login.fxml", "AI News & Notice Reader - Sign In");
    }
}
