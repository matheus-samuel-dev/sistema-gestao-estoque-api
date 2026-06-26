package com.matheus.estoque.stockmovement.service;

import com.matheus.estoque.attachment.service.AttachmentService;
import com.matheus.estoque.product.entity.Product;
import com.matheus.estoque.product.entity.InventoryOrigin;
import com.matheus.estoque.product.repository.ProductRepository;
import com.matheus.estoque.security.AuthenticatedUserService;
import com.matheus.estoque.stockmovement.dto.CreateStockMovementDTO;
import com.matheus.estoque.stockmovement.dto.StockMovementDTO;
import com.matheus.estoque.stockmovement.entity.MovementType;
import com.matheus.estoque.stockmovement.entity.StockMovement;
import com.matheus.estoque.stockmovement.repository.StockMovementRepository;
import com.matheus.estoque.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final AttachmentService attachmentService;

    public StockMovementService(
            StockMovementRepository stockMovementRepository,
            ProductRepository productRepository,
            AuthenticatedUserService authenticatedUserService,
            AttachmentService attachmentService
    ) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.attachmentService = attachmentService;
    }

    @Transactional
    public StockMovement create(CreateStockMovementDTO dto) {
        User user = authenticatedUserService.getCurrentUser();

        Product product = productRepository
                .findByIdAndUserAndActiveTrue(dto.productId(), user)
                .orElseThrow(() ->
                        new RuntimeException("Produto não encontrado"));

        if (dto.quantity() <= 0) {
            throw new RuntimeException(
                    "Quantidade deve ser maior que zero"
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
                .origin(dto.origin() == null ? InventoryOrigin.OUTRO : dto.origin())
                .notes(dto.notes() == null || dto.notes().isBlank() ? null : dto.notes().trim())
                .createdBy(user.getEmail())
                .user(user)
                .build();

        return stockMovementRepository.save(movement);
    }

    public Page<StockMovement> findAll(Pageable pageable) {
        User user = authenticatedUserService.getCurrentUser();
        return stockMovementRepository.findByUser(user, pageable);
    }

    public Page<StockMovementDTO> findAllDto(Pageable pageable) {
        User user = authenticatedUserService.getCurrentUser();
        return stockMovementRepository.findByUser(user, pageable)
                .map(movement -> toDto(movement, user));
    }

    public StockMovement findById(UUID id) {
        User user = authenticatedUserService.getCurrentUser();
        return findOwnedMovement(id, user);
    }

    public StockMovementDTO findByIdDto(UUID id) {
        User user = authenticatedUserService.getCurrentUser();
        return toDto(findOwnedMovement(id, user), user);
    }

    @Transactional
    public void delete(UUID id) {
        User user = authenticatedUserService.getCurrentUser();
        StockMovement movement = findOwnedMovement(id, user);
        Product product = movement.getProduct();

        if (movement.getType() == MovementType.ENTRY &&
                product.getQuantity() < movement.getQuantity()) {
            throw new RuntimeException(
                    "Não é possível excluir a entrada: o estoque atual ficaria negativo"
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
        User user = authenticatedUserService.getCurrentUser();
        return stockMovementRepository
                .findByUserAndCreatedAtBetween(user, start, end);
    }

    public List<StockMovementDTO> reportDto(LocalDateTime start, LocalDateTime end) {
        User user = authenticatedUserService.getCurrentUser();
        return stockMovementRepository.findByUserAndCreatedAtBetween(user, start, end)
                .stream()
                .map(movement -> toDto(movement, user))
                .toList();
    }

    public StockMovementDTO toDto(StockMovement movement, User user) {
        return StockMovementDTO.from(movement, productServiceDto(movement, user));
    }

    private com.matheus.estoque.product.dto.ProductDTO productServiceDto(StockMovement movement, User user) {
        if (movement.getProduct() == null) {
            return null;
        }
        String thumbnailUrl = attachmentService.findProductThumbnailUrl(movement.getProduct(), user).orElse(null);
        return com.matheus.estoque.product.dto.ProductDTO.from(movement.getProduct(), thumbnailUrl);
    }

    private StockMovement findOwnedMovement(
            UUID id,
            User user
    ) {
        return stockMovementRepository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new RuntimeException("Movimentação não encontrada"));
    }
}
