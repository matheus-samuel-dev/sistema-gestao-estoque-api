package com.matheus.estoque.product.service;

import com.matheus.estoque.category.entity.Category;
import com.matheus.estoque.category.repository.CategoryRepository;
import com.matheus.estoque.exception.CategoryNotFoundException;
import com.matheus.estoque.product.dto.CreateProductDTO;
import com.matheus.estoque.product.dto.UpdateProductDTO;
import com.matheus.estoque.product.entity.Product;
import com.matheus.estoque.product.repository.ProductRepository;
import com.matheus.estoque.security.AuthenticatedUserService;
import com.matheus.estoque.supplier.entity.Supplier;
import com.matheus.estoque.supplier.repository.SupplierRepository;
import com.matheus.estoque.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import com.matheus.estoque.product.entity.InventoryOrigin;
import jakarta.transaction.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            SupplierRepository supplierRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    public Product create(CreateProductDTO dto) {
        User user = authenticatedUserService.getCurrentUser();
        String internalCode = blankToNull(dto.internalCode()) == null ? generateInternalCode(user) : dto.internalCode();
        validateIdentifiers(user, internalCode, dto.sku(), dto.barcode(), null);

        Category category = categoryRepository
                .findByIdAndUser(dto.categoryId(), user)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Categoria não encontrada"));

        if (!Boolean.TRUE.equals(category.getActive())) {
            throw new RuntimeException("Categoria inativa. Selecione uma categoria ativa.");
        }

        Supplier supplier = resolveSupplier(dto.supplierId(), user);
        Product product = apply(Product.builder().user(user).active(true).build(), dto, category, supplier, internalCode);

        return productRepository.save(product);
    }

    @Transactional
    public List<Product> createBatch(List<CreateProductDTO> items) {
        if (items.size() > 500) {
            throw new RuntimeException("A importação permite no máximo 500 produtos por vez.");
        }
        return items.stream().map(this::create).toList();
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
        String internalCode = blankToNull(dto.internalCode()) == null ? product.getInternalCode() : dto.internalCode();
        if (blankToNull(internalCode) == null) {
            internalCode = generateInternalCode(user);
        }
        validateIdentifiers(user, internalCode, dto.sku(), dto.barcode(), id);

        Category category = categoryRepository
                .findByIdAndUser(dto.categoryId(), user)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Categoria não encontrada"));

        if (!Boolean.TRUE.equals(category.getActive())) {
            throw new RuntimeException("Categoria inativa. Selecione uma categoria ativa.");
        }

        Supplier supplier = resolveSupplier(dto.supplierId(), user);
        apply(product, dto, category, supplier, internalCode);

        return productRepository.save(product);
    }

    private Product apply(Product product, CreateProductDTO dto, Category category, Supplier supplier, String internalCode) {
        product.setName(dto.name().trim());
        product.setInternalCode(blankToNull(internalCode));
        product.setSku(blankToNull(dto.sku()));
        product.setBarcode(blankToNull(dto.barcode()));
        product.setSerialNumber(blankToNull(dto.serialNumber()));
        product.setDescription(blankToNull(dto.description()));
        product.setBrand(blankToNull(dto.brand()));
        product.setModel(blankToNull(dto.model()));
        product.setPhysicalLocation(blankToNull(dto.physicalLocation()));
        product.setPrice(dto.price());
        product.setQuantity(dto.quantity());
        product.setMinimumQuantity(dto.minimumQuantity());
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setOrigin(dto.origin() == null ? InventoryOrigin.OUTRO : dto.origin());
        product.setNotes(blankToNull(dto.notes()));
        return product;
    }

    private Product apply(Product product, UpdateProductDTO dto, Category category, Supplier supplier, String internalCode) {
        product.setName(dto.name().trim());
        product.setInternalCode(blankToNull(internalCode));
        product.setSku(blankToNull(dto.sku()));
        product.setBarcode(blankToNull(dto.barcode()));
        product.setSerialNumber(blankToNull(dto.serialNumber()));
        product.setDescription(blankToNull(dto.description()));
        product.setBrand(blankToNull(dto.brand()));
        product.setModel(blankToNull(dto.model()));
        product.setPhysicalLocation(blankToNull(dto.physicalLocation()));
        product.setPrice(dto.price());
        product.setQuantity(dto.quantity());
        product.setMinimumQuantity(dto.minimumQuantity());
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setOrigin(dto.origin() == null ? InventoryOrigin.OUTRO : dto.origin());
        product.setNotes(blankToNull(dto.notes()));
        return product;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private Supplier resolveSupplier(UUID supplierId, User user) {
        if (supplierId == null) {
            return null;
        }
        Supplier supplier = supplierRepository.findByIdAndUser(supplierId, user)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
        if (!Boolean.TRUE.equals(supplier.getActive())) {
            throw new RuntimeException("Fornecedor inativo. Selecione um fornecedor ativo.");
        }
        return supplier;
    }

    private String generateInternalCode(User user) {
        int next = (int) productRepository.countByUserAndActiveTrue(user) + 1;
        String code;
        do {
            code = "PRD-" + String.format("%03d", next++);
        } while (productRepository.existsByUserAndInternalCodeIgnoreCase(user, code));
        return code;
    }

    private void validateIdentifiers(User user, String internalCode, String sku, String barcode, UUID ignoredId) {
        String code = blankToNull(internalCode);
        String normalizedSku = blankToNull(sku);
        String normalizedBarcode = blankToNull(barcode);
        boolean duplicateCode = code != null && (ignoredId == null
                ? productRepository.existsByUserAndInternalCodeIgnoreCaseAndActiveTrue(user, code)
                : productRepository.existsByUserAndInternalCodeIgnoreCaseAndIdNotAndActiveTrue(user, code, ignoredId));
        boolean duplicateSku = normalizedSku != null && (ignoredId == null
                ? productRepository.existsByUserAndSkuIgnoreCaseAndActiveTrue(user, normalizedSku)
                : productRepository.existsByUserAndSkuIgnoreCaseAndIdNotAndActiveTrue(user, normalizedSku, ignoredId));
        boolean duplicateBarcode = normalizedBarcode != null && (ignoredId == null
                ? productRepository.existsByUserAndBarcodeAndActiveTrue(user, normalizedBarcode)
                : productRepository.existsByUserAndBarcodeAndIdNotAndActiveTrue(user, normalizedBarcode, ignoredId));
        if (duplicateCode) throw new RuntimeException("Código interno já utilizado por outro produto.");
        if (duplicateSku) throw new RuntimeException("SKU já utilizado por outro produto.");
        if (duplicateBarcode) throw new RuntimeException("Código de barras já utilizado por outro produto.");
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

    public List<Product> findBySupplier(UUID supplierId) {
        User user = authenticatedUserService.getCurrentUser();
        return productRepository
                .findByUserAndSupplierIdAndActiveTrue(user, supplierId);
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
