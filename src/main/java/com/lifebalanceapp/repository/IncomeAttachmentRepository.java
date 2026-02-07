package com.lifebalanceapp.repository;

import com.lifebalanceapp.model.IncomeAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IncomeAttachmentRepository extends JpaRepository<IncomeAttachment, Integer> {

    List<IncomeAttachment> findByIncome_Id(Integer incomeId);

    @Query("""
        select a from IncomeAttachment a
        join fetch a.income i
        join fetch i.user u
        where a.id = :id
    """)
    Optional<IncomeAttachment> findByIdWithIncomeAndUser(@Param("id") Integer id);
}
