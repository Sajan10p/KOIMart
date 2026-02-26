package com.koi.koimart.util;

import com.koi.koimart.R;
import com.koi.koimart.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductStore {

    public static List<Product> getProducts() {
        List<Product> list = new ArrayList<>();
        list.add(new Product(1, "Wireless Earbuds", "Compact earbuds with clear sound and charging case.", 59.99, R.drawable.p1));
        list.add(new Product(2, "Smart Watch", "Fitness tracking watch with notifications and heart rate.", 89.50, R.drawable.p2));
        list.add(new Product(3, "Phone Case", "Shockproof case for daily protection.", 14.99, R.drawable.p3));
        list.add(new Product(4, "USB-C Cable", "Fast charging USB-C cable (1.5m).", 9.99, R.drawable.p4));
        list.add(new Product(5, "Bluetooth Speaker", "Portable speaker with strong bass.", 39.00, R.drawable.p5));
        list.add(new Product(6, "Laptop Stand", "Adjustable stand for better posture.", 24.99, R.drawable.p6));
        return list;
    }

    public static Product findById(int id) {
        for (Product p : getProducts()) {
            if (p.id == id) return p;
        }
        return null;
    }
}
