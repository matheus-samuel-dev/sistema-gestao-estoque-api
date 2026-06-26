package com.matheus.estoque.attachment.dto;

public record AttachmentFileDTO(String fileName, String contentType, byte[] data) {
}
