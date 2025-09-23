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

        Customer customer = new Customer();
        mapDtoToEntity(requestDTO, customer);
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
        Customer customer = findCustomerById(id);

        return new CustomerResponseDTO(customer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO) {
        Customer existingCustomer = findCustomerById(id);
        mapDtoToEntity(requestDTO, existingCustomer);
        Customer updatedCustomer = customerRepository.save(existingCustomer);

        return new CustomerResponseDTO(updatedCustomer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        findCustomerById(id);
        customerRepository.deleteById(id);
    }

    public Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
    }

    /**
     * Mapeia os dados de um CustomerRequestDTO para uma entidade Customer.
     * Centraliza a lógica de conversão de DTO para Entidade.
     */
    private void mapDtoToEntity(CustomerRequestDTO dto, Customer customer) {
        customer.setFirstName(dto.firstName());
        customer.setLastName(dto.lastName());
        customer.setEmail(dto.email());
        customer.setPhone(dto.phone());
        customer.setAddress(dto.address());
    }
}
