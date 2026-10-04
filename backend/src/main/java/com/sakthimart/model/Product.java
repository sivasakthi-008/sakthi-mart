package com.sakthimart.model;

import java.math.BigDecimal;

public class Product {

    private Long id;
    private Long sellerId;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private String category;
    private String imageUrl;

    public Product() {
    }

    public Product(
            Long id,
            Long sellerId,
            String name,
            String description,
            BigDecimal price,
            int stock,
            String category,
            String imageUrl) {

        this.id = id;
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}