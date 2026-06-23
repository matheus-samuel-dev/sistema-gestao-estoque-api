package com.matheus.estoque.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class LegacyDataMigrationConfig {
    @Bean
    CommandLineRunner normalizeLegacyInventoryData(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.update("UPDATE categories SET active = true WHERE active IS NULL");
            jdbcTemplate.update("UPDATE products SET origin = 'OTHER' WHERE origin IS NULL");
            jdbcTemplate.update("UPDATE stock_movements SET origin = 'OTHER' WHERE origin IS NULL");
        };
    }
}
