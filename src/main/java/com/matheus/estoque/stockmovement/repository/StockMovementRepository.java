package com.matheus.estoque.stockmovement.repository;

import com.matheus.estoque.stockmovement.entity.StockMovement;
import com.matheus.estoque.stockmovement.entity.MovementType;
import com.matheus.estoque.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockMovementRepository
        extends JpaRepository<StockMovement, UUID> {

    Optional<StockMovement> findByIdAndUser(UUID id, User user);

    Page<StockMovement> findByUser(
            User user,
            Pageable pageable
    );

    List<StockMovement> findByUser(User user);

    List<StockMovement> findByUserAndCreatedAtBetween(
            User user,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByUser(User user);

    long countByUserAndTypeAndCreatedAtBetween(
            User user,
            MovementType type,
            LocalDateTime start,
            LocalDateTime end
    );
}
