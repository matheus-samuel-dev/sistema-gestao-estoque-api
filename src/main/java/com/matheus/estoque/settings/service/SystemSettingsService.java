package com.matheus.estoque.settings.service;

import com.matheus.estoque.security.AuthenticatedUserService;
import com.matheus.estoque.settings.dto.SystemSettingsDTO;
import com.matheus.estoque.settings.dto.SystemSettingsRequestDTO;
import com.matheus.estoque.settings.entity.SystemSettings;
import com.matheus.estoque.settings.repository.SystemSettingsRepository;
import com.matheus.estoque.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public class SystemSettingsService {
    private final SystemSettingsRepository repository;
    private final AuthenticatedUserService authenticatedUsers;

    public SystemSettingsService(SystemSettingsRepository repository, AuthenticatedUserService authenticatedUsers) {
        this.repository = repository;
        this.authenticatedUsers = authenticatedUsers;
    }

    public SystemSettingsDTO getCurrent() {
        return SystemSettingsDTO.from(getOrCreate());
    }

    public SystemSettingsDTO update(SystemSettingsRequestDTO dto) {
        SystemSettings settings = getOrCreate();
        apply(settings, dto);
        return SystemSettingsDTO.from(repository.save(settings));
    }

    public SystemSettingsDTO finishOnboarding() {
        SystemSettings settings = getOrCreate();
        settings.setOnboardingCompleted(true);
        return SystemSettingsDTO.from(repository.save(settings));
    }

    private SystemSettings getOrCreate() {
        User user = authenticatedUsers.getCurrentUser();
        return repository.findByUser(user)
                .orElseGet(() -> repository.save(SystemSettings.builder().user(user).build()));
    }

    private void apply(SystemSettings settings, SystemSettingsRequestDTO dto) {
        settings.setCompanyName(blankToNull(dto.companyName()));
        settings.setLogoUrl(blankToNull(dto.logoUrl()));
        settings.setCnpj(blankToNull(dto.cnpj()));
        settings.setPhone(blankToNull(dto.phone()));
        settings.setEmail(blankToNull(dto.email()));
        settings.setDefaultMinimumStock(dto.defaultMinimumStock() == null ? 0 : dto.defaultMinimumStock());
        settings.setBusinessType(blankToNull(dto.businessType()) == null ? "Outro" : dto.businessType().trim());
        settings.setBatchControl(Boolean.TRUE.equals(dto.batchControl()));
        settings.setSerialControl(Boolean.TRUE.equals(dto.serialControl()));
        settings.setCurrency(blankToNull(dto.currency()) == null ? "BRL" : dto.currency().trim());
        settings.setTimezone(blankToNull(dto.timezone()) == null ? "America/Sao_Paulo" : dto.timezone().trim());
        settings.setDateFormat(blankToNull(dto.dateFormat()) == null ? "dd/MM/yyyy" : dto.dateFormat().trim());
        if (dto.onboardingCompleted() != null) {
            settings.setOnboardingCompleted(dto.onboardingCompleted());
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
