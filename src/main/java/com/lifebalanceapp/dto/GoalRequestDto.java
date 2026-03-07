package com.lifebalanceapp.dto;

import com.lifebalanceapp.model.enums.AllocationMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public class GoalRequestDto {
    @NotBlank
    private String title;

    @NotNull
    @Positive
    private BigDecimal targetAmount;


    private BigDecimal allocationPercent;



    private LocalDate deadline;


    public GoalRequestDto(){}


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
}
