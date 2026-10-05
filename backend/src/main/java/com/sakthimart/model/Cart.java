package com.sakthimart.model;

public class Cart {

    private Long id;
    private Long buyerId;

    public Cart() {
    }

    public Cart(Long id, Long buyerId) {
        this.id = id;
        this.buyerId = buyerId;
    }

    public Long getId() {
        return id;
    }

    public Long getBuyerId() {
        return buyerId;
    }
}