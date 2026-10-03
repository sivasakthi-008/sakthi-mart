package com.sakthimart.service;

import com.sakthimart.dao.UserDao;
import com.sakthimart.model.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Test
    void loginShouldRejectWrongPassword() {

        User user = new User(
                1L,
                "Test User",
                "test@example.com",
                "$2a$12$.Sz9eUi75rNxVGptuNSnx.q5GEdC.ahFaVkvx0rPilb0CbuYUmjaC",
                "BUYER"
        );

        UserDao userDao = new UserDao() {

            @Override
            public void save(User user) {
            }

            @Override
            public Optional<User> findByEmail(String email) {
                return Optional.of(user);
            }
        };

        AuthService authService = new AuthService(userDao);

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(
                        "test@example.com",
                        "wrongpassword"
                )
        );
    }
}