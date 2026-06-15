package com.matheus.estoque.stockmovement.dto;

import java.util.UUID;

public record StockMovementRequestDTO(
        UUID productId,
        Integer quantity
) {
}
