package com.lifebalanceapp.repository;

import com.lifebalanceapp.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Integer> {
     List<Goal> findByUser_Id(Integer userId);
}
