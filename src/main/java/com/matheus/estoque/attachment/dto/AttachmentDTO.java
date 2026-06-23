package com.matheus.estoque.attachment.dto;

import com.matheus.estoque.attachment.entity.Attachment;
import java.time.LocalDateTime;
import java.util.UUID;

public record AttachmentDTO(UUID id, String fileName, String contentType, long size, LocalDateTime createdAt) {
    public static AttachmentDTO from(Attachment value) {
        return new AttachmentDTO(value.getId(), value.getFileName(), value.getContentType(), value.getSize(), value.getCreatedAt());
    }
}
