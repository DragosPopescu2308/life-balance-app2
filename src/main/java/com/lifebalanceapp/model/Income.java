package com.lifebalanceapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Income {
    private int id;
    private String title;
    private BigDecimal amount;
    private LocalDateTime dateRecived;
    private String notes;
    private LocalDateTime createdAt;

    private User user;
    private Category category;
}
