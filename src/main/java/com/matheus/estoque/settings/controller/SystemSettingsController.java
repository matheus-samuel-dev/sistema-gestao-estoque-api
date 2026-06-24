package com.matheus.estoque.settings.controller;

import com.matheus.estoque.settings.dto.SystemSettingsDTO;
import com.matheus.estoque.settings.dto.SystemSettingsRequestDTO;
import com.matheus.estoque.settings.service.SystemSettingsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
public class SystemSettingsController {
    private final SystemSettingsService service;

    public SystemSettingsController(SystemSettingsService service) {
        this.service = service;
    }

    @GetMapping
    public SystemSettingsDTO getCurrent() {
        return service.getCurrent();
    }

    @PutMapping
    public SystemSettingsDTO update(@Valid @RequestBody SystemSettingsRequestDTO dto) {
        return service.update(dto);
    }

    @PostMapping("/onboarding/finish")
    public SystemSettingsDTO finishOnboarding() {
        return service.finishOnboarding();
    }
}
