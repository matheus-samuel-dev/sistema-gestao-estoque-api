package com.matheus.estoque.product.controller;

import com.matheus.estoque.product.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.matheus.estoque.product.dto.CreateProductDTO;
import com.matheus.estoque.product.dto.ProductDTO;
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

    @Operation(summary = "Importar produtos validados em lote")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/import")
    public ResponseEntity<List<Product>> importProducts(
            @RequestBody List<@Valid CreateProductDTO> items
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBatch(items));
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
    public Page<ProductDTO> findAll(Pageable pageable) {
        return service.findAllDto(pageable);
    }

    @GetMapping("/teste-auth")
    public String testeAuth() {
        return "JWT FUNCIONANDO";
    }

    @Operation(summary = "Buscar produto por ID")
    @GetMapping("/{id}")
    public ProductDTO findById(@PathVariable UUID id) {
        return service.findByIdDto(id);
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
    public List<ProductDTO> findLowStock() {
        return service.findLowStockDto();
    }

    @GetMapping("/out-of-stock")
    public List<ProductDTO> findOutOfStock() {
        return service.findOutOfStockDto();
    }

    @Operation(summary = "Buscar produtos pelo nome")
    @GetMapping("/search")
    public List<ProductDTO> searchByName(
            @RequestParam String name
    ) {
        return service.searchByNameDto(name);
    }

    @Operation(summary = "Buscar produtos por categoria")
    @GetMapping("/category/{categoryId}")
    public List<ProductDTO> findByCategory(
            @PathVariable UUID categoryId
    ) {
        return service.findByCategoryDto(categoryId);
    }

    @Operation(summary = "Buscar produtos por fornecedor")
    @GetMapping("/supplier/{supplierId}")
    public List<ProductDTO> findBySupplier(
            @PathVariable UUID supplierId
    ) {
        return service.findBySupplierDto(supplierId);
    }

    @GetMapping("/latest")
    public List<ProductDTO> findLatestProducts() {
        return service.findLatestProductsDto();
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
