package com.lifebalanceapp.dto;

import com.lifebalanceapp.model.enums.CategoryType;
import java.time.LocalDateTime;

public class CategoryResponseDto {
    private Integer id;
    private String name;
    private CategoryType type;
    private LocalDateTime createdAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CategoryType getType() { return type; }
    public void setType(CategoryType type) { this.type = type; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
