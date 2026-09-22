package com.sakthimart.dao;

import com.sakthimart.model.User;

import java.util.Optional;

public interface UserDao {

    void save(User user);

    Optional<User> findByEmail(String email);
}