package com.lifebalanceapp.service;

import com.lifebalanceapp.dto.CategoryCreateRequestDto;
import com.lifebalanceapp.dto.CategoryResponseDto;
import com.lifebalanceapp.dto.CategoryUpdateDTO;
import com.lifebalanceapp.exception.NotFoundException;
import com.lifebalanceapp.model.Category;
import com.lifebalanceapp.model.User;
import com.lifebalanceapp.model.enums.CategoryType;
import com.lifebalanceapp.repository.CategoryRepository;
import com.lifebalanceapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<CategoryResponseDto> getForUser(Integer userId, CategoryType type) {
        List<Category> cats = (type == null)
                ? categoryRepository.findByUser_Id(userId)
                : categoryRepository.findByUser_IdAndType(userId, type);

        return cats.stream().map(this::toDto).toList();
    }

    @Transactional
    public CategoryResponseDto create(Integer userId, CategoryCreateRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Category category = new Category();
        category.setId(null);
        category.setUser(user);
        category.setName(dto.getName());
        category.setType(dto.getType());
        category.setCreatedAt(LocalDateTime.now());

        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponseDto update(Integer userId, Integer id, CategoryUpdateDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (!category.getUser().getId().equals(userId)) {
            throw new NotFoundException("Category not found");
        }

        category.setName(dto.getName());
        category.setType(dto.getType());

        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Integer userId, Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (!category.getUser().getId().equals(userId)) {
            throw new NotFoundException("Category not found");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponseDto toDto(Category c) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setType(c.getType());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}
