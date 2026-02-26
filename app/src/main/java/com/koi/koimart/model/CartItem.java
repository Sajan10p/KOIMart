package com.koi.koimart.model;
public class CartItem {
    public int cartId;
    public int productId;
    public String name;
    public double price;
    public int qty;

    public CartItem(int cartId, int productId, String name, double price, int qty) {
        this.cartId = cartId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.qty = qty;
    }
}
