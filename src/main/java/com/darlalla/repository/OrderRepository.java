package com.darlalla.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.darlalla.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(Order.OrderStatus status);
}