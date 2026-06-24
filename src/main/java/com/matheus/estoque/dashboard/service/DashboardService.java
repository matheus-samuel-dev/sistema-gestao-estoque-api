package com.matheus.estoque.dashboard.service;

import com.matheus.estoque.category.repository.CategoryRepository;
import com.matheus.estoque.dashboard.dto.CategorySummaryDTO;
import com.matheus.estoque.dashboard.dto.DashboardResponseDTO;
import com.matheus.estoque.dashboard.dto.MovementSummaryDTO;
import com.matheus.estoque.product.entity.Product;
import com.matheus.estoque.product.repository.ProductRepository;
import com.matheus.estoque.security.AuthenticatedUserService;
import com.matheus.estoque.stockmovement.entity.MovementType;
import com.matheus.estoque.stockmovement.repository.StockMovementRepository;
import com.matheus.estoque.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class DashboardService {

    private static final int LOW_STOCK_LIMIT = 10;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public DashboardService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            StockMovementRepository stockMovementRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    public DashboardResponseDTO getDashboard() {
        User user = authenticatedUserService.getCurrentUser();
        List<Product> products =
                productRepository.findByUserAndActiveTrue(user);

        long totalProducts =
                productRepository.countByUserAndActiveTrue(user);
        long totalCategories =
                categoryRepository.countByUserAndActiveTrue(user);
        long totalMovements =
                stockMovementRepository.countByUser(user);

        int totalItemsInStock = products.stream()
                .mapToInt(Product::getQuantity)
                .sum();

        long lowStockProducts = products.stream()
                .filter(product ->
                        product.getQuantity() <= product.getMinimumQuantity())
                .count();

        long outOfStockProducts = products.stream()
                .filter(product -> product.getQuantity() == 0)
                .count();

        BigDecimal totalStockValue = products.stream()
                .map(product -> product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        YearMonth currentMonth = YearMonth.now();
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.plusMonths(1).atDay(1).atStartOfDay();
        long entriesThisMonth = stockMovementRepository.countByUserAndTypeAndCreatedAtBetween(
                user, MovementType.ENTRY, monthStart, monthEnd
        );
        long exitsThisMonth = stockMovementRepository.countByUserAndTypeAndCreatedAtBetween(
                user, MovementType.EXIT, monthStart, monthEnd
        );

        return new DashboardResponseDTO(
                totalProducts,
                totalCategories,
                totalMovements,
                totalItemsInStock,
                lowStockProducts,
                outOfStockProducts,
                totalStockValue,
                entriesThisMonth,
                exitsThisMonth
        );
    }

    public List<CategorySummaryDTO> getProductsByCategory() {
        User user = authenticatedUserService.getCurrentUser();

        return productRepository.findByUserAndActiveTrue(user)
                .stream()
                .filter(product -> product.getCategory() != null)
                .collect(
                        Collectors.groupingBy(
                                product -> product.getCategory().getName(),
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
        User user = authenticatedUserService.getCurrentUser();

        return stockMovementRepository.findByUser(user)
                .stream()
                .filter(movement -> movement.getCreatedAt() != null)
                .collect(
                        Collectors.groupingBy(
                                movement -> movement
                                        .getCreatedAt()
                                        .getMonth()
                                        .getDisplayName(
                                                TextStyle.SHORT,
                                                new Locale("pt", "BR")
                                        ),
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
