package com.matheus.estoque.category.service;

import com.matheus.estoque.category.dto.CreateCategoryDTO;
import com.matheus.estoque.category.dto.UpdateCategoryDTO;
import com.matheus.estoque.category.entity.Category;
import com.matheus.estoque.category.repository.CategoryRepository;
import com.matheus.estoque.exception.CategoryNotFoundException;
import com.matheus.estoque.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository, ProductRepository productRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
    }

    private final ProductRepository productRepository;

    public Category create(CreateCategoryDTO dto) {

        Category category = Category.builder()
                .name(dto.name())
                .description(dto.description())
                .build();

        return repository.save(category);
    }

    public List<Category> findAll() {
        return repository.findAll();
    }

    public Category findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Categoria não encontrada")
                );
    }

    public Category update(UUID id, UpdateCategoryDTO dto) {

        Category category = repository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Categoria não encontrada")
                );

        category.setName(dto.name());
        category.setDescription(dto.description());

        return repository.save(category);
    }

    public void delete(UUID id) {

        Category category = repository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Categoria não encontrada"
                        )
                );

        boolean hasProducts =
                productRepository. existsByCategoryId(id);

        if (hasProducts) {
            throw new RuntimeException(
                    "Não é possível excluir uma categoria que possui produtos cadastrados."
            );
        }

        repository.delete(category);
    }
}