package com.lifebalanceapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class ManualAllocationRequestDto {

    @NotNull
    private Integer savingsTransactionId;

    @NotNull
    private Integer goalId;

    @NotNull
    @Positive
    private BigDecimal amount;

//    public Integer getSavingsTransactionId() {
//        return savingsTransactionId;
//    }

//    public void setSavingsTransactionId(Integer savingsTransactionId) {
//        this.savingsTransactionId = savingsTransactionId;
//    }

   public Integer getGoalId() {
        return goalId;
    }

    public void setGoalId(Integer goalId) {
        this.goalId = goalId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}