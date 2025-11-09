package com.lifebalanceapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Goal {
    private int id;
    private String name;
    private BigDecimal targetValue;
    private BigDecimal currentValue;
    private double progress;
    private LocalDate deadline;
    private LocalDateTime completedAt;

    private User user;
}
