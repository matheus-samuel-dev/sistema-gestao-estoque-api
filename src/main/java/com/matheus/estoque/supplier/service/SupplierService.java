package com.matheus.estoque.supplier.service;

import com.matheus.estoque.security.AuthenticatedUserService;
import com.matheus.estoque.supplier.dto.SupplierDTO;
import com.matheus.estoque.supplier.dto.SupplierRequestDTO;
import com.matheus.estoque.supplier.entity.Supplier;
import com.matheus.estoque.supplier.repository.SupplierRepository;
import com.matheus.estoque.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SupplierService {
    private final SupplierRepository repository;
    private final AuthenticatedUserService authenticatedUsers;

    public SupplierService(SupplierRepository repository, AuthenticatedUserService authenticatedUsers) {
        this.repository = repository;
        this.authenticatedUsers = authenticatedUsers;
    }

    public List<SupplierDTO> findAll() {
        User user = authenticatedUsers.getCurrentUser();
        return repository.findByUserOrderByNameAsc(user).stream().map(SupplierDTO::from).toList();
    }

    public List<SupplierDTO> findActive() {
        User user = authenticatedUsers.getCurrentUser();
        return repository.findByUserAndActiveTrueOrderByNameAsc(user).stream().map(SupplierDTO::from).toList();
    }

    public SupplierDTO create(SupplierRequestDTO dto) {
        User user = authenticatedUsers.getCurrentUser();
        if (repository.existsByUserAndNameIgnoreCase(user, dto.name().trim())) {
            throw new RuntimeException("Já existe um fornecedor com este nome.");
        }
        Supplier supplier = Supplier.builder().user(user).active(dto.active() == null || dto.active()).build();
        return SupplierDTO.from(repository.save(apply(supplier, dto)));
    }

    public SupplierDTO update(UUID id, SupplierRequestDTO dto) {
        User user = authenticatedUsers.getCurrentUser();
        Supplier supplier = findOwned(id, user);
        return SupplierDTO.from(repository.save(apply(supplier, dto)));
    }

    public SupplierDTO setActive(UUID id, boolean active) {
        User user = authenticatedUsers.getCurrentUser();
        Supplier supplier = findOwned(id, user);
        supplier.setActive(active);
        return SupplierDTO.from(repository.save(supplier));
    }

    public void delete(UUID id) {
        User user = authenticatedUsers.getCurrentUser();
        Supplier supplier = findOwned(id, user);
        supplier.setActive(false);
        repository.save(supplier);
    }

    public Supplier findOwned(UUID id, User user) {
        return repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
    }

    private Supplier apply(Supplier supplier, SupplierRequestDTO dto) {
        supplier.setName(dto.name().trim());
        supplier.setDocument(blankToNull(dto.document()));
        supplier.setPhone(blankToNull(dto.phone()));
        supplier.setEmail(blankToNull(dto.email()));
        supplier.setNotes(blankToNull(dto.notes()));
        if (dto.active() != null) {
            supplier.setActive(dto.active());
        }
        return supplier;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
