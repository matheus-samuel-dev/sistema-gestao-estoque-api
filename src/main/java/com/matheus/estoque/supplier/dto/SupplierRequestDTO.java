package com.matheus.estoque.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequestDTO(
        @NotBlank(message = "Nome do fornecedor é obrigatório")
        @Size(max = 180, message = "Nome deve ter no máximo 180 caracteres")
        String name,
        @Size(max = 120) String document,
        @Size(max = 120) String phone,
        @Email(message = "Informe um e-mail válido")
        @Size(max = 180) String email,
        @Size(max = 2000) String notes,
        Boolean active
) {}
