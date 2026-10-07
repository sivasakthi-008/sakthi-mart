package com.sakthimart.model;

import java.math.BigDecimal;

public class Order {

    private Long id;
    private Long buyerId;
    private BigDecimal totalAmount;
    private String status;

    public Order() {
    }

    public Order(
            Long id,
            Long buyerId,
            BigDecimal totalAmount,
            String status) {

        this.id = id;
        this.buyerId = buyerId;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }
}