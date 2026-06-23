package com.matheus.estoque.attachment.repository;

import com.matheus.estoque.attachment.entity.Attachment;
import com.matheus.estoque.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByProductIdAndUserOrderByCreatedAtDesc(UUID productId, User user);
    List<Attachment> findByMovementIdAndUserOrderByCreatedAtDesc(UUID movementId, User user);
    Optional<Attachment> findByIdAndUser(UUID id, User user);
}
