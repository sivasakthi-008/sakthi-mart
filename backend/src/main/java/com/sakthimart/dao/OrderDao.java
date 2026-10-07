package com.sakthimart.dao;

import com.sakthimart.model.Order;

import java.util.List;

public interface OrderDao {

    Order createOrder(
            Long buyerId,
            java.math.BigDecimal totalAmount
    );

    void addOrderItem(
            Long orderId,
            Long productId,
            int quantity,
            java.math.BigDecimal price
    );

    List<Order> findOrdersByBuyerId(Long buyerId);
}