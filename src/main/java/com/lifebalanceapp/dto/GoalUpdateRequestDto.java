package com.lifebalanceapp.dto;

import com.lifebalanceapp.model.enums.AllocationMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GoalUpdateRequestDto {

    @NotBlank
    private String title;

    @NotNull
    @Positive
    private BigDecimal targetAmount;

    private LocalDate deadline;

    @NotNull

    private BigDecimal allocationPercent;

    private Boolean active;

    public GoalUpdateRequestDto(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public BigDecimal getAllocationPercent() {
        return allocationPercent;
    }

    public void setAllocationPercent(BigDecimal allocationPercent) {
        this.allocationPercent = allocationPercent;
    }





    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
