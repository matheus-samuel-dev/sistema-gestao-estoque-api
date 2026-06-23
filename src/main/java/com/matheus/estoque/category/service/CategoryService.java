package com.matheus.estoque.category.service;

import com.matheus.estoque.category.dto.CreateCategoryDTO;
import com.matheus.estoque.category.dto.UpdateCategoryDTO;
import com.matheus.estoque.category.entity.Category;
import com.matheus.estoque.category.repository.CategoryRepository;
import com.matheus.estoque.exception.CategoryNotFoundException;
import com.matheus.estoque.product.repository.ProductRepository;
import com.matheus.estoque.security.AuthenticatedUserService;
import com.matheus.estoque.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public CategoryService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    public Category create(CreateCategoryDTO dto) {
        User user = authenticatedUserService.getCurrentUser();

        Category category = Category.builder()
                .name(dto.name())
                .description(dto.description())
                .user(user)
                .active(true)
                .build();

        return categoryRepository.save(category);
    }

    public Category setActive(UUID id, boolean active) {
        User user = authenticatedUserService.getCurrentUser();
        Category category = findOwnedCategory(id, user);
        category.setActive(active);
        return categoryRepository.save(category);
    }

    public List<Category> findAll() {
        User user = authenticatedUserService.getCurrentUser();
        return categoryRepository.findByUserOrderByNameAsc(user);
    }

    public Category findById(UUID id) {
        User user = authenticatedUserService.getCurrentUser();
        return findOwnedCategory(id, user);
    }

    public Category update(UUID id, UpdateCategoryDTO dto) {
        User user = authenticatedUserService.getCurrentUser();
        Category category = findOwnedCategory(id, user);

        category.setName(dto.name());
        category.setDescription(dto.description());

        return categoryRepository.save(category);
    }

    public void delete(UUID id) {
        User user = authenticatedUserService.getCurrentUser();
        Category category = findOwnedCategory(id, user);

        boolean hasProducts =
                productRepository
                        .existsByCategoryIdAndUser(id, user);

        if (hasProducts) {
            throw new RuntimeException(
                    "Não é possível excluir uma categoria que possui produtos cadastrados."
            );
        }

        categoryRepository.delete(category);
    }

    private Category findOwnedCategory(
            UUID id,
            User user
    ) {
        return categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Categoria não encontrada"));
    }
}
