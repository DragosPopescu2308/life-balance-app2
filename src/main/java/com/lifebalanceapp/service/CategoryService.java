package com.lifebalanceapp.service;

import com.lifebalanceapp.dto.CategoryUpdateDTO;
import com.lifebalanceapp.exception.NotFoundException;
import com.lifebalanceapp.model.Category;
import com.lifebalanceapp.model.enums.CategoryType;
import com.lifebalanceapp.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    @Transactional
    public Category createCategory(Category category){
        category.setId(null);
        category.setCreatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    public List<Category> getCategoryByType(CategoryType categoryType){
        return categoryRepository.findByType(categoryType);
    }

    public Category updateCategory(Integer id, CategoryUpdateDTO dto){
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));

        category.setName(dto.getName());
        category.setType(dto.getType());

        return categoryRepository.save(category);
    }

    public void deleteCategory(Integer id){
        if(!categoryRepository.existsById(id)){
            throw new NotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

}
