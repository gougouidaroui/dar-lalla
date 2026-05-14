package com.darlalla.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.darlalla.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}