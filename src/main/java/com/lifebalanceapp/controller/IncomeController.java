package com.lifebalanceapp.controller;

import com.lifebalanceapp.dto.IncomeCreateRequestDto;
import com.lifebalanceapp.dto.IncomeResponseDto;
import com.lifebalanceapp.model.Income;
import com.lifebalanceapp.repository.CategoryRepository;
import com.lifebalanceapp.repository.UserRepository;
import com.lifebalanceapp.service.IncomeService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {
    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService){
        this.incomeService = incomeService;
    }

    @GetMapping
    public List<IncomeResponseDto> getByUser(@RequestParam Integer userId){
        return incomeService.getIncomesByUser(userId);
    }

    @PostMapping
    public IncomeResponseDto create(@RequestBody IncomeCreateRequestDto request){
        return incomeService.createIncome(request);
    }


}