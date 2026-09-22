package com.sakthimart.dao;

import com.sakthimart.config.DatabaseConfig;
import com.sakthimart.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcUserDao implements UserDao {

    @Override
    public void save(User user) {

        String sql = """
                INSERT INTO users (name, email, password_hash, role)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection =
                     DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Unable to save user", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {

        String sql = """
                SELECT id, name, email, password_hash, role
                FROM users
                WHERE email = ?
                """;

        try (Connection connection =
                     DatabaseConfig.getDataSource().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    User user = new User(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("password_hash"),
                            resultSet.getString("role")
                    );

                    return Optional.of(user);
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException("Unable to find user", e);
        }

        return Optional.empty();
    }
}