package com.lifebalanceapp.controller;

import com.lifebalanceapp.dto.DashboardResponseDto;
import com.lifebalanceapp.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    private Integer requireUserId(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return userId;
    }

    @GetMapping
    public ResponseEntity<DashboardResponseDto> monthly(
            HttpSession session,
            @RequestParam(required = false) String month
    ) {
        Integer userId = requireUserId(session);
        return ResponseEntity.ok(dashboardService.getMonthlyDashboard(userId, month));
    }
}
