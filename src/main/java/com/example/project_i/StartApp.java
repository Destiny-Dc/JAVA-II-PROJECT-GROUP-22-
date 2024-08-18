package com.example.project_i;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        primaryStage.setScene(new Scene(root, 715, 491));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        DBConnection.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
