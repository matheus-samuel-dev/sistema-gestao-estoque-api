package com.matheus.estoque.product.service;

import com.matheus.estoque.category.entity.Category;
import com.matheus.estoque.category.repository.CategoryRepository;
import com.matheus.estoque.exception.CategoryNotFoundException;
import com.matheus.estoque.product.dto.CreateProductDTO;
import com.matheus.estoque.product.dto.UpdateProductDTO;
import com.matheus.estoque.product.entity.Product;
import com.matheus.estoque.product.repository.ProductRepository;
import com.matheus.estoque.security.AuthenticatedUserService;
import com.matheus.estoque.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    public Product create(CreateProductDTO dto) {
        User user = authenticatedUserService.getCurrentUser();

        Category category = categoryRepository
                .findByIdAndUser(dto.categoryId(), user)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Categoria não encontrada"));

        Product product = Product.builder()
                .name(dto.name())
                .price(dto.price())
                .quantity(dto.quantity())
                .minimumQuantity(dto.minimumQuantity())
                .category(category)
                .user(user)
                .active(true)
                .build();

        return productRepository.save(product);
    }

    public Page<Product> findAll(Pageable pageable) {
        User user = authenticatedUserService.getCurrentUser();
        return productRepository.findByUserAndActiveTrue(user, pageable);
    }

    public Product findById(UUID id) {
        User user = authenticatedUserService.getCurrentUser();
        return findOwnedProduct(id, user);
    }

    public Product update(UUID id, UpdateProductDTO dto) {
        User user = authenticatedUserService.getCurrentUser();
        Product product = findOwnedProduct(id, user);

        Category category = categoryRepository
                .findByIdAndUser(dto.categoryId(), user)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Categoria não encontrada"));

        product.setName(dto.name());
        product.setPrice(dto.price());
        product.setQuantity(dto.quantity());
        product.setMinimumQuantity(dto.minimumQuantity());
        product.setCategory(category);

        return productRepository.save(product);
    }

    public void delete(UUID id) {
        User user = authenticatedUserService.getCurrentUser();
        Product product = findOwnedProduct(id, user);

        product.setActive(false);
        productRepository.save(product);
    }

    public List<Product> findLowStock() {
        User user = authenticatedUserService.getCurrentUser();
        return productRepository
                .findByUserAndQuantityLessThanAndActiveTrue(user, 10);
    }

    public List<Product> findOutOfStock() {
        User user = authenticatedUserService.getCurrentUser();
        return productRepository
                .findByUserAndQuantityAndActiveTrue(user, 0);
    }

    public List<Product> searchByName(String name) {
        User user = authenticatedUserService.getCurrentUser();
        return productRepository
                .findByUserAndNameContainingIgnoreCaseAndActiveTrue(
                        user,
                        name
                );
    }

    public List<Product> findByCategory(UUID categoryId) {
        User user = authenticatedUserService.getCurrentUser();
        return productRepository
                .findByUserAndCategoryIdAndActiveTrue(user, categoryId);
    }

    public List<Product> findLatestProducts() {
        User user = authenticatedUserService.getCurrentUser();
        return productRepository
                .findTop5ByUserAndActiveTrueOrderByIdDesc(user);
    }

    private Product findOwnedProduct(
            UUID id,
            User user
    ) {
        return productRepository.findByIdAndUserAndActiveTrue(id, user)
                .orElseThrow(() ->
                        new RuntimeException("Produto não encontrado"));
    }
}
