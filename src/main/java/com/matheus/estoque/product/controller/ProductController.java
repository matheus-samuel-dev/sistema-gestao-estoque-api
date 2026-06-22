package com.matheus.estoque.product.controller;

import com.matheus.estoque.product.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.matheus.estoque.product.dto.CreateProductDTO;
import com.matheus.estoque.product.entity.Product;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;
import com.matheus.estoque.product.dto.UpdateProductDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/products")
@Tag(
        name = "Produtos",
        description = "Operações relacionadas ao gerenciamento de produtos"
)
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar um novo produto")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ResponseEntity<Product> create(
            @Valid @RequestBody CreateProductDTO dto
    ) {

        Product product = service.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(product);
    }

    @Operation(summary = "Listar todos os produtos")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public Page<Product> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/teste-auth")
    public String testeAuth() {
        return "JWT FUNCIONANDO";
    }

    @Operation(summary = "Buscar produto por ID")
    @GetMapping("/{id}")
    public Product findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @Operation(summary = "Atualizar produto")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public Product update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductDTO dto
    ) {
        return service.update(id, dto);
    }

    @Operation(summary = "Excluir produto")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @GetMapping("/low-stock")
    public List<Product> findLowStock() {
        return service.findLowStock();
    }

    @GetMapping("/out-of-stock")
    public List<Product> findOutOfStock() {
        return service.findOutOfStock();
    }

    @Operation(summary = "Buscar produtos pelo nome")
    @GetMapping("/search")
    public List<Product> searchByName(
            @RequestParam String name
    ) {
        return service.searchByName(name);
    }

    @Operation(summary = "Buscar produtos por categoria")
    @GetMapping("/category/{categoryId}")
    public List<Product> findByCategory(
            @PathVariable UUID categoryId
    ) {
        return service.findByCategory(categoryId);
    }

    @GetMapping("/latest")
    public List<Product> findLatestProducts() {
        return service.findLatestProducts();
    }


//    @GetMapping("/teste-auth")
//    public String testeAuth(Authentication authentication) {
//
//        if (authentication == null) {
//            return "AUTH NULL";
//        }
//
//        return authentication.getName()
//                + " | "
//                + authentication.getAuthorities();
//    }
}
