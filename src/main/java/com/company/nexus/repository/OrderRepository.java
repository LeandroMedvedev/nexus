package com.company.nexus.repository;

import com.company.nexus.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Busca todos os pedidos associados a um "ID" de cliente específico.
     *
     * @param customerId O ID do cliente.
     * @return Uma lista de Pedidos pertencentes ao cliente.
     */
    Optional<Order> findByCustomerId(Long customerId);
}
