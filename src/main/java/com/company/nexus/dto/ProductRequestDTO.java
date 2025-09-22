package com.company.nexus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank @Size(min = 2, max = 255)
        String name,

        String description,

        @NotNull @Positive
        BigDecimal price,

        @NotBlank @Size(max = 100)
        String sku,

        @NotNull  // Somente o "ID" do fornecedor é requerido para criação.
        Long supplierId
) {
}
