package com.sakthimart.dao;

import com.sakthimart.config.DatabaseConfig;
import com.sakthimart.model.Cart;
import com.sakthimart.model.CartItem;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCartDao implements CartDao {

    private final DataSource dataSource;

    public JdbcCartDao() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    @Override
    public Cart createCart(Long buyerId) {

        String sql =
                "INSERT INTO carts (buyer_id) VALUES (?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, buyerId);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (keys.next()) {
                    return new Cart(
                            keys.getLong(1),
                            buyerId
                    );
                }
            }

            throw new RuntimeException("Unable to create cart");

        } catch (SQLException e) {
            throw new RuntimeException("Unable to create cart", e);
        }
    }

    @Override
    public Optional<Cart> findCartByBuyerId(Long buyerId) {

        String sql =
                "SELECT id, buyer_id FROM carts WHERE buyer_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, buyerId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return Optional.of(
                            new Cart(
                                    result.getLong("id"),
                                    result.getLong("buyer_id")
                            )
                    );
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to find cart", e);
        }
    }

    @Override
    public void addItem(
            Long cartId,
            Long productId,
            int quantity) {

        String sql =
                "MERGE INTO cart_items " +
                "(cart_id, product_id, quantity) " +
                "KEY (cart_id, product_id) " +
                "VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, cartId);
            statement.setLong(2, productId);
            statement.setInt(3, quantity);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to add item to cart", e);
        }
    }

    @Override
    public List<CartItem> findItemsByCartId(Long cartId) {

        String sql =
                "SELECT id, cart_id, product_id, quantity " +
                "FROM cart_items WHERE cart_id = ?";

        List<CartItem> items = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, cartId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    items.add(
                            new CartItem(
                                    result.getLong("id"),
                                    result.getLong("cart_id"),
                                    result.getLong("product_id"),
                                    result.getInt("quantity")
                            )
                    );
                }
            }

            return items;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to fetch cart items", e);
        }
    }

    @Override
    public void updateItemQuantity(
            Long cartId,
            Long productId,
            int quantity) {

        String sql =
                "UPDATE cart_items " +
                "SET quantity = ? " +
                "WHERE cart_id = ? AND product_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setLong(2, cartId);
            statement.setLong(3, productId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to update cart item", e);
        }
    }

    @Override
    public void removeItem(
            Long cartId,
            Long productId) {

        String sql =
                "DELETE FROM cart_items " +
                "WHERE cart_id = ? AND product_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, cartId);
            statement.setLong(2, productId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to remove cart item", e);
        }
    }
}