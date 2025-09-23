package com.company.nexus.controller;

import com.company.nexus.dto.OrderItemRequestDTO;
import com.company.nexus.dto.OrderRequestDTO;
import com.company.nexus.model.*;
import com.company.nexus.repository.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private OrderRepository orderRepository;

    private Customer carrie;
    private Employee doug;
    private Product tv;

    @BeforeEach
    void setUp() {
        carrie = customerRepository.save(new Customer(
                null,
                "Carrie",
                "Heffernan",
                "carrie@example.com",
                "111",
                "Queens, NY"
        ));
        doug = employeeRepository.save(new Employee(
                null,
                "Doug",
                "Heffernan",
                "IPS Driver",
                "doug@ips.com",
                LocalDate.of(1995, 5, 1)
        ));
        Supplier supplier = supplierRepository.save(new Supplier(
                null,
                "Big TV Store",
                "Spence",
                "spence@tv.com",
                "222",
                new ArrayList<>()
        ));
        tv = productRepository.save(new Product(
                null,
                "Big Screen TV",
                "A very large TV",
                new BigDecimal("1200.00"),
                "TV-BIG-01",
                supplier
        ));
    }

    @Test
    @DisplayName("Deve criar um pedido com sucesso e retornar status 201")
    void createOrder_withValidData_shouldReturnCreated() throws Exception {
        var itemDTO = new OrderItemRequestDTO(tv.getId(), 1);
        var orderRequestDTO = new OrderRequestDTO(carrie.getId(), doug.getId(), List.of(itemDTO));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerName").value("Carrie Heffernan"))
                .andExpect(jsonPath("$.employeeName").value("Doug Heffernan"))
                .andExpect(jsonPath("$.totalAmount").value(1200.00))
                .andExpect(jsonPath("$.items[0].productName").value("Big Screen TV"));
    }

    @Test
    @DisplayName("Deve buscar um pedido pelo ID com sucesso")
    void getOrderById_whenIdExists_shouldReturnOrder() throws Exception {
        Order order = new Order();
        order.setCustomer(carrie);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING_PAYMENT");
        Order savedOrder = orderRepository.save(order);

        mockMvc.perform(get("/api/v1/orders/{id}", savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.customerId").value(carrie.getId()));
    }

    @Test
    @DisplayName("Deve cancelar um pedido com sucesso e retornar status 200")
    void cancelOrder_whenOrderIsCancellable_shouldReturnOk() throws Exception {
        Order order = new Order();
        order.setCustomer(carrie);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING_PAYMENT"); // Status que permite cancelamento
        Order savedOrder = orderRepository.save(order);

        mockMvc.perform(post("/api/v1/orders/{id}/cancel", savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("Deve retornar status 409 ao tentar cancelar um pedido já enviado")
    void cancelOrder_whenOrderIsNotCancellable_shouldReturnConflict() throws Exception {
        Order order = new Order();
        order.setCustomer(carrie);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("SHIPPED"); // Status que NÃO permite cancelamento
        Order savedOrder = orderRepository.save(order);

        mockMvc.perform(post("/api/v1/orders/{id}/cancel", savedOrder.getId()))
                .andExpect(status().isConflict()); // Esperamos 409 Conflict, tratado pelo nosso handler
    }
}
