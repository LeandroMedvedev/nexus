package com.company.nexus.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequestDTO(
        @NotNull
        Long customerId,

        Long employeeId, // Opcional

        @NotEmpty(message = "Order must have at least one item")
        List<@Valid OrderItemRequestDTO> items // @Valid habilita a validação para os objetos na lista
) {}
