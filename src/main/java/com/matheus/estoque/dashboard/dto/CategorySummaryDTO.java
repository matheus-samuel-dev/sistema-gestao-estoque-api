package com.matheus.estoque.dashboard.dto;

public record CategorySummaryDTO(
        String category,
        long products
) {
}