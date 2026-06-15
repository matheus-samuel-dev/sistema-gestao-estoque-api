package com.matheus.estoque.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateProductDTO(

        @NotBlank(message = "O nome não pode estar vazio")

        @jakarta.validation.constraints.Pattern(
                regexp = "^[A-Za-zÀ-ÿ0-9\\s]+$",
                message = "Nome inválido"
        )
        String name,

        @Positive(message = "O preço deve ser maior que zero")
        BigDecimal price,

        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantity,

        @Positive(message = "O estoque mínimo deve ser maior que zero")
        Integer minimumQuantity,

        @NotNull(message = "A categoria é obrigatória")
        UUID categoryId

) {
}