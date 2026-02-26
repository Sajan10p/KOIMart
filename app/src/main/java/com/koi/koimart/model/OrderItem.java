package com.koi.koimart.model;
public class OrderItem {
    public int orderId;
    public String orderDate;
    public double total;

    public OrderItem(int orderId, String orderDate, double total) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.total = total;
    }
}
