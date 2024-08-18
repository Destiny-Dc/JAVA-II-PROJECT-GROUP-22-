package com.example.project_i.files;

import com.example.project_i.DBConnection;

import java.sql.Connection;

public class testConnection {
    public static void main(String[] args) {
        try {
            Connection connection = DBConnection.getConnection();
            if (connection != null) {
                System.out.println("Connection established successfully!!");
            } else {
                System.out.println("Unable to establish connection!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
