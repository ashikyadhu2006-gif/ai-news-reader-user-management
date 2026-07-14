package com.newsreader.util;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

public class AlertHelper {

    /**
     * Shows an information alert to the user.
     */
    public static void showInfo(String title, String header, String content) {
        showAlert(Alert.AlertType.INFORMATION, title, header, content);
    }

    /**
     * Shows a warning alert to the user.
     */
    public static void showWarning(String title, String header, String content) {
        showAlert(Alert.AlertType.WARNING, title, header, content);
    }

    /**
     * Shows an error alert to the user.
     */
    public static void showError(String title, String header, String content) {
        showAlert(Alert.AlertType.ERROR, title, header, content);
    }

    private static void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Apply our custom CSS stylesheet to the dialog pane for visual coherence
        try {
            DialogPane dialogPane = alert.getDialogPane();
            String cssPath = AlertHelper.class.getResource("/com/newsreader/css/styles.css").toExternalForm();
            dialogPane.getStylesheets().add(cssPath);
            dialogPane.getStyleClass().add("root");
            
            // Subtle styling adjustment for alert pane elements
            dialogPane.setStyle("-fx-background-color: #1e293b;");
            dialogPane.lookup(".label.content").setStyle("-fx-text-fill: #e2e8f0;");
            if (dialogPane.lookup(".label.header-panel") != null) {
                dialogPane.lookup(".label.header-panel").setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
            }
        } catch (Exception e) {
            System.err.println("Could not apply stylesheet to alert dialog: " + e.getMessage());
        }

        alert.showAndWait();
    }
}
