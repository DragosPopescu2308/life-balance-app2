package com.lifebalanceapp.repository;

import com.lifebalanceapp.model.Category;
import com.lifebalanceapp.model.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByType(CategoryType type);
}
