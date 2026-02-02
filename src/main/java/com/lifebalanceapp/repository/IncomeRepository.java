package com.lifebalanceapp.repository;

import com.lifebalanceapp.model.Income;
import com.lifebalanceapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Integer>{
    List<Income> findByUser_Id(Integer userId);
}