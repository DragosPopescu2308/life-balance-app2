package com.lifebalanceapp.service;

import com.lifebalanceapp.model.Goal;
import com.lifebalanceapp.model.GoalAllocation;
import com.lifebalanceapp.model.SavingsTransaction;
import com.lifebalanceapp.model.User;
import com.lifebalanceapp.repository.GoalAllocationRepository;
import com.lifebalanceapp.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoalAllocationService {


    private final GoalRepository goalRepository;
    private final GoalAllocationRepository goalAllocationRepository;

    public GoalAllocationService(
            GoalRepository goalRepository,
            GoalAllocationRepository goalAllocationRepository
    ){
        this.goalRepository = goalRepository;
        this.goalAllocationRepository = goalAllocationRepository;
    }

    public void allocateAuto(SavingsTransaction savingsTransaction){

        User user = savingsTransaction.getUser();
        BigDecimal totalSavings = savingsTransaction.getAmount();

        List<Goal> goals = goalRepository.findByUser_Id(user.getId()).stream()
                .filter(Goal::isActive)
                .toList();

        if(goals.isEmpty()){
            return;
        }

        for(Goal goal : goals){

            BigDecimal percent = goal.getAllocationPercent();

            if(percent == null || percent.compareTo(BigDecimal.ZERO) <= 0){
                continue;
            }

            BigDecimal amountForGoal =
                    totalSavings
                            .multiply(percent)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if(amountForGoal.compareTo(BigDecimal.ZERO) <= 0){
                continue;
            }

            GoalAllocation allocation = new GoalAllocation();
            allocation.setUser(user);
            allocation.setGoal(goal);
            allocation.setSavingsTransaction(savingsTransaction);
            allocation.setAmount(amountForGoal);
            allocation.setCreatedAt(LocalDateTime.now());

            goalAllocationRepository.save(allocation);
        }
    }


}
