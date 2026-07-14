package com.newsreader.controller;

import com.newsreader.Main;
import com.newsreader.database.DBConnection;
import com.newsreader.model.User;
import com.newsreader.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label adminUsersLabel;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set dynamic welcome text based on the logged-in session
        if (Main.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome back, " + Main.getCurrentUser().getUsername() + "!");
        }

        // Configure TableView columns with User class property bindings
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Fetch data
        loadDashboardData();
    }

    /**
     * Refreshes dashboard data.
     */
    @FXML
    public void handleRefresh() {
        loadDashboardData();
    }

    /**
     * Navigates to user profile.
     */
    @FXML
    public void handleNavigateToProfile() {
        Main.switchScene("/com/newsreader/fxml/profile.fxml", "AI News Reader - Account Profile");
    }

    /**
     * Logs out the user.
     */
    @FXML
    public void handleLogout() {
        Main.setCurrentUser(null);
        Main.switchScene("/com/newsreader/fxml/login.fxml", "AI News Reader - Sign In");
    }

    /**
     * Queries SQLite database for statistics and user tables.
     */
    private void loadDashboardData() {
        userList.clear();
        int totalUsers = 0;
        int adminCount = 0;

        String queryUsers = "SELECT id, username, email, role FROM users;";
        String queryStats = "SELECT " +
                "(SELECT COUNT(*) FROM users) AS total_users, " +
                "(SELECT COUNT(*) FROM users WHERE role = 'Admin') AS admin_users;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmtUsers = conn.prepareStatement(queryUsers);
             PreparedStatement pstmtStats = conn.prepareStatement(queryStats)) {

            // 1. Load users table directory
            try (ResultSet rsUsers = pstmtUsers.executeQuery()) {
                while (rsUsers.next()) {
                    userList.add(new User(
                            rsUsers.getInt("id"),
                            rsUsers.getString("username"),
                            rsUsers.getString("email"),
                            "[PROTECTED]",
                            rsUsers.getString("role")
                    ));
                }
            }
            usersTable.setItems(userList);

            // 2. Load metrics
            try (ResultSet rsStats = pstmtStats.executeQuery()) {
                if (rsStats.next()) {
                    totalUsers = rsStats.getInt("total_users");
                    adminCount = rsStats.getInt("admin_users");
                }
            }

            totalUsersLabel.setText(String.valueOf(totalUsers));
            adminUsersLabel.setText(String.valueOf(adminCount));

        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Retrieval Failed", "Could not load dashboard statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
