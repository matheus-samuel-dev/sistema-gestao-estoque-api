package com.matheus.estoque.user.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginDTO(
        @NotBlank(message = "Token Google é obrigatório")
        String token
) {
}
