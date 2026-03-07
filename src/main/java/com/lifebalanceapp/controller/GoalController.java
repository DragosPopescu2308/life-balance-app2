package com.lifebalanceapp.controller;

import com.lifebalanceapp.dto.GoalRequestDto;
import com.lifebalanceapp.dto.GoalResponseDto;
import com.lifebalanceapp.dto.GoalUpdateRequestDto;
import com.lifebalanceapp.dto.ManualAllocationRequestDto;
import com.lifebalanceapp.model.Goal;
import com.lifebalanceapp.model.SavingsTransaction;
import com.lifebalanceapp.repository.GoalRepository;
import com.lifebalanceapp.repository.SavingsTransactionRepository;
import com.lifebalanceapp.service.GoalAllocationService;
import com.lifebalanceapp.service.GoalService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {
    private final GoalService goalService;
    private final SavingsTransactionRepository savingsTransactionRepository;
    private final GoalRepository goalRepository;
    private final GoalAllocationService goalAllocationService;

    public GoalController(GoalService goalService, SavingsTransactionRepository savingsTransactionRepository, GoalRepository goalRepository, GoalAllocationService goalAllocationService){
        this.goalService = goalService;
        this.savingsTransactionRepository = savingsTransactionRepository;
        this.goalRepository=goalRepository;
        this.goalAllocationService = goalAllocationService;
    }

    private int requireUserId(HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        if(userId == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return userId;
    }

    @GetMapping
    public ResponseEntity<List<GoalResponseDto>> getGoals(HttpSession session){
        Integer userId = requireUserId(session);
        return ResponseEntity.ok(goalService.getForUser(userId));
    }

    @PostMapping
    public ResponseEntity<GoalResponseDto> createGoal(HttpSession session, @RequestBody @Valid GoalRequestDto requestDto){
        Integer userId = requireUserId(session);
        GoalResponseDto dto = goalService.createGoal(userId, requestDto);
        return ResponseEntity.status(201).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(HttpSession session, @PathVariable Integer id){
        Integer userId = requireUserId(session);
        goalService.delete(userId,id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponseDto> updateGoal(
            HttpSession session,
            @PathVariable Integer id,
            @RequestBody @Valid GoalUpdateRequestDto dto
    ) {
        Integer userId = requireUserId(session);
        return ResponseEntity.ok(goalService.update(userId, id, dto));
    }



}
