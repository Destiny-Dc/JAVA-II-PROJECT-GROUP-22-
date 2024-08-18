package com.example.project_i;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Please fill all the fields.");
            return;
        }

        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                String salt = resultSet.getString("salt");

                // Debugging prints
                System.out.println("Username: " + username);
                System.out.println("Entered Password: " + password);
                System.out.println("Salt: " + salt);
                System.out.println("Stored Password: " + storedPassword);

                if (validatePassword(password, storedPassword, salt)) {
                    loadMainView(username);
                } else {
                    showAlert("Authentication Error", "Invalid username or password.");
                }
            } else {
                showAlert("Authentication Error", "Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to connect to the database.");
        }
    }

    private void loadMainView(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
            Parent root = loader.load();

            // Get the controller and set the username
            MainViewController mainViewController = loader.getController();
            mainViewController.setUsername(username);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showAlert("Navigation Error", "Unable to load the main view.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showAlert("Navigation Error", "Unable to load the register screen.");
        }
    }

    private boolean validatePassword(String password, String storedPassword, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            String encodedPassword = Base64.getEncoder().encodeToString(hashedPassword);

            // Debugging print
            System.out.println("Hashed Password: " + encodedPassword);

            return encodedPassword.equals(storedPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }
}
