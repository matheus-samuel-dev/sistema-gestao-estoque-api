package com.matheus.estoque.supplier.controller;

import com.matheus.estoque.supplier.dto.SupplierDTO;
import com.matheus.estoque.supplier.dto.SupplierRequestDTO;
import com.matheus.estoque.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    private final SupplierService service;

    public SupplierController(SupplierService service) {
        this.service = service;
    }

    @GetMapping
    public List<SupplierDTO> findAll(@RequestParam(defaultValue = "false") boolean activeOnly) {
        return activeOnly ? service.findActive() : service.findAll();
    }

    @PostMapping
    public ResponseEntity<SupplierDTO> create(@Valid @RequestBody SupplierRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public SupplierDTO update(@PathVariable UUID id, @Valid @RequestBody SupplierRequestDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/status")
    public SupplierDTO setActive(@PathVariable UUID id, @RequestParam boolean active) {
        return service.setActive(id, active);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
