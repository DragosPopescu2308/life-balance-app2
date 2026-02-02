package com.lifebalanceapp.service;

import com.lifebalanceapp.dto.IncomeCreateRequestDto;
import com.lifebalanceapp.dto.IncomeResponseDto;
import com.lifebalanceapp.exception.NotFoundException;
import com.lifebalanceapp.model.Category;
import com.lifebalanceapp.model.Income;
import com.lifebalanceapp.model.User;
import com.lifebalanceapp.model.enums.CategoryType;
import com.lifebalanceapp.repository.CategoryRepository;
import com.lifebalanceapp.repository.IncomeRepository;
import com.lifebalanceapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final IncomeRepository incomeRepository;

    public IncomeService(CategoryRepository categoryRepository,
                            UserRepository userRepository,
                            IncomeRepository incomeRepository){
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.incomeRepository = incomeRepository;
    }

    public List<IncomeResponseDto> getIncomesByUser(Integer userId){
        return incomeRepository.findByUser_Id(userId)
                .stream().map(this::toDtoIncome).toList();
    }

    @Transactional
    public IncomeResponseDto createIncome(IncomeCreateRequestDto request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        Income income = new Income();
        income.setUser(user);

        income.setAmount(request.getAmount());
        income.setTitle(request.getTitle());
        income.setDateReceived(request.getDateReceived());
        income.setNotes(request.getNotes());


        if(request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new NotFoundException("Category not found"));
            if (category.getType() != CategoryType.INCOME) {
                throw new IllegalArgumentException("Category must be INCOME for incomes");
            }
            income.setCategory(category);
        }else {
            income.setCategory(null);
        }

        income.setCreatedAt(LocalDateTime.now());

        Income saved = incomeRepository.save(income);
        return toDtoIncome(saved);


    }

    public IncomeResponseDto toDtoIncome(Income income){
        IncomeResponseDto dto = new IncomeResponseDto();

        dto.setId(income.getId());
        dto.setTitle(income.getTitle());
        dto.setAmount(income.getAmount());
        dto.setDateReceived(income.getDateReceived());
        dto.setNotes(income.getNotes());
        dto.setCreatedAt(income.getCreatedAt());

        dto.setUserId(income.getUser().getId());
        dto.setCategoryId(income.getCategory() != null ? income.getCategory().getId() : null);

        return dto;
    }
}
