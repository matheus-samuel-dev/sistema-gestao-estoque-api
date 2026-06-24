package com.matheus.estoque.dashboard.dto;

import java.math.BigDecimal;

public record DashboardResponseDTO(
        long totalProducts,
        long totalCategories,
        long totalMovements,
        int totalItemsInStock,
        long lowStockProducts,
        long outOfStockProducts,
        BigDecimal totalStockValue,
        long entriesThisMonth,
        long exitsThisMonth
) {}
