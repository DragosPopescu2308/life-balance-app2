package com.lifebalanceapp.service;

import com.lifebalanceapp.model.Income;
import com.lifebalanceapp.model.SavingSetting;
import com.lifebalanceapp.model.SavingsTransaction;
import com.lifebalanceapp.model.User;
import com.lifebalanceapp.repository.GoalAllocationRepository;
import com.lifebalanceapp.repository.SavingSettingRepository;
import com.lifebalanceapp.repository.SavingsTransactionRepository;
import com.lifebalanceapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class SavingService {

    private final SavingSettingRepository settingRepo;
    private final SavingsTransactionRepository savingsRepo;
    private final UserRepository userRepo;
    private final GoalAllocationService goalAllocationService;
    private final GoalAllocationRepository goalAllocationRepository;

    public SavingService(SavingSettingRepository settingRepo,
                         SavingsTransactionRepository savingsRepo,
                         UserRepository userRepo,
                         GoalAllocationService goalAllocationService, GoalAllocationRepository goalAllocationRepository) {
        this.settingRepo = settingRepo;
        this.savingsRepo = savingsRepo;
        this.userRepo = userRepo;
        this.goalAllocationService = goalAllocationService;
        this.goalAllocationRepository = goalAllocationRepository;
    }

    @Transactional
    public void processSavings(Income income){
        if (income == null || income.getUser() == null || income.getId() == null) return;


        if (savingsRepo.existsByIncome_Id(income.getId())) return;

        Integer userId = income.getUser().getId();


        SavingSetting setting = settingRepo.findById(userId).orElseGet(() -> {
            User u = userRepo.findById(userId).orElseThrow();
            SavingSetting s = new SavingSetting();
            s.setUser(u);
            s.setPercentage(10.0);
            s.setActive(true);
            return settingRepo.save(s);
        });

        if (!Boolean.TRUE.equals(setting.getActive())) return;

        double pctRaw = setting.getPercentage() == null ? 0.0 : setting.getPercentage();
        if (pctRaw <= 0) return;

        BigDecimal pct = BigDecimal.valueOf(pctRaw)
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

        BigDecimal incomeAmount = income.getAmount() == null ? BigDecimal.ZERO : income.getAmount();

        BigDecimal saveAmount = incomeAmount
                .multiply(pct)
                .setScale(2, RoundingMode.HALF_UP);

        if (saveAmount.compareTo(BigDecimal.ZERO) <= 0) return;

        SavingsTransaction st = new SavingsTransaction();
        st.setUser(income.getUser());
        st.setIncome(income);
        st.setAmount(saveAmount);
        st.setDateSaved(income.getDateReceived());
        st.setCreatedAt(LocalDateTime.now());

        savingsRepo.save(st);
        goalAllocationService.allocateAuto(st);
    }

    public BigDecimal getRemainingSavings(Integer userId){

        BigDecimal totalSavings =
                savingsRepo.sumSavingsForUser(userId);

        BigDecimal allocated =
                goalAllocationRepository.sumAllocatedForUser(userId);

        return totalSavings.subtract(allocated).max(BigDecimal.ZERO);
    }
}
