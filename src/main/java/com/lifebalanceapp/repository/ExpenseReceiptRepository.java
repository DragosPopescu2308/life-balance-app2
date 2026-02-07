package com.lifebalanceapp.repository;

import com.lifebalanceapp.model.ExpenseReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpenseReceiptRepository extends JpaRepository<ExpenseReceipt, Integer> {

    List<ExpenseReceipt> findByExpense_Id(Integer expenseId);


    @Query("""
        select r from ExpenseReceipt r
        join fetch r.expense e
        join fetch e.user u
        where r.id = :id
    """)
    Optional<ExpenseReceipt> findByIdWithExpenseAndUser(@Param("id") Integer id);
}
