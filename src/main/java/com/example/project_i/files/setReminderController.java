package com.example.project_i.files;

import com.example.project_i.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class setReminderController
{
@FXML
    private TextField titleField;
@FXML
    private TextField descriptionField;
@FXML
    private DatePicker dateField;
@FXML
private Label messageLabel;


@FXML
    private void handleSaveReminder()
{
    String title = titleField.getText();
    String description = descriptionField.getText();
    String date = dateField.getValue().toString();

    if (title.isEmpty() || description.isEmpty() || date.isEmpty())
    {
        messageLabel.setText("please fill in all details");
        return;
    }

    try {
        Connection conn = DBConnection.getConnection();

        String query = "INSERT INTO reminder (`reminder_name`,`reminder_date`,`description`) VALUES(?,?,?);";

        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1,title);
        statement.setString(2,date);
        statement.setString(3,description);

        statement.executeUpdate();

        messageLabel.setText("success");

    }
    catch (SQLException E)
    {
        E.printStackTrace();
        messageLabel.setText("failed");
    }
}

}
