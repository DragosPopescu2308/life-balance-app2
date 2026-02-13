package com.lifebalanceapp.controller;

import com.lifebalanceapp.dto.CategoryCreateRequestDto;
import com.lifebalanceapp.dto.CategoryResponseDto;
import com.lifebalanceapp.dto.CategoryUpdateDTO;
import com.lifebalanceapp.model.enums.CategoryType;
import com.lifebalanceapp.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    private Integer requireUserId(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return userId;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllForUser(
            HttpSession session,
            @RequestParam(required = false) CategoryType type
    ) {
        Integer userId = requireUserId(session);
        return ResponseEntity.ok(categoryService.getForUser(userId, type));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(
            HttpSession session,
            @RequestBody @Valid CategoryCreateRequestDto request
    ) {
        Integer userId = requireUserId(session);
        CategoryResponseDto dto = categoryService.create(userId, request);
        return ResponseEntity.status(201).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> update(
            HttpSession session,
            @PathVariable Integer id,
            @RequestBody @Valid CategoryUpdateDTO dto
    ) {
        Integer userId = requireUserId(session);
        return ResponseEntity.ok(categoryService.update(userId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            HttpSession session,
            @PathVariable Integer id
    ) {
        Integer userId = requireUserId(session);
        categoryService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
