package com.example.project_i;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class HabitController implements Initializable {

    @FXML private TextField habitField;
    @FXML private DatePicker startDatePicker;
    @FXML private ChoiceBox<String> statusChoiceBox;
    @FXML private TableView<Habit> habitTableView;
    @FXML private TableColumn<Habit, Integer> idColumn;
    @FXML private TableColumn<Habit, String> habitNameColumn;
    @FXML private TableColumn<Habit, LocalDate> startDateColumn;
    @FXML private TableColumn<Habit, String> statusColumn;
    @FXML private PieChart habitPieChart;

    private ObservableList<Habit> habitList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusChoiceBox.setItems(FXCollections.observableArrayList("Pending", "Completed"));
        habitList = FXCollections.observableArrayList();
        habitTableView.setItems(habitList);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        habitNameColumn.setCellValueFactory(new PropertyValueFactory<>("habitName"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        loadHabits();
        populatePieChart();
    }

    @FXML
    private void handleAddHabit() {
        String habitName = habitField.getText();
        LocalDate startDate = startDatePicker.getValue();
        String status = statusChoiceBox.getValue();

        if (habitName.isEmpty() || startDate == null || status == null) {
            showAlert("Validation Error", "Please fill all the fields.");
            return;
        }

        String sql = "INSERT INTO habits (habit_name, start_date, status) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, habitName);
            preparedStatement.setDate(2, java.sql.Date.valueOf(startDate));
            preparedStatement.setString(3, status);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                habitField.clear();
                startDatePicker.setValue(null);
                statusChoiceBox.setValue(null);
                loadHabits();
                populatePieChart();
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error occurred while adding the habit.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteHabit() {
        Habit selectedHabit = habitTableView.getSelectionModel().getSelectedItem();
        if (selectedHabit != null) {
            try (Connection connection = DBConnection.getInstance().getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM habits WHERE id = ?")) {

                statement.setInt(1, selectedHabit.getId());
                statement.executeUpdate();
                loadHabits();
                populatePieChart();
                habitField.clear();
                startDatePicker.setValue(null);
                statusChoiceBox.setValue(null);
            } catch (SQLException e) {
                showAlert("Database Error", "Error occurred while deleting the habit.");
                e.printStackTrace();
            }
        } else {
            showAlert("Selection Error", "Please select a habit to delete.");
        }
    }

    @FXML
    private void handleUpdateHabit() {
        Habit selectedHabit = habitTableView.getSelectionModel().getSelectedItem();
        String habitName = habitField.getText();
        LocalDate startDate = startDatePicker.getValue();
        String status = statusChoiceBox.getValue();

        if (selectedHabit != null && !habitName.isEmpty() && startDate != null && status != null) {
            String sql = "UPDATE habits SET habit_name = ?, start_date = ?, status = ? WHERE id = ?";

            try (Connection connection = DBConnection.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, habitName);
                preparedStatement.setDate(2, java.sql.Date.valueOf(startDate));
                preparedStatement.setString(3, status);
                preparedStatement.setInt(4, selectedHabit.getId());

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    habitField.clear();
                    startDatePicker.setValue(null);
                    statusChoiceBox.setValue(null);
                    loadHabits();
                    populatePieChart();
                }
            } catch (SQLException e) {
                showAlert("Database Error", "Error occurred while updating the habit.");
                e.printStackTrace();
            }
        } else {
            showAlert("Validation Error", "Please select a habit and fill all the fields.");
        }
    }

    private void loadHabits() {
        habitList.clear();
        String sql = "SELECT id, habit_name, start_date, status FROM habits";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String habitName = resultSet.getString("habit_name");
                LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
                String status = resultSet.getString("status");
                habitList.add(new Habit(id, habitName, startDate, status));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error occurred while fetching the habits.");
            e.printStackTrace();
        }
    }

    private void populatePieChart() {
        habitPieChart.getData().clear();
        String sql = "SELECT status, COUNT(*) AS count FROM habits GROUP BY status";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String status = resultSet.getString("status");
                int count = resultSet.getInt("count");
                PieChart.Data slice = new PieChart.Data(status, count);
                habitPieChart.getData().add(slice);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error occurred while fetching data for the pie chart.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
            Stage stage = (Stage) habitTableView.getScene().getWindow();
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
}
