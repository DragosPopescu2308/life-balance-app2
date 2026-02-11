package com.lifebalanceapp.controller;

import com.lifebalanceapp.dto.ExpenseCreateRequestDto;
import com.lifebalanceapp.dto.ExpenseResponseDto;
import com.lifebalanceapp.dto.ExpenseUpdateRequestDto;
import com.lifebalanceapp.service.ExpenseService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService){
        this.expenseService = expenseService;
    }

    private Integer requireUserId(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {

            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Not authenticated"
            );
        }
        return userId;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDto>> getByUser(
            HttpSession session,
            @RequestParam(required = false) String month
    ){
        Integer userId = requireUserId(session);
        return ResponseEntity.ok(expenseService.getExpensesByUserAndMonth(userId, month));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> getById(
            HttpSession session,
            @PathVariable Integer id
    ){
        Integer userId = requireUserId(session);
        return ResponseEntity.ok(expenseService.getById(id, userId));
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDto> create(
            HttpSession session,
            @RequestBody @Valid ExpenseCreateRequestDto request
    ){
        Integer userId = requireUserId(session);
        ExpenseResponseDto dto = expenseService.create(userId, request);
        return ResponseEntity.status(201).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> update(
            HttpSession session,
            @PathVariable Integer id,
            @RequestBody @Valid ExpenseUpdateRequestDto request
    ){
        Integer userId = requireUserId(session);
        return ResponseEntity.ok(expenseService.update(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            HttpSession session,
            @PathVariable Integer id
    ){
        Integer userId = requireUserId(session);
        expenseService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
