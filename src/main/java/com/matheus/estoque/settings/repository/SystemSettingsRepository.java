package com.matheus.estoque.settings.repository;

import com.matheus.estoque.settings.entity.SystemSettings;
import com.matheus.estoque.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SystemSettingsRepository extends JpaRepository<SystemSettings, UUID> {
    Optional<SystemSettings> findByUser(User user);
}
