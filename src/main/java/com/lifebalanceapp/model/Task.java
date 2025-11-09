package com.lifebalanceapp.model;

import com.lifebalanceapp.model.enums.TaskPriority;
import com.lifebalanceapp.model.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private int id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate deadline;
    private LocalDateTime completedAt;

    private User user;

}
