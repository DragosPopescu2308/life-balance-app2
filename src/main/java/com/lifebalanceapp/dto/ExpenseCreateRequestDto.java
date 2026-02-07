package com.lifebalanceapp.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseCreateRequestDto {
    private Integer userId;

    @NotNull(message = "Category is required")
    private Integer categoryId;

    private String title;
    private BigDecimal amount;
    private LocalDate dateSpent;
    private String notes;

    public ExpenseCreateRequestDto(){}

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getDateSpent() { return dateSpent; }
    public void setDateSpent(LocalDate dateSpent) { this.dateSpent = dateSpent; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
