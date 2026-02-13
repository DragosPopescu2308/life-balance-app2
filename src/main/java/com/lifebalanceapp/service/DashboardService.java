package com.lifebalanceapp.service;

import com.lifebalanceapp.dto.DashboardResponseDto;
import com.lifebalanceapp.dto.DashboardTrendPointDto;
import com.lifebalanceapp.repository.ExpenseRepository;
import com.lifebalanceapp.repository.IncomeRepository;
import com.lifebalanceapp.repository.SavingsTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final SavingsTransactionRepository savingsRepository;

    public DashboardService(IncomeRepository incomeRepository,
                            ExpenseRepository expenseRepository,
                            SavingsTransactionRepository savingsRepository) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.savingsRepository = savingsRepository;
    }

    public DashboardResponseDto getMonthlyDashboard(Integer userId, String month) {
        YearMonth ym = (month == null || month.isBlank())
                ? YearMonth.now()
                : YearMonth.parse(month);

        LocalDate start = ym.atDay(1);
        LocalDate end = ym.plusMonths(1).atDay(1);

        BigDecimal totalIncome = incomeRepository
                .findByUser_IdAndDateReceivedBetween(userId, start, end)
                .stream()
                .map(i -> i.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = expenseRepository
                .findByUser_IdAndDateSpentBetween(userId, start, end)
                .stream()
                .map(e -> e.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSavings = savingsRepository
                .findByUser_IdAndDateSavedBetween(userId, start, end)
                .stream()
                .map(tx -> tx.getAmount()) // BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal net = totalIncome.subtract(totalExpense).subtract(totalSavings);

        List<DashboardResponseDto.CategoryTotalDto> topCats = expenseRepository
                .sumByCategoryForMonth(userId, start, end)
                .stream()
                .map(row -> {
                    DashboardResponseDto.CategoryTotalDto dto = new DashboardResponseDto.CategoryTotalDto();
                    dto.setCategoryId((Integer) row[0]);
                    dto.setCategoryName((String) row[1]);
                    dto.setTotal((BigDecimal) row[2]);
                    return dto;
                })
                .toList();

        DashboardResponseDto dto = new DashboardResponseDto();
        dto.setMonth(ym.toString());
        dto.setTotalIncome(totalIncome);
        dto.setTotalExpense(totalExpense);
        dto.setTotalSavings(totalSavings);
        dto.setNet(net);
        dto.setTopExpenseCategories(topCats);

        return dto;
    }


    public List<DashboardTrendPointDto> getTrend(Integer userId, int months, String endMonth) {
        int safeMonths = Math.max(1, Math.min(months, 24));
        YearMonth endYm = (endMonth == null || endMonth.isBlank())
                ? YearMonth.now()
                : YearMonth.parse(endMonth);

        List<DashboardTrendPointDto> out = new ArrayList<>();

        for (int i = safeMonths - 1; i >= 0; i--) {
            YearMonth ym = endYm.minusMonths(i);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.plusMonths(1).atDay(1);

            BigDecimal income = incomeRepository
                    .findByUser_IdAndDateReceivedBetween(userId, start, end)
                    .stream()
                    .map(x -> x.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal expense = expenseRepository
                    .findByUser_IdAndDateSpentBetween(userId, start, end)
                    .stream()
                    .map(x -> x.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal savings = savingsRepository
                    .findByUser_IdAndDateSavedBetween(userId, start, end)
                    .stream()
                    .map(x -> x.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal net = income.subtract(expense).subtract(savings);

            DashboardTrendPointDto p = new DashboardTrendPointDto();
            p.setMonth(ym.toString());
            p.setIncome(income);
            p.setExpense(expense);
            p.setSavings(savings);
            p.setNet(net);

            out.add(p);
        }

        return out;
    }
}
