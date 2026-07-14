package com.newsreader;

import com.newsreader.database.DBConnection;
import com.newsreader.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static Stage primaryStage;
    private static User currentUser;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        
        // Initialize SQLite Database schema and baseline records
        DBConnection.initializeDatabase();
        
        // Load the initial Sign-In page
        switchScene("/com/newsreader/fxml/login.fxml", "AI News & Notice Reader - Sign In");
        primaryStage.show();
    }

    /**
     * Helper method to dynamically switch the central scene.
     * @param fxmlPath The path to the target FXML file (relative to resources)
     * @param title The title for the primary stage
     */
    public static void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            // Check if current scene exists to reuse window sizing bounds, or create new scene
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Fatal: Failed to transition scene for FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
