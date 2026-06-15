package com.matheus.estoque.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100)
        String name,

        @Size(max = 255)
        String description
) {
}