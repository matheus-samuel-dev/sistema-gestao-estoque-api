package com.matheus.estoque.settings.dto;

import com.matheus.estoque.settings.entity.SystemSettings;

import java.util.UUID;

public record SystemSettingsDTO(
        UUID id,
        String companyName,
        String logoUrl,
        String cnpj,
        String phone,
        String email,
        Integer defaultMinimumStock,
        String businessType,
        Boolean batchControl,
        Boolean serialControl,
        String currency,
        String timezone,
        String dateFormat,
        Boolean onboardingCompleted
) {
    public static SystemSettingsDTO from(SystemSettings settings) {
        return new SystemSettingsDTO(
                settings.getId(),
                settings.getCompanyName(),
                settings.getLogoUrl(),
                settings.getCnpj(),
                settings.getPhone(),
                settings.getEmail(),
                settings.getDefaultMinimumStock(),
                settings.getBusinessType(),
                settings.getBatchControl(),
                settings.getSerialControl(),
                settings.getCurrency(),
                settings.getTimezone(),
                settings.getDateFormat(),
                settings.getOnboardingCompleted()
        );
    }
}
