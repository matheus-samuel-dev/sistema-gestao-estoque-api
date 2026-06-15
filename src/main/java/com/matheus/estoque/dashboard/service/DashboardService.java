package com.matheus.estoque.dashboard.service;

import com.matheus.estoque.category.repository.CategoryRepository;
import com.matheus.estoque.dashboard.dto.CategorySummaryDTO;
import com.matheus.estoque.dashboard.dto.DashboardResponseDTO;
import com.matheus.estoque.product.entity.Product;
import com.matheus.estoque.product.repository.ProductRepository;
import com.matheus.estoque.stockmovement.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.matheus.estoque.dashboard.dto.MovementSummaryDTO;

import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class DashboardService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private static final int LOW_STOCK_LIMIT = 10;

    public DashboardService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            StockMovementRepository stockMovementRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public DashboardResponseDTO getDashboard() {

        long totalProducts = productRepository.count();

        long totalCategories = categoryRepository.count();

        long totalMovements = stockMovementRepository.count();

        List<Product> products =
                productRepository.findByActiveTrue();

        int totalItemsInStock = products.stream()
                .mapToInt(Product::getQuantity)
                .sum();

        long lowStockProducts = products.stream()
                .filter(product ->
                        product.getQuantity() < LOW_STOCK_LIMIT)
                .count();

        return new DashboardResponseDTO(
                totalProducts,
                totalCategories,
                totalMovements,
                totalItemsInStock,
                lowStockProducts
        );
    }

    public List<CategorySummaryDTO>
    getProductsByCategory() {

        return productRepository.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                product ->
                                        product
                                                .getCategory()
                                                .getName(),
                                Collectors.counting()
                        )
                )
                .entrySet()
                .stream()
                .map(entry ->
                        new CategorySummaryDTO(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();
    }

    public List<MovementSummaryDTO> getMovementsByMonth() {

        return stockMovementRepository.findAll()
                .stream()
                .filter(movement -> movement.getCreatedAt() != null)
                .collect(
                        Collectors.groupingBy(
                                movement ->
                                        movement.getCreatedAt()
                                                .getMonth()
                                                .getDisplayName(
                                                        TextStyle.SHORT,
                                                        new Locale("pt", "BR")
                                                )
                                ,
                                Collectors.counting()
                        )
                )
                .entrySet()
                .stream()
                .map(entry ->
                        new MovementSummaryDTO(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();
    }
}