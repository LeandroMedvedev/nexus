package com.company.nexus.controller;

import com.company.nexus.dto.ProductRequestDTO;
import com.company.nexus.model.Product;
import com.company.nexus.model.Supplier;
import com.company.nexus.repository.ProductRepository;
import com.company.nexus.repository.SupplierRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SupplierRepository supplierRepository;

    private Supplier defaultSupplier;

    @BeforeEach // Este método roda antes de CADA teste na classe.
    void setUp() {
        // Cria um fornecedor padrão que pode ser usado por múltiplos testes.
        defaultSupplier = supplierRepository.save(new Supplier(
                null,
                "Componentes BR",
                "Ana",
                "vendas@componentesbr.com",
                "444",
                new ArrayList<>()
        ));
    }

    @Test
    @DisplayName("Deve criar um produto com sucesso quando o fornecedor existir")
    void createProduct_withValidDataAndExistingSupplier_shouldReturnCreated() throws Exception {
        var requestDTO = new ProductRequestDTO(
                "SSD 1TB",
                "Solid State Drive",
                new BigDecimal("350.00"),
                "SSD-1TB-XYZ",
                defaultSupplier.getId()
        );

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("SSD 1TB"))
                .andExpect(jsonPath("$.supplier.id").value(defaultSupplier.getId()));
    }

    @Test
    @DisplayName("Deve retornar status 404 ao tentar criar um produto com um fornecedor inexistente")
    void createProduct_withNonExistentSupplier_shouldReturnNotFound() throws Exception {
        var requestDTO = new ProductRequestDTO(
                "Mouse Gamer",
                "Mouse RGB",
                new BigDecimal("150.00"),
                "MOUSE-RGB-123",
                999L
        );

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar um produto pelo ID quando o ID existir")
    void getProductById_whenIdExists_shouldReturnProduct() throws Exception {
        Product savedProduct = productRepository.save(new Product(
                null,
                "RAM 16GB",
                "DDR4 RAM",
                new BigDecimal("250.00"),
                "RAM-16GB-DDR4",
                defaultSupplier
        ));

        mockMvc.perform(get("/api/v1/products/{id}", savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedProduct.getId()))
                .andExpect(jsonPath("$.name").value("RAM 16GB"));
    }

    @Test
    @DisplayName("Deve atualizar um produto com sucesso")
    void updateProduct_withValidData_shouldReturnOk() throws Exception {
        Product existingProduct = productRepository.save(new Product(
                null,
                "Old SKU",
                "Old Desc",
                BigDecimal.TEN,
                "OLD-SKU",
                defaultSupplier
        ));
        var requestDTO = new ProductRequestDTO(
                "New SKU", "New Desc", BigDecimal.ONE, "NEW-SKU", defaultSupplier.getId()
        );

        mockMvc.perform(put("/api/v1/products/{id}", existingProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New SKU"))
                .andExpect(jsonPath("$.sku").value("NEW-SKU"));
    }

    @Test
    @DisplayName("Deve excluir um produto com sucesso")
    void deleteProduct_whenIdExists_shouldReturnNoContent() throws Exception {
        Product productToDelete = productRepository.save(new Product(
                null, "ToDelete", "...", BigDecimal.ZERO, "DEL-SKU", defaultSupplier)
        );

        mockMvc.perform(delete("/api/v1/products/{id}", productToDelete.getId()))
                .andExpect(status().isNoContent());
    }
}
