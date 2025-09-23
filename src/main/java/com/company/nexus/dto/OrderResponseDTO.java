package com.company.nexus.dto;

import com.company.nexus.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record OrderResponseDTO(
        Long id,
        LocalDateTime orderDate,
        String status,
        BigDecimal totalAmount,
        Long customerId,
        String customerName,
        Long employeeId, // Pode ser nulo
        String employeeName, // Pode ser nulo
        List<OrderItemResponseDTO> items
) {
    public OrderResponseDTO(Order order) {
        this(
                order.getId(),
                order.getOrderDate(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCustomer().getId(),
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
                order.getEmployee() != null
                        ? order.getEmployee().getId()
                        : null,
                order.getEmployee() != null
                        ? order.getEmployee().getFirstName() + " " + order.getEmployee().getLastName()
                        : null,
                order.getItems().stream().map(OrderItemResponseDTO::new).collect(Collectors.toList())
        );
    }
}
