package com.example.project_i;

import java.time.LocalDate;

public class Habit {

    private int id;
    private String habitName;
    private LocalDate startDate;
    private String status;

    public Habit(int id, String habitName, LocalDate startDate, String status) {
        this.id = id;
        this.habitName = habitName;
        this.startDate = startDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
