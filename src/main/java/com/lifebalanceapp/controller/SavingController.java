package com.lifebalanceapp.controller;

import com.lifebalanceapp.model.SavingSetting;
import com.lifebalanceapp.repository.SavingSettingRepository;
import com.lifebalanceapp.repository.SavingsTransactionRepository;
import com.lifebalanceapp.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/savings")
public class SavingController {

    private final SavingSettingRepository savingSettingRepository;
    private final SavingsTransactionRepository savingsTransactionRepository;
    private final UserRepository userRepository;

    public SavingController(SavingSettingRepository savingSettingRepository,
                            SavingsTransactionRepository savingsTransactionRepository,
                            UserRepository userRepository) {
        this.savingSettingRepository = savingSettingRepository;
        this.savingsTransactionRepository = savingsTransactionRepository;
        this.userRepository = userRepository;
    }

    private Integer requireUserId(HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        return userId;
    }

    @GetMapping("/settings")
    public SavingSetting get(HttpSession session){
        Integer userId = requireUserId(session);

        return savingSettingRepository.findById(userId).orElseGet(() -> {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            SavingSetting s = new SavingSetting();
            s.setUser(user);
            s.setPercentage(10.0);
            s.setActive(true);
            return savingSettingRepository.save(s);
        });
    }

    @PutMapping("/settings")
    public SavingSetting update(@RequestBody SavingSetting req, HttpSession session){
        Integer userId = requireUserId(session);

        SavingSetting s = savingSettingRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Settings not found"));

        s.setPercentage(req.getPercentage());
        s.setActive(req.getActive());
        return savingSettingRepository.save(s);
    }

    @GetMapping("/monthly")
    public BigDecimal monthlyTotal(@RequestParam String month, HttpSession session){
        Integer userId = requireUserId(session);

        YearMonth ym = YearMonth.parse(month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.plusMonths(1).atDay(1);

        return savingsTransactionRepository.findByUser_IdAndDateSavedBetween(userId, start, end)
                .stream()
                .map(tx -> tx.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
