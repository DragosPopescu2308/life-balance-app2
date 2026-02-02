package com.lifebalanceapp.service;

import com.lifebalanceapp.dto.ExpenseCreateRequestDto;
import com.lifebalanceapp.dto.ExpenseResponseDto;
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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository){
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository =categoryRepository;
    }

    public List<ExpenseResponseDto> getExpensesByUser(Integer userId){
        return expenseRepository.findByUser_Id(userId).stream().map(this::toDtoExpense).toList();
    }

    @Transactional
    public ExpenseResponseDto create(ExpenseCreateRequestDto request){
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new NotFoundException("User not found"));

        Expense expense = new Expense();

        expense.setUser(user);
        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setDateSpent(request.getDateSpent());
        expense.setNotes(request.getNotes());
        expense.setCreatedAt(LocalDateTime.now());

        if(request.getCategoryId() != null){
            Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new NotFoundException("Category not found"));

            if(category.getType() != CategoryType.EXPENSE){
                throw new IllegalArgumentException("Category must be EXPENSE");
            }
            expense.setCategory(category);
        }

        Expense saved = expenseRepository.save(expense);
        return toDtoExpense(saved);
    }
    public ExpenseResponseDto toDtoExpense(Expense expense){
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
