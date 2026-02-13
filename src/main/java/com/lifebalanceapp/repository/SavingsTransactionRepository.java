package com.lifebalanceapp.repository;

import com.lifebalanceapp.model.SavingsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SavingsTransactionRepository extends JpaRepository<SavingsTransaction, Integer> {

    List<SavingsTransaction> findByUser_IdAndDateSavedBetween(Integer userId, LocalDate start, LocalDate end);

    boolean existsByIncome_Id(Integer incomeId);
}
