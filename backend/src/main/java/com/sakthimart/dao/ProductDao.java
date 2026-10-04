package com.sakthimart.dao;

import com.sakthimart.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {

    void save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findAll();

    List<Product> findBySellerId(Long sellerId);

    void update(Product product);

    void delete(Long id);
}