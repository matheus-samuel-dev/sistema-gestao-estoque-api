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
            jdbcTemplate.execute("ALTER TABLE products DROP CONSTRAINT IF EXISTS products_origin_check");
            jdbcTemplate.execute("ALTER TABLE stock_movements DROP CONSTRAINT IF EXISTS stock_movements_origin_check");
            jdbcTemplate.update("UPDATE categories SET active = true WHERE active IS NULL");
            jdbcTemplate.update("UPDATE products SET origin = 'OUTRO' WHERE origin IS NULL OR origin = 'OTHER'");
            jdbcTemplate.update("UPDATE products SET origin = 'COMPRA' WHERE origin = 'PURCHASE'");
            jdbcTemplate.update("UPDATE products SET origin = 'TRANSFERENCIA' WHERE origin = 'TRANSFER'");
            jdbcTemplate.update("UPDATE products SET origin = 'AJUSTE' WHERE origin = 'STOCK_ADJUSTMENT'");
            jdbcTemplate.update("UPDATE stock_movements SET origin = 'OUTRO' WHERE origin IS NULL OR origin = 'OTHER'");
            jdbcTemplate.update("UPDATE stock_movements SET origin = 'COMPRA' WHERE origin = 'PURCHASE'");
            jdbcTemplate.update("UPDATE stock_movements SET origin = 'TRANSFERENCIA' WHERE origin = 'TRANSFER'");
            jdbcTemplate.update("UPDATE stock_movements SET origin = 'AJUSTE' WHERE origin = 'STOCK_ADJUSTMENT'");
        };
    }
}
