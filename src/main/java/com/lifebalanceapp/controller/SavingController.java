package com.lifebalanceapp.controller;

import com.lifebalanceapp.dto.SavingSettingDto;
import com.lifebalanceapp.model.SavingSetting;
import com.lifebalanceapp.repository.SavingSettingRepository;
import com.lifebalanceapp.repository.SavingsTransactionRepository;
import com.lifebalanceapp.repository.UserRepository;
import com.lifebalanceapp.service.SavingService;
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
    private final SavingService savingService;

    public SavingController(SavingSettingRepository savingSettingRepository,
                            SavingsTransactionRepository savingsTransactionRepository,
                            UserRepository userRepository, SavingService savingService) {
        this.savingSettingRepository = savingSettingRepository;
        this.savingsTransactionRepository = savingsTransactionRepository;
        this.userRepository = userRepository;
        this.savingService = savingService;
    }

    private Integer requireUserId(HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        return userId;
    }

    @GetMapping("/settings")
    public SavingSettingDto get(HttpSession session){
        Integer userId = requireUserId(session);

        SavingSetting s = savingSettingRepository.findById(userId).orElseGet(() -> {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            SavingSetting created = new SavingSetting();
            created.setUser(user);
            created.setPercentage(10.0);
            created.setActive(true);
            return savingSettingRepository.save(created);
        });

        SavingSettingDto dto = new SavingSettingDto();
        dto.setPercentage(s.getPercentage());
        dto.setActive(s.getActive());
        return dto;
    }

    @PutMapping("/settings")
    public SavingSettingDto update(@RequestBody SavingSettingDto req, HttpSession session){
        Integer userId = requireUserId(session);

        SavingSetting s = savingSettingRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Settings not found"));

        s.setPercentage(req.getPercentage());
        s.setActive(req.getActive());
        SavingSetting saved = savingSettingRepository.save(s);

        SavingSettingDto dto = new SavingSettingDto();
        dto.setPercentage(saved.getPercentage());
        dto.setActive(saved.getActive());
        return dto;
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

    @GetMapping("/remaining")
    public BigDecimal remaining(HttpSession session){
        Integer userId = requireUserId(session);
        return savingService.getRemainingSavings(userId);
    }
}
