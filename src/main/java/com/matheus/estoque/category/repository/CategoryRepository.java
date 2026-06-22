package com.matheus.estoque.category.repository;

import com.matheus.estoque.category.entity.Category;
import com.matheus.estoque.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByIdAndUser(UUID id, User user);

    List<Category> findByUserOrderByNameAsc(User user);

    long countByUser(User user);
}
