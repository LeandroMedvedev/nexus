package com.company.nexus.dto;

import com.company.nexus.model.Supplier;

public record SupplierResponseDTO(
        Long id,
        String name,
        String contactPerson,
        String email,
        String phone
) {
    public SupplierResponseDTO(Supplier supplier) {
        this(
                supplier.getId(),
                supplier.getName(),
                supplier.getContactPerson(),
                supplier.getEmail(),
                supplier.getPhone()
        );
    }
}
