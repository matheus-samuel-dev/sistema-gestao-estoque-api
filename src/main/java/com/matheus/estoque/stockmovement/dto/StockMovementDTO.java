package com.matheus.estoque.stockmovement.dto;

import com.matheus.estoque.product.dto.ProductDTO;
import com.matheus.estoque.product.entity.InventoryOrigin;
import com.matheus.estoque.stockmovement.entity.MovementType;
import com.matheus.estoque.stockmovement.entity.StockMovement;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockMovementDTO(
        UUID id,
        ProductDTO product,
        String productName,
        String productCode,
        String thumbnailUrl,
        Integer quantity,
        MovementType type,
        InventoryOrigin origin,
        String notes,
        String createdBy,
        LocalDateTime createdAt
) {
    public static StockMovementDTO from(StockMovement movement, ProductDTO product) {
        return new StockMovementDTO(
                movement.getId(),
                product,
                product == null ? null : product.name(),
                product == null ? null : product.internalCode(),
                product == null ? null : product.thumbnailUrl(),
                movement.getQuantity(),
                movement.getType(),
                movement.getOrigin(),
                movement.getNotes(),
                movement.getCreatedBy(),
                movement.getCreatedAt()
        );
    }
}
