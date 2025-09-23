package com.company.nexus.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequestDTO(
        @NotNull
        Long productId,

        @NotNull
        @Positive(message = "Quantity must be a positive number")
        Integer quantity
) {}
