package com.company.nexus.controller;

import com.company.nexus.dto.CustomerRequestDTO;
import com.company.nexus.model.Customer;
import com.company.nexus.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository; // Injetado para preparar os dados de teste.

    @Test
    @DisplayName("Deve criar um cliente com sucesso e retornar status 201")
    void createCustomer_withValidData_shouldReturnCreated() throws Exception {
        // Arrange
        var requestDTO = new CustomerRequestDTO(
                "Carrie",
                "Heffernan",
                "heffernancarrie@sitcom.com",
                "500742399",
                "456 Oak Avenue"
        );

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        // Assert
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Carrie"))
                .andExpect(jsonPath("$.lastName").value("Heffernan"))
                .andExpect(jsonPath("$.email").value("heffernancarrie@sitcom.com"))
                .andExpect(jsonPath("$.phone").value("500742399"))
                .andExpect(jsonPath("$.address").value("456 Oak Avenue"));
    }

    @Test
    @DisplayName("Deve retornar um cliente pelo ID quando o ID existir")
    void getCustomerById_whenIdExists_shouldReturnCustomer() throws Exception {
        // Arrange: Salva um cliente no banco para que possamos buscá-lo.
        Customer savedCustomer = customerRepository.save(new Customer(
                null,
                "Doug",
                "Heffernan",
                "heffernandoug@sitcom.com",
                "300578992",
                "456 Oak Avenue"
        ));

        // Act & Assert
        mockMvc.perform(get("/api/v1/customers/{id}", savedCustomer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCustomer.getId()))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Doug"))
                .andExpect(jsonPath("$.lastName").value("Heffernan"))
                .andExpect(jsonPath("$.email").value("heffernandoug@sitcom.com"))
                .andExpect(jsonPath("$.phone").value("300578992"))
                .andExpect(jsonPath("$.address").value("456 Oak Avenue"));
    }

    @Test
    @DisplayName("Deve retornar status 404 quando o ID do cliente não existir")
    void getCustomerById_whenIdDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        long nonExistentId = 999L;

        // Act & Assert
        mockMvc.perform(get("/api/v1/customers/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um cliente com sucesso e retornar status 200")
    void updateCustomer_withValidData_shouldReturnOk() throws Exception {
        // Arrange
        Customer existingCustomer = customerRepository.save(new Customer(
                null,
                "Old",
                "Name",
                "old@example.com",
                "111",
                "Old Address"));
        var requestDTO = new CustomerRequestDTO(
                "New", "Name", "new@example.com", "222", "New Address"
        );

        // Act
        ResultActions result = mockMvc.perform(put("/api/v1/customers/{id}", existingCustomer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    @DisplayName("Deve deletar um cliente com sucesso e retornar status 204")
    void deleteCustomer_whenIdExists_shouldReturnNoContent() throws Exception {
        // Arrange
        Customer customerToDelete = customerRepository.save(new Customer(
                null,
                "ToDelete",
                "User",
                "todelete@example.com",
                "333", "Anywhere"
        ));

        mockMvc.perform(delete("/api/v1/customers/{id}", customerToDelete.getId()))
                .andExpect(status().isNoContent());
    }
}