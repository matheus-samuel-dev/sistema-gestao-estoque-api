package com.matheus.estoque.dashboard.dto;

public record DashboardResponseDTO(
        long totalProducts,
        long totalCategories,
        long totalMovements,
        int totalItemsInStock,
        long lowStockProducts
) {
}