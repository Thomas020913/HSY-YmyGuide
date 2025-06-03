package com.thomas.page.admin.product.model;

public class Product {
    private String name;
    private double price;
    private int stock;
    private String description;
    private String imageUrl;

    public Product(String name, double price, int stock, String description, String imageUrl) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
} 