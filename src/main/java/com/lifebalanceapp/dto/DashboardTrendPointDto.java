package com.lifebalanceapp.dto;

import java.math.BigDecimal;

public class DashboardTrendPointDto {
    private String month;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal savings;
    private BigDecimal net;

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }

    public BigDecimal getExpense() { return expense; }
    public void setExpense(BigDecimal expense) { this.expense = expense; }

    public BigDecimal getSavings() { return savings; }
    public void setSavings(BigDecimal savings) { this.savings = savings; }

    public BigDecimal getNet() { return net; }
    public void setNet(BigDecimal net) { this.net = net; }
}
