package com.matheus.estoque.product.dto;

import com.matheus.estoque.product.entity.InventoryOrigin;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 180, message = "Nome deve ter no máximo 180 caracteres")
        String name,
        @Size(max = 80) String internalCode,
        @Size(max = 80) String sku,
        @Size(max = 120) String barcode,
        @Size(max = 120) String serialNumber,
        @Size(max = 2000) String description,
        @Size(max = 120) String brand,
        @Size(max = 120) String model,
        @Size(max = 180) String physicalLocation,
        @NotNull(message = "Valor unitário é obrigatório")
        @DecimalMin(value = "0.0", inclusive = true, message = "Valor inválido")
        BigDecimal price,
        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 0, message = "Quantidade não pode ser negativa")
        Integer quantity,
        @NotNull(message = "Estoque mínimo é obrigatório")
        @Min(value = 0, message = "Estoque mínimo não pode ser negativo")
        Integer minimumQuantity,
        @NotNull(message = "Categoria é obrigatória") UUID categoryId,
        UUID supplierId,
        InventoryOrigin origin,
        @Size(max = 2000) String notes
) {}
