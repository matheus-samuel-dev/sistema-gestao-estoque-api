package com.matheus.estoque.attachment.service;

import com.matheus.estoque.attachment.dto.AttachmentFileDTO;
import com.matheus.estoque.attachment.dto.AttachmentSummaryDTO;
import com.matheus.estoque.attachment.entity.Attachment;
import com.matheus.estoque.attachment.exception.AttachmentStorageException;
import com.matheus.estoque.attachment.repository.AttachmentRepository;
import com.matheus.estoque.product.entity.Product;
import com.matheus.estoque.product.repository.ProductRepository;
import com.matheus.estoque.security.AuthenticatedUserService;
import com.matheus.estoque.stockmovement.entity.StockMovement;
import com.matheus.estoque.stockmovement.repository.StockMovementRepository;
import com.matheus.estoque.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AttachmentService {
    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "image/webp"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "jpg", "jpeg", "png", "webp");

    private final AttachmentRepository repository;
    private final ProductRepository products;
    private final StockMovementRepository movements;
    private final AuthenticatedUserService authenticatedUsers;

    public AttachmentService(
            AttachmentRepository repository,
            ProductRepository products,
            StockMovementRepository movements,
            AuthenticatedUserService authenticatedUsers
    ) {
        this.repository = repository;
        this.products = products;
        this.movements = movements;
        this.authenticatedUsers = authenticatedUsers;
    }

    @Transactional
    public AttachmentSummaryDTO uploadForProduct(UUID id, MultipartFile file) {
        User user = authenticatedUsers.getCurrentUser();
        Product product = products.findByIdAndUserAndActiveTrue(id, user)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado. Atualize a página e tente novamente."));
        return AttachmentSummaryDTO.from(save(file, user, product, null));
    }

    @Transactional
    public AttachmentSummaryDTO uploadForMovement(UUID id, MultipartFile file) {
        User user = authenticatedUsers.getCurrentUser();
        StockMovement movement = movements.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada. Atualize a página e tente novamente."));
        return AttachmentSummaryDTO.from(save(file, user, null, movement));
    }

    @Transactional(readOnly = true)
    public List<AttachmentSummaryDTO> listProduct(UUID id) {
        User user = authenticatedUsers.getCurrentUser();
        products.findByIdAndUserAndActiveTrue(id, user)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado. Atualize a página e tente novamente."));
        return repository.findProductMetadata(id, user);
    }

    @Transactional(readOnly = true)
    public List<AttachmentSummaryDTO> listMovement(UUID id) {
        User user = authenticatedUsers.getCurrentUser();
        movements.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada. Atualize a página e tente novamente."));
        return repository.findMovementMetadata(id, user);
    }

    @Transactional(readOnly = true)
    public AttachmentFileDTO download(UUID id) {
        Attachment attachment = repository.findByIdAndUser(id, authenticatedUsers.getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Anexo não encontrado. Atualize a página e tente novamente."));

        byte[] data = attachment.getData();
        if (data == null || data.length == 0) {
            throw new RuntimeException("Não foi possível abrir este anexo. Envie o arquivo novamente.");
        }

        return new AttachmentFileDTO(
                attachment.getFileName(),
                attachment.getContentType(),
                data
        );
    }

    @Transactional
    public void delete(UUID id) {
        Attachment attachment = repository.findByIdAndUser(id, authenticatedUsers.getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Anexo não encontrado. Atualize a página e tente novamente."));
        repository.delete(attachment);
    }

    private Attachment save(MultipartFile file, User user, Product product, StockMovement movement) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Arquivo inválido.");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("O arquivo selecionado ultrapassa o limite de 5MB.");
        }

        String originalName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase()
                : "";

        if (!ALLOWED_EXTENSIONS.contains(extension) || !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Formato não permitido. Envie apenas PDF, JPG, JPEG, PNG ou WEBP.");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            throw new AttachmentStorageException("Não foi possível anexar o arquivo. Verifique o formato e tente novamente.", ex);
        }

        return repository.save(Attachment.builder()
                .fileName(originalName)
                .contentType(file.getContentType())
                .size(file.getSize())
                .data(bytes)
                .product(product)
                .movement(movement)
                .user(user)
                .build());
    }
}
