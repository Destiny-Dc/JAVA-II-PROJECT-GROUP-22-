package com.example.project_i;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML
    private Label userName;
    @FXML
    private Button reminderButton;
    @FXML
    private Button tasksButton;
    @FXML
    private Button habitsButton;
    @FXML
    private Label timeDateLabel;
    @FXML
    private MenuItem logoutMenu;
    @FXML
    private MenuItem exitMenu;
    @FXML
    private PieChart habitPieChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeDateLabel.setText(LocalDateTime.now().format(formatter));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        populatePieChart();
    }

    public void setUsername(String username) {
        userName.setText(username);
    }

    @FXML
    private void handleReminderPage() {
        loadPage("Reminders.fxml");
    }

    @FXML
    private void handleTasksPage() {
        loadPage("Task.fxml");
    }

    @FXML
    private void handleHabitsPage() {
        loadPage("Habits.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Stage stage = (Stage) timeDateLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showErrorAlert("Logout Error", "Unable to load the login page.");
        }
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) timeDateLabel.getScene().getWindow();
        stage.close();
    }

    private void loadPage(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) timeDateLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showErrorAlert("Navigation Error", "Unable to load the page: " + fxmlFile);
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void populatePieChart() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT status, COUNT(*) AS count FROM tasks GROUP BY status";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                PieChart.Data slice = new PieChart.Data(status, count);
                habitPieChart.getData().add(slice);
            }
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Unable to retrieve data for the pie chart.");
        }
    }
}
