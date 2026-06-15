package com.matheus.estoque.dashboard.controller;

import com.matheus.estoque.dashboard.dto.CategorySummaryDTO;
import com.matheus.estoque.dashboard.dto.DashboardResponseDTO;
import com.matheus.estoque.dashboard.dto.MovementSummaryDTO;
import com.matheus.estoque.dashboard.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(
        name = "Dashboard",
        description = "Indicadores e métricas do sistema"
)
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @Operation(summary = "Visualizar indicadores do sistema")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/dashboard")
    public DashboardResponseDTO dashboard() {
        return service.getDashboard();
    }

    @GetMapping("/dashboard/categories")
    public List<CategorySummaryDTO>
    categories() {

        return service.getProductsByCategory();
    }

    @GetMapping("/dashboard/movements")
    public List<MovementSummaryDTO> movements() {
        return service.getMovementsByMonth();
    }
}