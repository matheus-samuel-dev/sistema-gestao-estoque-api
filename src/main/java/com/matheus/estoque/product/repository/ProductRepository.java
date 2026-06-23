package com.matheus.estoque.product.repository;

import com.matheus.estoque.product.entity.Product;
import com.matheus.estoque.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByIdAndUserAndActiveTrue(UUID id, User user);

    Page<Product> findByUserAndActiveTrue(
            User user,
            Pageable pageable
    );

    List<Product> findByUserAndActiveTrue(User user);

    List<Product> findByUserAndQuantityLessThanAndActiveTrue(
            User user,
            Integer quantity
    );

    List<Product> findByUserAndQuantityAndActiveTrue(
            User user,
            Integer quantity
    );

    List<Product> findByUserAndNameContainingIgnoreCaseAndActiveTrue(
            User user,
            String name
    );

    List<Product> findByUserAndCategoryIdAndActiveTrue(
            User user,
            UUID categoryId
    );

    List<Product> findTop5ByUserAndActiveTrueOrderByIdDesc(User user);

    boolean existsByCategoryIdAndUser(
            UUID categoryId,
            User user
    );

    long countByUserAndActiveTrue(User user);

    boolean existsByUserAndInternalCodeIgnoreCaseAndActiveTrue(User user, String internalCode);
    boolean existsByUserAndSkuIgnoreCaseAndActiveTrue(User user, String sku);
    boolean existsByUserAndBarcodeAndActiveTrue(User user, String barcode);
    boolean existsByUserAndInternalCodeIgnoreCaseAndIdNotAndActiveTrue(User user, String internalCode, UUID id);
    boolean existsByUserAndSkuIgnoreCaseAndIdNotAndActiveTrue(User user, String sku, UUID id);
    boolean existsByUserAndBarcodeAndIdNotAndActiveTrue(User user, String barcode, UUID id);
}
