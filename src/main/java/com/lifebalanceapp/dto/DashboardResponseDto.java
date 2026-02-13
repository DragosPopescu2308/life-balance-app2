package com.lifebalanceapp.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponseDto {

    private String month;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalSavings;
    private BigDecimal net;

    private List<CategoryTotalDto> topExpenseCategories;

    public DashboardResponseDto() {}

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

    public BigDecimal getTotalExpense() { return totalExpense; }
    public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }

    public BigDecimal getTotalSavings() { return totalSavings; }
    public void setTotalSavings(BigDecimal totalSavings) { this.totalSavings = totalSavings; }

    public BigDecimal getNet() { return net; }
    public void setNet(BigDecimal net) { this.net = net; }

    public List<CategoryTotalDto> getTopExpenseCategories() { return topExpenseCategories; }
    public void setTopExpenseCategories(List<CategoryTotalDto> topExpenseCategories) { this.topExpenseCategories = topExpenseCategories; }

    public static class CategoryTotalDto {
        private Integer categoryId;
        private String categoryName;
        private BigDecimal total;

        public Integer getCategoryId() { return categoryId; }
        public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
    }
}
