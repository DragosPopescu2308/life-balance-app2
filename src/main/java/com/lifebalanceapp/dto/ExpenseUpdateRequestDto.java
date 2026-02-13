package com.lifebalanceapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseUpdateRequestDto {

    @NotNull(message = "Category is required")
    private Integer categoryId;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be at most 100 characters")
    private String title;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotNull(message = "Date spent is required")
    private LocalDate dateSpent;

    private String notes;

    public ExpenseUpdateRequestDto() {}

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
