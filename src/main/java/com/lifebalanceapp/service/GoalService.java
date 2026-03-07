package com.lifebalanceapp.service;

import com.lifebalanceapp.dto.GoalResponseDto;
import com.lifebalanceapp.dto.GoalRequestDto;
import com.lifebalanceapp.dto.GoalUpdateRequestDto;
import com.lifebalanceapp.exception.NotFoundException;
import com.lifebalanceapp.model.Goal;
import com.lifebalanceapp.model.User;
import com.lifebalanceapp.repository.GoalAllocationRepository;
import com.lifebalanceapp.repository.GoalRepository;
import com.lifebalanceapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class GoalService {


    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalAllocationRepository goalAllocationRepository;

    public GoalService(
            GoalRepository goalRepository,
            UserRepository userRepository,
            GoalAllocationRepository goalAllocationRepository
    ){
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.goalAllocationRepository = goalAllocationRepository;
    }

    public GoalResponseDto createGoal(Integer userId, GoalRequestDto requestDto){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (requestDto.getAllocationPercent() == null
                || requestDto.getAllocationPercent().compareTo(BigDecimal.ZERO) <= 0
                || requestDto.getAllocationPercent().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Invalid percent");
        }

        BigDecimal existingPercent = goalRepository.findByUser_Id(userId).stream()
                .map(g -> g.getAllocationPercent() == null ? BigDecimal.ZERO : g.getAllocationPercent())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = existingPercent.add(requestDto.getAllocationPercent());

        if (total.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Total percent cannot exceed 100%");
        }

        Goal goal = new Goal();
        goal.setTitle(requestDto.getTitle());
        goal.setUser(user);
        goal.setTargetAmount(requestDto.getTargetAmount());
        goal.setDeadline(requestDto.getDeadline());
        goal.setActive(true);
        goal.setAllocationPercent(requestDto.getAllocationPercent());

        Goal saved = goalRepository.save(goal);
        return toDto(saved);
    }

    public List<GoalResponseDto> getForUser(Integer userId){
        return goalRepository.findByUser_Id(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public void delete(Integer userId, Integer goalId){

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal not found"));

        if (!goal.getUser().getId().equals(userId)) {
            throw new NotFoundException("Goal not found");
        }

        goalAllocationRepository.deleteByGoal_Id(goalId);
        goalRepository.delete(goal);
    }

    public GoalResponseDto update(Integer userId, Integer goalId, GoalUpdateRequestDto dto){

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal not found"));

        if (!goal.getUser().getId().equals(userId)) {
            throw new NotFoundException("Goal not found");
        }

        goal.setTitle(dto.getTitle());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setDeadline(dto.getDeadline());

        if (dto.getActive() != null) {
            goal.setActive(dto.getActive());
        }

        if (dto.getAllocationPercent() == null
                || dto.getAllocationPercent().compareTo(BigDecimal.ZERO) <= 0
                || dto.getAllocationPercent().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Invalid percent");
        }

        BigDecimal existingPercent = goalRepository.findByUser_Id(userId).stream()
                .filter(g -> g.getId() != goalId)
                .map(g -> g.getAllocationPercent() == null ? BigDecimal.ZERO : g.getAllocationPercent())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = existingPercent.add(dto.getAllocationPercent());

        if (total.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Total percent cannot exceed 100%");
        }

        goal.setAllocationPercent(dto.getAllocationPercent());

        Goal saved = goalRepository.save(goal);
        return toDto(saved);
    }

    private BigDecimal sumAmountForGoal(Integer goalId, Integer userId){
        return goalAllocationRepository.sumAllocatedForGoal(goalId, userId);
    }

    private int estimatedMonths(LocalDate deadline){

        long days = ChronoUnit.DAYS.between(LocalDate.now(), deadline);

        if(days <= 0){
            return 0;
        }

        double months = days / 30.44;

        return Math.max(1, (int)Math.ceil(months));
    }

    private GoalResponseDto toDto(Goal goal){

        GoalResponseDto dto = new GoalResponseDto();

        dto.setId(goal.getId());
        dto.setTitle(goal.getTitle());
        dto.setActive(goal.isActive());
        dto.setDeadline(goal.getDeadline());
        dto.setTargetAmount(goal.getTargetAmount());
        dto.setAllocationPercent(goal.getAllocationPercent());

        BigDecimal savedAmount =
                sumAmountForGoal(goal.getId(), goal.getUser().getId());

        dto.setSavedAmount(savedAmount);

        if (goal.getTargetAmount() != null
                && goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal progress = savedAmount
                    .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            dto.setProgress(progress.doubleValue());
        } else {
            dto.setProgress(0.0);
        }

        BigDecimal remaining = goal.getTargetAmount().subtract(savedAmount);
        dto.setRemainingAmount(remaining.max(BigDecimal.ZERO));

        if (goal.getDeadline() == null
                || !goal.getDeadline().isAfter(LocalDate.now())
                || remaining.compareTo(BigDecimal.ZERO) <= 0) {

            dto.setEstimatedMonthly(null);

        } else {

            int months = estimatedMonths(goal.getDeadline());

            dto.setEstimatedMonthly(
                    remaining.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP)
            );
        }

        dto.setRemainingPercent(Math.max(0.0, 100.0 - dto.getProgress()));

        return dto;
    }


}
