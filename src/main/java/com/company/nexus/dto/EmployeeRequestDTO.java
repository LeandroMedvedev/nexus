package com.company.nexus.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EmployeeRequestDTO(
        @NotBlank(message = "First name cannot be blank")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        String lastName,

        @NotBlank(message = "Role cannot be blank")
        String role,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Must be a valid email format")
        String email,

        @NotNull(message = "Hire date cannot be null")
        @PastOrPresent(message = "Hire date cannot be in the future")
        LocalDate hireDate
) {}
