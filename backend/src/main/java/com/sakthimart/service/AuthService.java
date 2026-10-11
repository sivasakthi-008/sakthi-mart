
package com.sakthimart.service;

import com.sakthimart.dao.UserDao;
import com.sakthimart.model.User;

import java.util.Locale;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void register(String name, String email,
                         String password, String role) {

        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Enter valid details. Password must be at least 8 characters."
            );
        }

        if (!Pattern.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                email.trim())) {
            throw new IllegalArgumentException("Enter a valid email.");
        }

        String safeRole = role == null
                ? "" : role.trim().toUpperCase(Locale.ROOT);

        if (!safeRole.equals("BUYER") && !safeRole.equals("SELLER")) {
            throw new IllegalArgumentException("Invalid registration role.");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (userDao.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("Email already registered.");
        }

        String passwordHash = BCrypt.hashpw(
                password, BCrypt.gensalt(12));

        User user = new User(
                null, name.trim(), normalizedEmail,
                passwordHash, safeRole);

        userDao.save(user);
    }

    public User login(String email, String password) {

        if (email == null || password == null) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        User user = userDao.findByEmail(
                email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid email or password."));

        String storedHash = user.getPasswordHash();

        if (storedHash == null
                || !storedHash.startsWith("$2")
                || !BCrypt.checkpw(password, storedHash)) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return user;
    }
}
