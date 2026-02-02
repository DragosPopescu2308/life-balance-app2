package com.lifebalanceapp.controller;

import com.lifebalanceapp.dto.CategoryUpdateDTO;
import com.lifebalanceapp.model.Category;
import com.lifebalanceapp.model.enums.CategoryType;
import com.lifebalanceapp.repository.CategoryRepository;
import com.lifebalanceapp.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAllCategories();
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category){
        return categoryService.createCategory(category);
    }
    @GetMapping("/filter")
    public List<Category> getCategoriesByType(@RequestParam CategoryType categoryType){
        return categoryService.getCategoryByType(categoryType);
    }
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Integer id, @RequestBody @Valid CategoryUpdateDTO dto){
        return categoryService.updateCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Integer id){
        categoryService.deleteCategory(id);
    }

}
