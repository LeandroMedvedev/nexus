package com.company.nexus.service;

import com.company.nexus.dto.OrderRequestDTO;
import com.company.nexus.dto.OrderResponseDTO;
import com.company.nexus.model.*;
import com.company.nexus.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final EmployeeService employeeService;
    private final ProductService productService;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) {
        // Buscar as entidades principais
        Customer customer = customerService.findCustomerById(requestDTO.customerId());

        Employee employee = null;
        if (requestDTO.employeeId() != null) {
            employee = employeeService.findEmployeeById(requestDTO.employeeId());
        }

        // 2. Processar os itens e calcular o total
        Order order = new Order();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (var itemDto : requestDTO.items()) {
            Product product = productService.findProductById(itemDto.productId());

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.quantity());
            orderItem.setUnitPrice(product.getPrice());  // Usar o preço do banco.

            order.addItem(orderItem);  // Adiciona o item ao pedido e estabelece a relação bidirecional

            totalAmount = totalAmount.add(
                    product.getPrice().multiply(BigDecimal.valueOf(itemDto.quantity()))
            );
        }

        // Montar e persistir o pedido
        order.setCustomer(customer);
        order.setEmployee(employee);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING_PAYMENT");
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        return new OrderResponseDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(OrderResponseDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        return new OrderResponseDTO(order);
    }
}
