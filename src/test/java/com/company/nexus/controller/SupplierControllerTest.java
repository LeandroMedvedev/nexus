package com.company.nexus.controller;

import com.company.nexus.dto.SupplierRequestDTO;
import com.company.nexus.model.Supplier;
import com.company.nexus.repository.SupplierRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SupplierRepository supplierRepository;

    @Test
    @DisplayName("Deve criar um fornecedor com sucesso e retornar status 201")
    void createSupplier_withValidData_shouldReturnCreated() throws Exception {
        // Arrange
        var requestDTO = new SupplierRequestDTO(
                "Tech Supplies Inc.",
                "Doug Heffernan",
                "contact@techsupplies.com",
                "555-0404"
        );

        mockMvc.perform(post("/api/v1/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Tech Supplies Inc."));
    }

    @Test
    @DisplayName("Deve retornar um fornecedor pelo ID quando o ID existir")
    void getSupplierById_whenIdExists_shouldReturnSupplier() throws Exception {
        Supplier savedSupplier = supplierRepository.save(
                new Supplier(
                        null,
                        "Cell Phone Supplies Inc.",
                        "Carrie Heffernan",
                        "contact@cellphonesupplies.com",
                        "333-0101",
                        new ArrayList<>()
                )
        );

        mockMvc.perform(get("/api/v1/suppliers/{id}", savedSupplier.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedSupplier.getId()))
                .andExpect(jsonPath("$.name").value("Cell Phone Supplies Inc."));
    }

    @Test
    @DisplayName("Deve retornar status 404 ao buscar um ID de fornecedor que não existe")
    void getSupplierById_whenIdDoesNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/suppliers/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um fornecedor com sucesso e retornar status 200")
    void updateSupplier_withValidData_shouldReturnOk() throws Exception {
        Supplier existingSupplier = supplierRepository.save(new Supplier(
                null,
                "Old Name",
                "Old Contact",
                "old@supplier.com",
                "111",
                new ArrayList<>()
        ));
        var requestDTO = new SupplierRequestDTO(
                "New Name", "New Contact", "new@supplier.com", "222"
        );

        mockMvc.perform(put("/api/v1/suppliers/{id}", existingSupplier.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("new@supplier.com"));
    }

    @Test
    @DisplayName("Deve excluir um fornecedor com sucesso e retornar status 204")
    void deleteSupplier_whenIdExists_shouldReturnNoContent() throws Exception {
        Supplier supplierToDelete = supplierRepository.save(new Supplier(
                null,
                "ToDelete",
                "User",
                "todelete@supplier.com",
                "333",
                new ArrayList<>()
        ));

        mockMvc.perform(delete("/api/v1/suppliers/{id}", supplierToDelete.getId()))
                .andExpect(status().isNoContent());
    }
}
