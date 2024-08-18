package com.example.project_i;

import java.time.LocalDate;

public class Reminder {
    private int id;
    private int userId;
    private String reminderName;
    private LocalDate reminderDate;
    private String description;

    public Reminder(int id, String reminderName, LocalDate reminderDate, String description) {
        this.id = id;
        this.userId = this.userId;
        this.reminderName = reminderName;
        this.reminderDate = reminderDate;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getReminderName() {
        return reminderName;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public String getDescription() {
        return description;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setReminderName(String reminderName) {
        this.reminderName = reminderName;
    }

    public void setReminderDate(LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

