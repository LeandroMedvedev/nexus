package com.company.nexus.dto;

import com.company.nexus.model.Customer;

public record CustomerResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address
) {
    public CustomerResponseDTO(Customer customer) {
        this(
                customer.getId(),
                customer.getFirstname(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress()
        );
    }
}
