package com.matheus.estoque.stockmovement.controller;

import com.matheus.estoque.stockmovement.dto.CreateStockMovementDTO;
import com.matheus.estoque.stockmovement.dto.StockMovementDTO;
import com.matheus.estoque.stockmovement.entity.StockMovement;
import com.matheus.estoque.stockmovement.service.StockMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stock-movements")
@Tag(
        name = "Movimentações",
        description = "Controle de entrada e saída de estoque"
)
public class StockMovementController {
    private final StockMovementService service;

    public StockMovementController(StockMovementService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar movimentação de estoque")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public StockMovement create(
            @Valid @RequestBody CreateStockMovementDTO dto
    ) {
        return service.create(dto);
    }

    @Operation(summary = "Listar movimentações")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public Page<StockMovementDTO> findAll(
            Pageable pageable
    ) {
        return service.findAllDto(pageable);
    }

    @Operation(summary = "Buscar movimentação por ID")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public StockMovementDTO findById(@PathVariable UUID id) {
        return service.findByIdDto(id);
    }

    @Operation(summary = "Excluir movimentação")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @Operation(summary = "Relatório por período")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/report")
    public List<StockMovementDTO> report(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end
    ) {
        return service.reportDto(start, end);
    }
}
