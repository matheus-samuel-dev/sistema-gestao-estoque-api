package com.matheus.estoque.supplier.dto;

import com.matheus.estoque.supplier.entity.Supplier;

import java.util.UUID;

public record SupplierDTO(
        UUID id,
        String name,
        String document,
        String phone,
        String email,
        String notes,
        Boolean active
) {
    public static SupplierDTO from(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return new SupplierDTO(
                supplier.getId(),
                supplier.getName(),
                supplier.getDocument(),
                supplier.getPhone(),
                supplier.getEmail(),
                supplier.getNotes(),
                supplier.getActive()
        );
    }
}
