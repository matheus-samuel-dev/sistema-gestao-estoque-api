package com.matheus.estoque.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

import com.matheus.estoque.category.entity.Category;
import com.matheus.estoque.supplier.entity.Supplier;
import com.matheus.estoque.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "internal_code", length = 80)
    private String internalCode;

    @Column(length = 80)
    private String sku;

    @Column(name = "barcode", length = 120)
    private String barcode;

    @Column(name = "serial_number", length = 120)
    private String serialNumber;

    @Column(length = 2000)
    private String description;

    @Column(length = 120)
    private String brand;

    @Column(length = 120)
    private String model;

    @Column(name = "physical_location", length = 180)
    private String physicalLocation;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    @Builder.Default
    private InventoryOrigin origin = InventoryOrigin.OUTRO;

    @Column(length = 2000)
    private String notes;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer minimumQuantity;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
