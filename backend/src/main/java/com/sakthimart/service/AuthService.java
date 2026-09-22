package com.sakthimart.service;

import com.sakthimart.dao.UserDao;
import com.sakthimart.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void register(String name, String email, String password, String role) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                    "Password must contain at least 6 characters");
        }

        if (!role.equals("BUYER") && !role.equals("SELLER")) {
            throw new IllegalArgumentException("Invalid role");
        }

        if (userDao.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));

        User user = new User(
                null,
                name,
                email,
                passwordHash,
                role
        );

        userDao.save(user);
    }

    public User login(String email, String password) {

        Optional<User> userOptional = userDao.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userOptional.get();

        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }
}