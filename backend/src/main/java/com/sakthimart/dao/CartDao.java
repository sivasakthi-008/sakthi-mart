package com.sakthimart.dao;

import com.sakthimart.model.Cart;
import com.sakthimart.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartDao {

    Cart createCart(Long buyerId);

    Optional<Cart> findCartByBuyerId(Long buyerId);

    void addItem(Long cartId, Long productId, int quantity);

    List<CartItem> findItemsByCartId(Long cartId);

    void updateItemQuantity(Long cartId, Long productId, int quantity);

    void removeItem(Long cartId, Long productId);
}