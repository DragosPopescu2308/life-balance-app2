package com.lifebalanceapp.dto;

public class SavingSettingDto {
    private Double percentage;
    private Boolean active;

    public SavingSettingDto(Double percentage, Boolean active) {
        this.percentage = percentage;
        this.active = active;
    }

    public SavingSettingDto(){}

    public Double getPercentage() { return percentage; }
    public Boolean getActive() { return active; }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
