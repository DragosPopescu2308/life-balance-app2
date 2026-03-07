package com.lifebalanceapp.dto;

import com.lifebalanceapp.model.enums.AllocationMode;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GoalResponseDto {
    private Integer id;
    private String title;
    private BigDecimal savedAmount;
    private BigDecimal targetAmount;
    private Double progress;
    private Boolean active;
    private LocalDate deadline;


    private AllocationMode allocationMode;

    private BigDecimal allocationPercent;

    private BigDecimal remainingAmount;
    private Double remainingPercent;

    private BigDecimal estimatedMonthly;

    public BigDecimal getEstimatedMonthly() {
        return estimatedMonthly;
    }

    public void setEstimatedMonthly(BigDecimal estimatedMonthly) {
        this.estimatedMonthly = estimatedMonthly;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Double getRemainingPercent() {
        return remainingPercent;
    }

    public void setRemainingPercent(Double remainingPercent) {
        this.remainingPercent = remainingPercent;
    }

    public GoalResponseDto(){}



    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }



    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }



    public BigDecimal getSavedAmount() {
        return savedAmount;
    }
    public void setSavedAmount(BigDecimal savedAmount) {
        this.savedAmount = savedAmount;
    }




    public BigDecimal getTargetAmount() {
        return targetAmount;
    }
    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }




    public Double getProgress() {
        return progress;
    }
    public void setProgress(Double progress) {
        this.progress = progress;
    }




    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }




    public LocalDate getDeadline() {
        return deadline;
    }
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public AllocationMode getAllocationMode() {
        return allocationMode;
    }

    public void setAllocationMode(AllocationMode allocationMode) {
        this.allocationMode = allocationMode;
    }

    public BigDecimal getAllocationPercent() {
        return allocationPercent;
    }

    public void setAllocationPercent(BigDecimal allocationPercent) {
        this.allocationPercent = allocationPercent;
    }
}
