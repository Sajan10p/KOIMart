package com.koi.koimart.model;
public class Product {
    public int id;
    public String name;
    public String description;
    public double price;
    public int imageResId;

    public Product(int id, String name, String description, double price, int imageResId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
    }
}
