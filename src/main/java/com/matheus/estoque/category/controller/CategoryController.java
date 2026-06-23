package com.matheus.estoque.category.controller;

import com.matheus.estoque.category.service.CategoryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.matheus.estoque.category.dto.CreateCategoryDTO;
import com.matheus.estoque.category.dto.UpdateCategoryDTO;
import com.matheus.estoque.category.entity.Category;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
@Tag(
        name = "Categorias",
        description = "Operações relacionadas às categorias"
)
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @Operation(summary = "Ativar ou desativar categoria")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Category> setActive(
            @PathVariable UUID id,
            @RequestParam boolean active
    ) {
        return ResponseEntity.ok(service.setActive(id, active));
    }

    @Operation(summary = "Cadastrar categoria")
    @PostMapping
    public ResponseEntity<Category> create(
            @Valid @RequestBody CreateCategoryDTO dto
    ) {

        Category category = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(category);
    }

    @Operation(summary = "Listar categorias")
    @GetMapping
    public ResponseEntity<List<Category>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Buscar categoria por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Category> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Atualizar categoria")
    @PutMapping("/{id}")
    public ResponseEntity<Category> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryDTO dto
    ) {

        return ResponseEntity.ok(
                service.update(id, dto)
        );
    }

    @Operation(summary = "Excluir categoria")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }



}
