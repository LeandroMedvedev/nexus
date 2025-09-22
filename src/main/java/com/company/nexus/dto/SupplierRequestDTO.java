package com.company.nexus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequestDTO(
        @NotBlank @Size(min = 2, max = 255)
        String name,

        String contactPerson,

        @NotBlank @Email
        String email,

        @Size(max = 20)
        String phone
) {
}
