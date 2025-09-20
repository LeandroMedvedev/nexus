package com.company.nexus.repository;

import com.company.nexus.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Anotação @Repository opcional, pois Spring Data JPA já a infere.
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // JpaRepository<Customer, Long>
    // 1. Customer: A entidade que este repositório gerencia.
    // 2. Long: O tipo da chave primária (PK) da entidade Customer.
    Optional<Customer> findByEmail(String email);
}
