package com.lifebalanceapp.service;

import com.lifebalanceapp.dto.ExpenseCreateRequestDto;
import com.lifebalanceapp.dto.ExpenseResponseDto;
import com.lifebalanceapp.dto.ExpenseUpdateRequestDto;
import com.lifebalanceapp.exception.NotFoundException;
import com.lifebalanceapp.model.Category;
import com.lifebalanceapp.model.Expense;
import com.lifebalanceapp.model.User;
import com.lifebalanceapp.model.enums.CategoryType;
import com.lifebalanceapp.repository.CategoryRepository;
import com.lifebalanceapp.repository.ExpenseRepository;
import com.lifebalanceapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ExpenseResponseDto> getExpensesByUser(Integer userId) {
        return expenseRepository.findByUser_Id(userId).stream().map(this::toDtoExpense).toList();
    }

    public List<ExpenseResponseDto> getExpensesByUserAndMonth(Integer userId, String month) {
        if (month == null || month.isBlank()) {
            return getExpensesByUser(userId);
        }

        YearMonth ym = YearMonth.parse(month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.plusMonths(1).atDay(1);

        return expenseRepository
                .findByUser_IdAndDateSpentBetween(userId, start, end)
                .stream()
                .map(this::toDtoExpense)
                .toList();
    }

    public ExpenseResponseDto getById(Integer id, Integer userId) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Expense not found"));


        if (!expense.getUser().getId().equals(userId)) {
            throw new NotFoundException("Expense not found");
        }

        return toDtoExpense(expense);
    }

    @Transactional
    public ExpenseResponseDto create(Integer userId, ExpenseCreateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setDateSpent(request.getDateSpent());
        expense.setNotes(request.getNotes());
        expense.setCreatedAt(LocalDateTime.now());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));

            if (category.getType() != CategoryType.EXPENSE) {
                throw new IllegalArgumentException("Category must be EXPENSE");
            }


            if (!category.getUser().getId().equals(userId)) {
                throw new NotFoundException("Category not found");
            }

            expense.setCategory(category);
        }

        Expense saved = expenseRepository.save(expense);
        return toDtoExpense(saved);
    }

    @Transactional
    public ExpenseResponseDto update(Integer id, Integer userId, ExpenseUpdateRequestDto request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Expense not found"));

        if (!expense.getUser().getId().equals(userId)) {
            throw new NotFoundException("Expense not found");
        }

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setDateSpent(request.getDateSpent());
        expense.setNotes(request.getNotes());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));

            if (category.getType() != CategoryType.EXPENSE) {
                throw new IllegalArgumentException("Category must be EXPENSE");
            }


            if (!category.getUser().getId().equals(userId)) {
                throw new NotFoundException("Category not found");
            }

            expense.setCategory(category);
        }

        Expense saved = expenseRepository.save(expense);
        return toDtoExpense(saved);
    }

    @Transactional
    public void delete(Integer id, Integer userId) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Expense not found"));

        if (!expense.getUser().getId().equals(userId)) {
            throw new NotFoundException("Expense not found");
        }

        expenseRepository.delete(expense);
    }

    private ExpenseResponseDto toDtoExpense(Expense expense) {
        ExpenseResponseDto dto = new ExpenseResponseDto();
        dto.setId(expense.getId());
        dto.setTitle(expense.getTitle());
        dto.setAmount(expense.getAmount());
        dto.setDateSpent(expense.getDateSpent());
        dto.setNotes(expense.getNotes());
        dto.setCreatedAt(expense.getCreatedAt());
        dto.setUserId(expense.getUser().getId());
        dto.setCategoryId(expense.getCategory() != null ? expense.getCategory().getId() : null);
        return dto;
    }
}
