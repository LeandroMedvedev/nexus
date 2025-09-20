package com.company.nexus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequestDTO(
        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 100, message = "Lasts name must be between 2 and 100 characters")
        String lastName,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Must be a valid email format")
        String email,

        String phone,

        String address
) {
}
