package com.matheus.estoque.product.repository;

import com.matheus.estoque.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByQuantityLessThanAndActiveTrue(Integer quantity);

    List<Product> findByQuantityAndActiveTrue(Integer quantity);

    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    List<Product> findByCategoryId(UUID categoryId);

    Page<Product> findByActiveTrue(Pageable pageable);

    List<Product> findByActiveTrue();

    List<Product> findTop5ByActiveTrueOrderByIdDesc();

    boolean existsByCategoryId(UUID categoryId);
}
