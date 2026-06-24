package com.matheus.estoque.settings.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record SystemSettingsRequestDTO(
        @Size(max = 180) String companyName,
        @Size(max = 1000) String logoUrl,
        @Size(max = 40) String cnpj,
        @Size(max = 80) String phone,
        @Email(message = "Informe um e-mail válido")
        @Size(max = 180) String email,
        @Min(value = 0, message = "Estoque mínimo padrão não pode ser negativo")
        Integer defaultMinimumStock,
        @Size(max = 80) String businessType,
        Boolean batchControl,
        Boolean serialControl,
        @Size(max = 12) String currency,
        @Size(max = 80) String timezone,
        @Size(max = 40) String dateFormat,
        Boolean onboardingCompleted
) {}
