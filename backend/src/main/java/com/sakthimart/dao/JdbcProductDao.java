package com.sakthimart.dao;

import com.sakthimart.config.DatabaseConfig;
import com.sakthimart.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcProductDao implements ProductDao {

    @Override
    public void save(Product product) {

        String sql = """
                INSERT INTO products
                (seller_id, name, description, price, stock, category, image_url)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection =
                     DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, product.getSellerId());
            statement.setString(2, product.getName());
            statement.setString(3, product.getDescription());
            statement.setBigDecimal(4, product.getPrice());
            statement.setInt(5, product.getStock());
            statement.setString(6, product.getCategory());
            statement.setString(7, product.getImageUrl());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Unable to save product", e);
        }
    }

    @Override
    public Optional<Product> findById(Long id) {

        String sql = "SELECT * FROM products WHERE id = ?";

        try (Connection connection =
                     DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapProduct(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Unable to find product", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {

        String sql = "SELECT * FROM products ORDER BY created_at DESC";

        List<Product> products = new ArrayList<>();

        try (Connection connection =
                     DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {
                products.add(mapProduct(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch products", e);
        }

        return products;
    }

    @Override
    public List<Product> findBySellerId(Long sellerId) {

        String sql = """
                SELECT * FROM products
                WHERE seller_id = ?
                ORDER BY created_at DESC
                """;

        List<Product> products = new ArrayList<>();

        try (Connection connection =
                     DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, sellerId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    products.add(mapProduct(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to fetch seller products", e);
        }

        return products;
    }

    @Override
    public void update(Product product) {

        String sql = """
                UPDATE products
                SET name = ?, description = ?, price = ?,
                    stock = ?, category = ?, image_url = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection connection =
                     DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setBigDecimal(3, product.getPrice());
            statement.setInt(4, product.getStock());
            statement.setString(5, product.getCategory());
            statement.setString(6, product.getImageUrl());
            statement.setLong(7, product.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Unable to update product", e);
        }
    }

    @Override
    public void delete(Long id) {

        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection connection =
                     DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete product", e);
        }
    }

    private Product mapProduct(ResultSet resultSet)
            throws SQLException {

        return new Product(
                resultSet.getLong("id"),
                resultSet.getLong("seller_id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getBigDecimal("price"),
                resultSet.getInt("stock"),
                resultSet.getString("category"),
                resultSet.getString("image_url")
        );
    }
}