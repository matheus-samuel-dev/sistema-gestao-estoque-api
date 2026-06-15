package com.matheus.estoque.product.service;

import com.matheus.estoque.category.repository.CategoryRepository;
import com.matheus.estoque.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import com.matheus.estoque.category.entity.Category;
import com.matheus.estoque.exception.CategoryNotFoundException;
import com.matheus.estoque.product.dto.CreateProductDTO;
import com.matheus.estoque.product.entity.Product;

import com.matheus.estoque.product.dto.UpdateProductDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product create(CreateProductDTO dto) {

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException("Categoria não encontrada"));

        Product product = Product.builder()
                .name(dto.name())
                .price(dto.price())
                .quantity(dto.quantity())
                .minimumQuantity(dto.minimumQuantity())
                .category(category)
                .active(true)
                .build();

        return productRepository.save(product);
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    public Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Produto não encontrado"));
    }

    public Product update(UUID id, UpdateProductDTO dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Produto não encontrado"));

        Category category = categoryRepository.findById(dto.categoryId())
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

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Produto não encontrado"));

        product.setActive(false);

        productRepository.save(product);
    }

    public List<Product> findLowStock() {
        return productRepository.findByQuantityLessThanAndActiveTrue(10);
    }

    public List<Product> findOutOfStock() {
        return productRepository.findByQuantityAndActiveTrue(0);
    }

    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
    }

    public List<Product> findByCategory(UUID categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> findLatestProducts() {
        return productRepository.findTop5ByActiveTrueOrderByIdDesc();
    }


}
