package com.trekking.app;

import com.trekking.app.util.UTF8Control;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class TrekkingApp extends Application {
    private static Stage primaryStage;
    public static boolean isDarkModeActive = true;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.getIcons().add(new Image(TrekkingApp.class.getResourceAsStream("/images/trek-sync-logo.png")));
        primaryStage.setWidth(1800); // <-- UPDATED from 1600
        primaryStage.setHeight(1000);
        showLoginScreen();
    }

    public static void showLoginScreen() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("lang.lang", Locale.getDefault(), new UTF8Control());
            FXMLLoader loader = new FXMLLoader(TrekkingApp.class.getResource("/view/login.fxml"), bundle);
            Parent root = loader.load();
            Scene scene = new Scene(root, 700, 600);

            if (isDarkModeActive) {
                scene.getStylesheets().add(TrekkingApp.class.getResource("/dark-styles.css").toExternalForm());
            } else {
                scene.getStylesheets().add(TrekkingApp.class.getResource("/styles.css").toExternalForm());
            }

            primaryStage.setScene(scene);
            primaryStage.setTitle("Trek Sync Nepal");
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAdminDashboard(boolean darkMode) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("lang.lang", Locale.getDefault(), new UTF8Control());
            FXMLLoader loader = new FXMLLoader(TrekkingApp.class.getResource("/view/admin_dashboard.fxml"), bundle);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            scene.getStylesheets().clear();
            if (darkMode) {
                scene.getStylesheets().add(TrekkingApp.class.getResource("/dark-styles.css").toExternalForm());
            } else {
                scene.getStylesheets().add(TrekkingApp.class.getResource("/styles.css").toExternalForm());
            }
            isDarkModeActive = darkMode;
            primaryStage.setScene(scene);
            primaryStage.setTitle("Admin Dashboard");
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showTouristDashboard(boolean darkMode) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("lang.lang", Locale.getDefault(), new UTF8Control());
            FXMLLoader loader = new FXMLLoader(TrekkingApp.class.getResource("/view/tourist_dashboard.fxml"), bundle);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            scene.getStylesheets().clear();
            if (darkMode) {
                scene.getStylesheets().add(TrekkingApp.class.getResource("/dark-styles.css").toExternalForm());
            } else {
                scene.getStylesheets().add(TrekkingApp.class.getResource("/styles.css").toExternalForm());
            }
            isDarkModeActive = darkMode;
            primaryStage.setScene(scene);
            primaryStage.setTitle("Tourist Dashboard");
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    public static boolean isDarkModeActive() {
        return isDarkModeActive;
    }
    public static void main(String[] args) {
        launch(args);
    }
}