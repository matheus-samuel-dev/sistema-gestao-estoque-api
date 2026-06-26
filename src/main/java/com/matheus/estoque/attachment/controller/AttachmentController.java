package com.matheus.estoque.attachment.controller;

import com.matheus.estoque.attachment.dto.AttachmentFileDTO;
import com.matheus.estoque.attachment.dto.AttachmentSummaryDTO;
import com.matheus.estoque.attachment.service.AttachmentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
public class AttachmentController {
    private final AttachmentService service;

    public AttachmentController(AttachmentService service) {
        this.service = service;
    }

    @PostMapping(
            value = {"/products/{productId}/attachments", "/attachments/products/{productId}"},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public AttachmentSummaryDTO uploadProduct(
            @PathVariable UUID productId,
            @RequestParam("file") MultipartFile file
    ) {
        return service.uploadForProduct(productId, file);
    }

    @PostMapping(
            value = {"/movements/{movementId}/attachments", "/attachments/movements/{movementId}"},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public AttachmentSummaryDTO uploadMovement(
            @PathVariable UUID movementId,
            @RequestParam("file") MultipartFile file
    ) {
        return service.uploadForMovement(movementId, file);
    }

    @GetMapping({"/products/{productId}/attachments", "/attachments/products/{productId}"})
    public List<AttachmentSummaryDTO> listProduct(@PathVariable UUID productId) {
        return service.listProduct(productId);
    }

    @GetMapping({"/movements/{movementId}/attachments", "/attachments/movements/{movementId}"})
    public List<AttachmentSummaryDTO> listMovement(@PathVariable UUID movementId) {
        return service.listMovement(movementId);
    }

    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<byte[]> download(@PathVariable UUID attachmentId) {
        AttachmentFileDTO file = service.download(attachmentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sanitize(file.fileName()) + "\"")
                .body(file.data());
    }

    @GetMapping("/attachments/{attachmentId}/view")
    public ResponseEntity<byte[]> view(@PathVariable UUID attachmentId) {
        AttachmentFileDTO file = service.viewImage(attachmentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + sanitize(file.fileName()) + "\"")
                .body(file.data());
    }

    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> delete(@PathVariable UUID attachmentId) {
        service.delete(attachmentId);
        return ResponseEntity.noContent().build();
    }

    private String sanitize(String fileName) {
        return fileName == null ? "arquivo" : fileName.replace("\"", "");
    }
}
