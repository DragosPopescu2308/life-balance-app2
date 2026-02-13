package com.lifebalanceapp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "savings_transactions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_savings_income", columnNames = {"income_id"})
        }
)
public class SavingsTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "income_id", nullable = false)
    private Income income;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "date_saved", nullable = false)
    private LocalDate dateSaved;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public SavingsTransaction() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Income getIncome() { return income; }
    public void setIncome(Income income) { this.income = income; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getDateSaved() { return dateSaved; }
    public void setDateSaved(LocalDate dateSaved) { this.dateSaved = dateSaved; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
