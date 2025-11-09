package com.lifebalanceapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Expense {
    private int id;
    private String title;
    private BigDecimal amount;
    private LocalDate dateSpent;
    private String notes;
    private LocalDateTime createdAt;

    private User user;
    private Category category;
}
