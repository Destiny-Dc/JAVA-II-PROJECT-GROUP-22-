package com.example.project_i;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class RemindersController {

    @FXML private TextField reminderTextField;
    @FXML private TextField dateTextField;
    @FXML private TextField descriptionTextField;
    @FXML private TableView<Reminder> remindersTable;
    @FXML private TableColumn<Reminder, Integer> idColumn;
    @FXML private TableColumn<Reminder, String> reminderNameColumn;
    @FXML private TableColumn<Reminder, LocalDate> reminderDateColumn;
    @FXML private TableColumn<Reminder, String> descriptionColumn;

    private ObservableList<Reminder> reminderList;

    @FXML
    private void initialize() {
        reminderList = FXCollections.observableArrayList();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        reminderNameColumn.setCellValueFactory(new PropertyValueFactory<>("reminderName"));
        reminderDateColumn.setCellValueFactory(new PropertyValueFactory<>("reminderDate"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadReminders();
    }

    @FXML
    private void handleAddReminder() {
        String reminderName = reminderTextField.getText();
        String reminderDate = dateTextField.getText();
        String description = descriptionTextField.getText();

        if (!reminderName.isEmpty() && !reminderDate.isEmpty() && !description.isEmpty()) {
            try (Connection connection = DBConnection.getInstance().getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO reminder (reminder_name, reminder_date, description) VALUES (?, ?, ?)"))
            {

                statement.setString(1, reminderName);
                statement.setDate(2, java.sql.Date.valueOf(reminderDate));
                statement.setString(3, description);
                statement.executeUpdate();
                loadReminders();
                reminderTextField.clear();
                dateTextField.clear();
                descriptionTextField.clear();

            } catch (SQLException e) {
                showErrorAlert("Error Adding Reminder", e.getMessage());
            }
        } else {
            showErrorAlert("Input Error", "All fields must be filled.");
        }
    }

    @FXML
    private void handleUpdateReminder() {
        Reminder selectedReminder = remindersTable.getSelectionModel().getSelectedItem();
        String reminderName = reminderTextField.getText();
        String reminderDate = dateTextField.getText();
        String description = descriptionTextField.getText();

        if (selectedReminder != null && !reminderName.isEmpty() && !reminderDate.isEmpty() && !description.isEmpty()) {
            try (Connection connection = DBConnection.getInstance().getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE reminder SET reminder_name = ?, reminder_date = ?, description = ? WHERE id = ?")) {

                statement.setString(1, reminderName);
                statement.setDate(2, java.sql.Date.valueOf(reminderDate));
                statement.setString(3, description);
                statement.setInt(4, selectedReminder.getId());
                statement.executeUpdate();
                loadReminders();
                reminderTextField.clear();
                dateTextField.clear();
                descriptionTextField.clear();
            } catch (SQLException e) {
                showErrorAlert("Error Updating Reminder", e.getMessage());
            }
        } else {
            showErrorAlert("Input Error", "Please select a reminder and fill all fields.");
        }
    }

    @FXML
    private void handleDeleteReminder() {
        Reminder selectedReminder = remindersTable.getSelectionModel().getSelectedItem();
        if (selectedReminder != null) {
            try (Connection connection = DBConnection.getInstance().getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM reminder WHERE id = ?")) {

                statement.setInt(1, selectedReminder.getId());
                statement.executeUpdate();
                loadReminders();
                reminderTextField.clear();
                dateTextField.clear();
                descriptionTextField.clear();
            } catch (SQLException e)
            {
                showErrorAlert("Error Deleting Reminder", e.getMessage());
            }
        } else {
            showErrorAlert("Selection Error", "Please select a reminder to delete.");
        }
    }

    @FXML
    private void handleBackToHomepage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("mainView.fxml"));
            Stage stage = (Stage) remindersTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showErrorAlert("Navigation Error", "Unable to load the homepage.");
        }
    }

    private void loadReminders() {
        reminderList.clear();
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM reminder");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String reminderName = resultSet.getString("reminder_name");
                LocalDate reminderDate = resultSet.getDate("reminder_date").toLocalDate();
                String description = resultSet.getString("description");
                reminderList.add(new Reminder(id, reminderName, reminderDate, description));
            }
        } catch (SQLException e) {
            showErrorAlert("Error Loading Reminders", e.getMessage());
        }
        remindersTable.setItems(reminderList);
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
