package com.lifebalanceapp.repository;

import com.lifebalanceapp.dto.ExpenseResponseDto;
import com.lifebalanceapp.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findByUser_Id(Integer userId);
}
