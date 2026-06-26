package com.matheus.estoque.attachment.repository;

import com.matheus.estoque.attachment.dto.AttachmentSummaryDTO;
import com.matheus.estoque.attachment.entity.Attachment;
import com.matheus.estoque.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    @Query("""
            select new com.matheus.estoque.attachment.dto.AttachmentSummaryDTO(
                a.id,
                a.fileName,
                a.contentType,
                a.size,
                a.createdAt
            )
            from Attachment a
            where a.product.id = :productId
              and a.user = :user
            order by a.createdAt desc
            """)
    List<AttachmentSummaryDTO> findProductMetadata(
            @Param("productId") UUID productId,
            @Param("user") User user
    );

    @Query("""
            select new com.matheus.estoque.attachment.dto.AttachmentSummaryDTO(
                a.id,
                a.fileName,
                a.contentType,
                a.size,
                a.createdAt
            )
            from Attachment a
            where a.movement.id = :movementId
              and a.user = :user
            order by a.createdAt desc
            """)
    List<AttachmentSummaryDTO> findMovementMetadata(
            @Param("movementId") UUID movementId,
            @Param("user") User user
    );

    Optional<Attachment> findByIdAndUser(UUID id, User user);
}
