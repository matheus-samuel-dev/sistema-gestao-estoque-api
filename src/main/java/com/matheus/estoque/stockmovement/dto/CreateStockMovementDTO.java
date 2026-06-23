package com.matheus.estoque.stockmovement.dto;

import com.matheus.estoque.stockmovement.entity.MovementType;
import com.matheus.estoque.product.entity.InventoryOrigin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateStockMovementDTO(
        @NotNull(message = "Produto é obrigatório")
        UUID productId,

        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        Integer quantity,

        @NotNull(message = "Tipo da movimentação é obrigatório")
        MovementType type,

        InventoryOrigin origin,

        String notes
) {
}
