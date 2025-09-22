package com.company.nexus.dto;

import com.company.nexus.model.Product;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String sku,
        SupplierResponseDTO supplier  // DTO aninhado para representar o relacionamento.
) {
    public ProductResponseDTO(Product product) {
        this(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getSku(),
                new SupplierResponseDTO(product.getSupplier())
        );
    }
}
