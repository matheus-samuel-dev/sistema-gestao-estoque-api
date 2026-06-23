package com.matheus.estoque.attachment.service;

import com.matheus.estoque.attachment.dto.AttachmentDTO;
import com.matheus.estoque.attachment.entity.Attachment;
import com.matheus.estoque.attachment.repository.AttachmentRepository;
import com.matheus.estoque.product.entity.Product;
import com.matheus.estoque.product.repository.ProductRepository;
import com.matheus.estoque.security.AuthenticatedUserService;
import com.matheus.estoque.stockmovement.entity.StockMovement;
import com.matheus.estoque.stockmovement.repository.StockMovementRepository;
import com.matheus.estoque.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AttachmentService {
    private static final long MAX_SIZE = 10 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of("application/pdf", "image/jpeg", "image/png", "image/webp");
    private final AttachmentRepository repository;
    private final ProductRepository products;
    private final StockMovementRepository movements;
    private final AuthenticatedUserService authenticatedUsers;

    public AttachmentService(AttachmentRepository repository, ProductRepository products,
                             StockMovementRepository movements, AuthenticatedUserService authenticatedUsers) {
        this.repository = repository; this.products = products; this.movements = movements; this.authenticatedUsers = authenticatedUsers;
    }

    public AttachmentDTO uploadForProduct(UUID id, MultipartFile file) throws IOException {
        User user = authenticatedUsers.getCurrentUser();
        Product product = products.findByIdAndUserAndActiveTrue(id, user).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        return AttachmentDTO.from(save(file, user, product, null));
    }

    public AttachmentDTO uploadForMovement(UUID id, MultipartFile file) throws IOException {
        User user = authenticatedUsers.getCurrentUser();
        StockMovement movement = movements.findByIdAndUser(id, user).orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
        return AttachmentDTO.from(save(file, user, null, movement));
    }

    public List<AttachmentDTO> listProduct(UUID id) {
        User user = authenticatedUsers.getCurrentUser();
        products.findByIdAndUserAndActiveTrue(id, user).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        return repository.findByProductIdAndUserOrderByCreatedAtDesc(id, user).stream().map(AttachmentDTO::from).toList();
    }

    public List<AttachmentDTO> listMovement(UUID id) {
        User user = authenticatedUsers.getCurrentUser();
        movements.findByIdAndUser(id, user).orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
        return repository.findByMovementIdAndUserOrderByCreatedAtDesc(id, user).stream().map(AttachmentDTO::from).toList();
    }

    public Attachment get(UUID id) { return repository.findByIdAndUser(id, authenticatedUsers.getCurrentUser()).orElseThrow(() -> new RuntimeException("Anexo não encontrado")); }
    public void delete(UUID id) { repository.delete(get(id)); }

    private Attachment save(MultipartFile file, User user, Product product, StockMovement movement) throws IOException {
        if (file.isEmpty()) throw new RuntimeException("Selecione um arquivo.");
        if (file.getSize() > MAX_SIZE) throw new RuntimeException("O arquivo deve ter no máximo 10 MB.");
        if (!ALLOWED_TYPES.contains(file.getContentType())) throw new RuntimeException("Formato inválido. Use PDF, JPG, PNG ou WEBP.");
        return repository.save(Attachment.builder().fileName(file.getOriginalFilename()).contentType(file.getContentType())
                .size(file.getSize()).data(file.getBytes()).product(product).movement(movement).user(user).build());
    }
}
