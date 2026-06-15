package com.matheus.estoque.stockmovement.service;

import com.matheus.estoque.product.entity.Product;
import com.matheus.estoque.product.repository.ProductRepository;
import com.matheus.estoque.stockmovement.dto.CreateStockMovementDTO;
import com.matheus.estoque.stockmovement.entity.MovementType;
import com.matheus.estoque.stockmovement.entity.StockMovement;
import com.matheus.estoque.stockmovement.repository.StockMovementRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;

    public StockMovementService(
            StockMovementRepository stockMovementRepository,
            ProductRepository productRepository
    ) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public StockMovement create(CreateStockMovementDTO dto) {
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() ->
                        new RuntimeException("Produto não encontrado"));

        if (dto.quantity() <= 0) {
            throw new RuntimeException(
                    "Quantidade deve ser maior que zero"
            );
        }

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String userEmail =
                authentication != null
                        ? authentication.getName()
                        : null;

        if (userEmail == null || userEmail.isBlank()) {
            throw new RuntimeException(
                    "Usuário não autenticado"
            );
        }

        if (dto.type() == MovementType.EXIT &&
                product.getQuantity() < dto.quantity()) {

            throw new RuntimeException(
                    "Estoque insuficiente para realizar a saída"
            );
        }

        if (dto.type() == MovementType.ENTRY) {
            product.setQuantity(
                    product.getQuantity() + dto.quantity()
            );
        }

        if (dto.type() == MovementType.EXIT) {
            product.setQuantity(
                    product.getQuantity() - dto.quantity()
            );
        }

        productRepository.save(product);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .quantity(dto.quantity())
                .type(dto.type())
                .createdBy(userEmail)
                .build();

        return stockMovementRepository.save(movement);
    }

    public Page<StockMovement> findAll(Pageable pageable) {
        return stockMovementRepository.findAll(pageable);
    }

    public StockMovement findById(UUID id) {
        return stockMovementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Movimentação não encontrada"));
    }

    @Transactional
    public void delete(UUID id) {
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Movimentação não encontrada"));

        Product product = movement.getProduct();

        if (movement.getType() == MovementType.ENTRY &&
                product.getQuantity() < movement.getQuantity()) {

            throw new RuntimeException(
                    "Não é possível excluir a entrada: estoque atual ficaria negativo"
            );
        }

        if (movement.getType() == MovementType.ENTRY) {
            product.setQuantity(
                    product.getQuantity() - movement.getQuantity()
            );
        }

        if (movement.getType() == MovementType.EXIT) {
            product.setQuantity(
                    product.getQuantity() + movement.getQuantity()
            );
        }

        productRepository.save(product);
        stockMovementRepository.delete(movement);
    }

    public List<StockMovement> report(
            LocalDateTime start,
            LocalDateTime end
    ) {
        return stockMovementRepository
                .findByCreatedAtBetween(
                        start,
                        end
                );
    }
}
