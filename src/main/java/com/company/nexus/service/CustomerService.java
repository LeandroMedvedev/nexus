package com.company.nexus.service;

import com.company.nexus.dto.CustomerRequestDTO;
import com.company.nexus.dto.CustomerResponseDTO;
import com.company.nexus.model.Customer;
import com.company.nexus.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO) {
        if (customerRepository.findByEmail(requestDTO.email()).isPresent()) {
            throw new IllegalArgumentException("Customer with email " + requestDTO.email() + " already exists.");
        }

        // Mapeamento de DTO para Entidade
        Customer customer = new Customer();
        customer.setFirstName(requestDTO.firstName());
        customer.setLastName(requestDTO.lastName());
        customer.setEmail(requestDTO.email());
        customer.setPhone(requestDTO.phone());
        customer.setAddress(requestDTO.address());

        Customer savedCustomer = customerRepository.save(customer);

        return new CustomerResponseDTO(savedCustomer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerResponseDTO::new)  // Converte cada Customer em CustomerResponseDTO
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
        return new CustomerResponseDTO(customer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));

        // Mapeia os novos dados do DTO para a entidade existente.
        existingCustomer.setFirstName(requestDTO.firstName());
        existingCustomer.setLastName(requestDTO.lastName());
        existingCustomer.setEmail(requestDTO.email());
        existingCustomer.setPhone(requestDTO.phone());
        existingCustomer.setAddress(requestDTO.address());

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return new CustomerResponseDTO(updatedCustomer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
