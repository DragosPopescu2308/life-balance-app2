package com.lifebalanceapp.model;

import com.lifebalanceapp.model.enums.CategoryType;

import java.time.LocalDateTime;

public class Category {
    private int id;
    private String name;
    private CategoryType type;
    private LocalDateTime createdAt;
}
