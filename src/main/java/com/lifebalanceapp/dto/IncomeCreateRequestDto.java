package com.lifebalanceapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IncomeCreateRequestDto {
    private Integer userId;
    private Integer categoryId;
    private String title;
    private BigDecimal amount;
    private LocalDate dateReceived;
    private String notes;

    public IncomeCreateRequestDto(){
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getDateReceived() { return dateReceived; }
    public void setDateReceived(LocalDate dateReceived) { this.dateReceived = dateReceived; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
