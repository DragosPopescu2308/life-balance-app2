package com.lifebalanceapp.controller;

import com.lifebalanceapp.dto.IncomeCreateRequestDto;
import com.lifebalanceapp.dto.IncomeResponseDto;
import com.lifebalanceapp.dto.IncomeUpdateRequestDto;
import com.lifebalanceapp.service.IncomeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService){
        this.incomeService = incomeService;
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
    public ResponseEntity<List<IncomeResponseDto>> getByUser(
            HttpSession session,
            @RequestParam(required = false) String month
    ){
        Integer userId = requireUserId(session);
        return ResponseEntity.ok(incomeService.getIncomesByUserAndMonth(userId, month));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeResponseDto> getById(
            HttpSession session,
            @PathVariable Integer id
    ){
        Integer userId = requireUserId(session);
        return ResponseEntity.ok(incomeService.getById(id, userId));
    }

    @PostMapping
    public ResponseEntity<IncomeResponseDto> create(
            HttpSession session,
            @RequestBody @Valid IncomeCreateRequestDto request
    ){
        Integer userId = requireUserId(session);
        IncomeResponseDto dto = incomeService.create(userId, request);
        return ResponseEntity.status(201).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponseDto> update(
            HttpSession session,
            @PathVariable Integer id,
            @RequestBody @Valid IncomeUpdateRequestDto request
    ){
        Integer userId = requireUserId(session);
        return ResponseEntity.ok(incomeService.update(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            HttpSession session,
            @PathVariable Integer id
    ){
        Integer userId = requireUserId(session);
        incomeService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
