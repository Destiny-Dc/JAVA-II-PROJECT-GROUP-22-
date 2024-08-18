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
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TaskController implements Initializable {

    @FXML private Button updateButton;
    @FXML private Button addTaskButton;
    @FXML private TextField taskField;
    @FXML private DatePicker dueDatePicker;
    @FXML private ChoiceBox<String> statusChoiceBox;
    @FXML private TableView<Task> taskTableView;
    @FXML private TableColumn<Task, Integer> idColumn;
    @FXML private TableColumn<Task, String> taskNameColumn;
    @FXML private TableColumn<Task, LocalDate> dueDateColumn;
    @FXML private TableColumn<Task, String> statusColumn;

    private ObservableList<Task> taskList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusChoiceBox.setItems(FXCollections.observableArrayList("Pending", "Completed"));
        taskList = FXCollections.observableArrayList();
        taskTableView.setItems(taskList);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        loadTasks();
    }

    @FXML
    private void handleAddTask() {
        String taskName = taskField.getText();
        LocalDate dueDate = dueDatePicker.getValue();
        String status = statusChoiceBox.getValue();

        if (taskName.isEmpty() || dueDate == null || status == null) {
            showAlert("Validation Error", "Please fill all the fields.");
            return;
        }

        String sql = "INSERT INTO tasks (task_name, due_date, status) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, taskName);
            preparedStatement.setDate(2, java.sql.Date.valueOf(dueDate));
            preparedStatement.setString(3, status);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                taskField.clear();
                dueDatePicker.setValue(null);
                statusChoiceBox.setValue(null);
                loadTasks();
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error occurred while adding the task.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            try (Connection connection = DBConnection.getInstance().getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM tasks WHERE id = ?")) {

                statement.setInt(1, selectedTask.getId());
                statement.executeUpdate();
                loadTasks();
                taskField.clear();
                dueDatePicker.setValue(null);
                statusChoiceBox.setValue(null);
            } catch (SQLException e) {
                showAlert("Database Error", "Error occurred while deleting the task.");
                e.printStackTrace();
            }
        } else {
            showAlert("Selection Error", "Please select a task to delete.");
        }
    }

    @FXML
    private void handleUpdateTask() {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        String taskName = taskField.getText();
        LocalDate dueDate = dueDatePicker.getValue();
        String status = statusChoiceBox.getValue();

        if (selectedTask != null && !taskName.isEmpty() && dueDate != null && status != null) {
            String sql = "UPDATE tasks SET task_name = ?, due_date = ?, status = ? WHERE id = ?";

            try (Connection connection = DBConnection.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, taskName);
                preparedStatement.setDate(2, java.sql.Date.valueOf(dueDate));
                preparedStatement.setString(3, status);
                preparedStatement.setInt(4, selectedTask.getId());

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    taskField.clear();
                    dueDatePicker.setValue(null);
                    statusChoiceBox.setValue(null);
                    loadTasks();
                }
            } catch (SQLException e) {
                showAlert("Database Error", "Error occurred while updating the task.");
                e.printStackTrace();
            }
        } else {
            showAlert("Validation Error", "Please select a task and fill all the fields.");
        }
    }

    private void loadTasks() {
        taskList.clear();
        String sql = "SELECT id, task_name, due_date, status FROM tasks";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String taskName = resultSet.getString("task_name");
                LocalDate dueDate = resultSet.getDate("due_date").toLocalDate();
                String status = resultSet.getString("status");
                taskList.add(new Task(id, taskName, dueDate, status));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error occurred while fetching the tasks.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
            Stage stage = (Stage) taskTableView.getScene().getWindow();
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
