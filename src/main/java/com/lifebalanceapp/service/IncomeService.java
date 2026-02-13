package com.lifebalanceapp.service;

import com.lifebalanceapp.dto.IncomeCreateRequestDto;
import com.lifebalanceapp.dto.IncomeResponseDto;
import com.lifebalanceapp.dto.IncomeUpdateRequestDto;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final IncomeRepository incomeRepository;
    private final SavingService savingService;


    public IncomeService(CategoryRepository categoryRepository,
                         UserRepository userRepository,
                         IncomeRepository incomeRepository,
                         SavingService savingService) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.incomeRepository = incomeRepository;
            this.savingService = savingService;
    }

    public List<IncomeResponseDto> getIncomesByUser(Integer userId) {
        return incomeRepository.findByUser_Id(userId).stream().map(this::toDtoIncome).toList();
    }

    public List<IncomeResponseDto> getIncomesByUserAndMonth(Integer userId, String month) {
        if (month == null || month.isBlank()) {
            return getIncomesByUser(userId);
        }

        YearMonth ym = YearMonth.parse(month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.plusMonths(1).atDay(1);

        return incomeRepository
                .findByUser_IdAndDateReceivedBetween(userId, start, end)
                .stream()
                .map(this::toDtoIncome)
                .toList();
    }

    public IncomeResponseDto getById(Integer id, Integer userId) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Income not found"));

        if (!income.getUser().getId().equals(userId)) {
            throw new NotFoundException("Income not found");
        }

        return toDtoIncome(income);
    }

    @Transactional
    public IncomeResponseDto create(Integer userId, IncomeCreateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Income income = new Income();
        income.setUser(user);
        income.setAmount(request.getAmount());
        income.setTitle(request.getTitle());
        income.setDateReceived(request.getDateReceived());
        income.setNotes(request.getNotes());
        income.setCreatedAt(LocalDateTime.now());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));

            if (category.getType() != CategoryType.INCOME) {
                throw new IllegalArgumentException("Category must be INCOME");
            }


            if (!category.getUser().getId().equals(userId)) {
                throw new NotFoundException("Category not found");
            }

            income.setCategory(category);
        }

        Income saved = incomeRepository.save(income);
        savingService.processSavings(saved);
        return toDtoIncome(saved);
    }

    @Transactional
    public IncomeResponseDto update(Integer id, Integer userId, IncomeUpdateRequestDto request) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Income not found"));

        if (!income.getUser().getId().equals(userId)) {
            throw new NotFoundException("Income not found");
        }

        income.setTitle(request.getTitle());
        income.setAmount(request.getAmount());
        income.setDateReceived(request.getDateReceived());
        income.setNotes(request.getNotes());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));

            if (category.getType() != CategoryType.INCOME) {
                throw new IllegalArgumentException("Category must be INCOME");
            }


            if (!category.getUser().getId().equals(userId)) {
                throw new NotFoundException("Category not found");
            }

            income.setCategory(category);
        }

        Income saved = incomeRepository.save(income);
        return toDtoIncome(saved);
    }

    @Transactional
    public void delete(Integer id, Integer userId) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Income not found"));

        if (!income.getUser().getId().equals(userId)) {
            throw new NotFoundException("Income not found");
        }

        incomeRepository.delete(income);
    }

    private IncomeResponseDto toDtoIncome(Income income) {
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
