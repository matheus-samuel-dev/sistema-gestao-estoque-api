package com.matheus.estoque.supplier.repository;

import com.matheus.estoque.supplier.entity.Supplier;
import com.matheus.estoque.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    List<Supplier> findByUserOrderByNameAsc(User user);
    List<Supplier> findByUserAndActiveTrueOrderByNameAsc(User user);
    Optional<Supplier> findByIdAndUser(UUID id, User user);
    boolean existsByUserAndNameIgnoreCase(User user, String name);
}
