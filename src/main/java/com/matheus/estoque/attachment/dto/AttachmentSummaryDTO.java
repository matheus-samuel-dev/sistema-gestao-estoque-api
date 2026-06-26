package com.matheus.estoque.attachment.dto;

import com.matheus.estoque.attachment.entity.Attachment;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttachmentSummaryDTO(
        UUID id,
        String fileName,
        String contentType,
        long size,
        LocalDateTime createdAt
) {
    public static AttachmentSummaryDTO from(Attachment value) {
        return new AttachmentSummaryDTO(
                value.getId(),
                value.getFileName(),
                value.getContentType(),
                value.getSize(),
                value.getCreatedAt()
        );
    }
}
