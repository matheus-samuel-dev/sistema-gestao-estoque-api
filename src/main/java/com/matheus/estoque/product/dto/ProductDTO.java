package com.matheus.estoque.product.dto;

import com.matheus.estoque.category.entity.Category;
import com.matheus.estoque.product.entity.InventoryOrigin;
import com.matheus.estoque.product.entity.Product;
import com.matheus.estoque.supplier.entity.Supplier;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDTO(
        UUID id,
        String name,
        String internalCode,
        String sku,
        String barcode,
        String serialNumber,
        String description,
        String brand,
        String model,
        String physicalLocation,
        BigDecimal price,
        InventoryOrigin origin,
        String notes,
        Integer quantity,
        Integer minimumQuantity,
        Boolean active,
        Category category,
        Supplier supplier,
        String thumbnailUrl
) {
    public static ProductDTO from(Product product, String thumbnailUrl) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getInternalCode(),
                product.getSku(),
                product.getBarcode(),
                product.getSerialNumber(),
                product.getDescription(),
                product.getBrand(),
                product.getModel(),
                product.getPhysicalLocation(),
                product.getPrice(),
                product.getOrigin(),
                product.getNotes(),
                product.getQuantity(),
                product.getMinimumQuantity(),
                product.getActive(),
                product.getCategory(),
                product.getSupplier(),
                thumbnailUrl
        );
    }
}
