package com.sakthimart.model;

public class CartItem {

    private Long id;
    private Long cartId;
    private Long productId;
    private int quantity;

    public CartItem() {
    }

    public CartItem(
            Long id,
            Long cartId,
            Long productId,
            int quantity) {

        this.id = id;
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Long getCartId() {
        return cartId;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}