package com.company.nexus.dto;

import com.company.nexus.model.Employee;

import java.time.LocalDate;

public record EmployeeResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String role,
        String email,
        LocalDate hireDate
) {
    public EmployeeResponseDTO(Employee employee) {
        this(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getRole(),
                employee.getEmail(),
                employee.getHireDate()
        );
    }
}
