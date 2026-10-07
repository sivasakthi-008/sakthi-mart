package com.sakthimart.dao;

import com.sakthimart.config.DatabaseConfig;
import com.sakthimart.model.Order;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcOrderDao implements OrderDao {

    private final DataSource dataSource;

    public JdbcOrderDao() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    @Override
    public Order createOrder(
            Long buyerId,
            BigDecimal totalAmount) {

        String sql =
                "INSERT INTO orders " +
                "(buyer_id, total_amount, status) " +
                "VALUES (?, ?, 'PLACED')";

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, buyerId);
            statement.setBigDecimal(2, totalAmount);

            statement.executeUpdate();

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (keys.next()) {
                    return new Order(
                            keys.getLong(1),
                            buyerId,
                            totalAmount,
                            "PLACED"
                    );
                }
            }

            throw new RuntimeException(
                    "Unable to create order");

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to create order", e);
        }
    }

    @Override
    public void addOrderItem(
            Long orderId,
            Long productId,
            int quantity,
            BigDecimal price) {

        String sql =
                "INSERT INTO order_items " +
                "(order_id, product_id, quantity, price) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, orderId);
            statement.setLong(2, productId);
            statement.setInt(3, quantity);
            statement.setBigDecimal(4, price);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to add order item", e);
        }
    }

    @Override
    public List<Order> findOrdersByBuyerId(Long buyerId) {

        String sql =
                "SELECT id, buyer_id, total_amount, status " +
                "FROM orders " +
                "WHERE buyer_id = ? " +
                "ORDER BY created_at DESC";

        List<Order> orders = new ArrayList<>();

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, buyerId);

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {

                    orders.add(
                            new Order(
                                    result.getLong("id"),
                                    result.getLong("buyer_id"),
                                    result.getBigDecimal("total_amount"),
                                    result.getString("status")
                            )
                    );
                }
            }

            return orders;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to fetch orders", e);
        }
    }
}