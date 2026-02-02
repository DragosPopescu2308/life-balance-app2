package com.lifebalanceapp.controller;

import com.lifebalanceapp.dto.ExpenseCreateRequestDto;
import com.lifebalanceapp.dto.ExpenseResponseDto;
import com.lifebalanceapp.model.Expense;
import com.lifebalanceapp.service.ExpenseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService){
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<ExpenseResponseDto> getExpenseByUser(@RequestParam Integer userId){
        return expenseService.getExpensesByUser(userId);
    }

    @PostMapping
    public ExpenseResponseDto create(@RequestBody ExpenseCreateRequestDto request){
        return expenseService.create(request);
    }


}