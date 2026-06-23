package com.matheus.estoque.attachment.controller;

import com.matheus.estoque.attachment.dto.AttachmentDTO;
import com.matheus.estoque.attachment.entity.Attachment;
import com.matheus.estoque.attachment.service.AttachmentService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {
    private final AttachmentService service;
    public AttachmentController(AttachmentService service) { this.service = service; }

    @PostMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AttachmentDTO uploadProduct(@PathVariable UUID id, @RequestPart("file") MultipartFile file) throws IOException { return service.uploadForProduct(id, file); }
    @PostMapping(value = "/movements/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AttachmentDTO uploadMovement(@PathVariable UUID id, @RequestPart("file") MultipartFile file) throws IOException { return service.uploadForMovement(id, file); }
    @GetMapping("/products/{id}") public List<AttachmentDTO> listProduct(@PathVariable UUID id) { return service.listProduct(id); }
    @GetMapping("/movements/{id}") public List<AttachmentDTO> listMovement(@PathVariable UUID id) { return service.listMovement(id); }
    @GetMapping("/{id}/download") public ResponseEntity<byte[]> download(@PathVariable UUID id) {
        Attachment file = service.get(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName().replace("\"", "") + "\"").body(file.getData());
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable UUID id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
