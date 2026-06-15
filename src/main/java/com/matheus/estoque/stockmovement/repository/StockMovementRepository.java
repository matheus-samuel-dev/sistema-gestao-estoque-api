package com.matheus.estoque.stockmovement.repository;

import com.matheus.estoque.stockmovement.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository
        extends JpaRepository<StockMovement, UUID> {

    List<StockMovement>
    findByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

}