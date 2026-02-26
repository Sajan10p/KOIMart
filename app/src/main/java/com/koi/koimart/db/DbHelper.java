package com.koi.koimart.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.koi.koimart.model.CartItem;
import com.koi.koimart.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "koimart.db";
    private static final int DB_VERSION = 2;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "phone TEXT)");

        db.execSQL("CREATE TABLE cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "product_id INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "qty INTEGER NOT NULL," +
                "UNIQUE(user_id, product_id))");

        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "order_date TEXT NOT NULL," +
                "total REAL NOT NULL," +
                "full_name TEXT NOT NULL," +
                "address TEXT NOT NULL," +
                "phone TEXT NOT NULL," +
                "email TEXT NOT NULL)");

        db.execSQL("CREATE TABLE order_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id INTEGER NOT NULL," +
                "product_id INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "qty INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS order_items");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public boolean registerUser(String name, String email, String password, String phone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("email", email.toLowerCase().trim());
        cv.put("password", password);
        cv.put("phone", phone);

        long r = -1;
        try {
            r = db.insertOrThrow("users", null, cv);
        } catch (Exception ignored) {
        }
        return r != -1;
    }

    public int loginUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        int userId = -1;

        try (Cursor c = db.rawQuery(
                "SELECT id FROM users WHERE email=? AND password=?",
                new String[]{email.toLowerCase().trim(), password}
        )) {
            if (c.moveToFirst()) userId = c.getInt(0);
        }

        return userId;
    }

    public void addToCart(int userId, int productId, String name, double price, int qty) {
        SQLiteDatabase db = getWritableDatabase();

        try (Cursor c = db.rawQuery(
                "SELECT qty FROM cart WHERE user_id=? AND product_id=?",
                new String[]{String.valueOf(userId), String.valueOf(productId)}
        )) {
            if (c.moveToFirst()) {
                int current = c.getInt(0);

                ContentValues cv = new ContentValues();
                cv.put("qty", current + qty);

                db.update("cart", cv, "user_id=? AND product_id=?",
                        new String[]{String.valueOf(userId), String.valueOf(productId)});
            } else {
                ContentValues cv = new ContentValues();
                cv.put("user_id", userId);
                cv.put("product_id", productId);
                cv.put("name", name);
                cv.put("price", price);
                cv.put("qty", qty);

                db.insert("cart", null, cv);
            }
        }
    }

    public List<CartItem> getCartItems(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        List<CartItem> list = new ArrayList<>();

        try (Cursor c = db.rawQuery(
                "SELECT id, product_id, name, price, qty FROM cart WHERE user_id=? ORDER BY id DESC",
                new String[]{String.valueOf(userId)}
        )) {
            while (c.moveToNext()) {
                list.add(new CartItem(
                        c.getInt(0),
                        c.getInt(1),
                        c.getString(2),
                        c.getDouble(3),
                        c.getInt(4)
                ));
            }
        }

        return list;
    }

    public void updateCartQty(int cartId, int qty) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("qty", qty);
        db.update("cart", cv, "id=?", new String[]{String.valueOf(cartId)});
    }

    public void removeCartItem(int cartId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("cart", "id=?", new String[]{String.valueOf(cartId)});
    }

    public void clearCart(int userId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("cart", "user_id=?", new String[]{String.valueOf(userId)});
    }

    public double getCartTotal(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        double total = 0.0;

        try (Cursor c = db.rawQuery(
                "SELECT SUM(price * qty) FROM cart WHERE user_id=?",
                new String[]{String.valueOf(userId)}
        )) {
            if (c.moveToFirst()) total = c.isNull(0) ? 0.0 : c.getDouble(0);
        }

        return total;
    }

    public long placeOrder(int userId, String orderDate, String fullName, String address, String phone, String email) {
        SQLiteDatabase db = getWritableDatabase();
        double total = getCartTotal(userId);

        ContentValues order = new ContentValues();
        order.put("user_id", userId);
        order.put("order_date", orderDate);
        order.put("total", total);
        order.put("full_name", fullName);
        order.put("address", address);
        order.put("phone", phone);
        order.put("email", email);

        long orderId = db.insert("orders", null, order);
        if (orderId == -1) return -1;

        List<CartItem> cart = getCartItems(userId);
        for (CartItem ci : cart) {
            ContentValues oi = new ContentValues();
            oi.put("order_id", orderId);
            oi.put("product_id", ci.productId);
            oi.put("name", ci.name);
            oi.put("price", ci.price);
            oi.put("qty", ci.qty);
            db.insert("order_items", null, oi);
        }

        clearCart(userId);
        return orderId;
    }

    public List<OrderItem> getOrdersLast6Months(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        List<OrderItem> list = new ArrayList<>();

        String sql = "SELECT id, order_date, total FROM orders " +
                "WHERE user_id=? AND date(order_date) >= date('now','-6 months') " +
                "ORDER BY date(order_date) DESC";

        try (Cursor c = db.rawQuery(sql, new String[]{String.valueOf(userId)})) {
            while (c.moveToNext()) {
                list.add(new OrderItem(
                        c.getInt(0),
                        c.getString(1),
                        c.getDouble(2)
                ));
            }
        }

        return list;
    }

    public String getUserNameById(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        String name = "";

        try (Cursor c = db.rawQuery(
                "SELECT name FROM users WHERE id=?",
                new String[]{String.valueOf(userId)}
        )) {
            if (c.moveToFirst()) name = c.getString(0);
        }

        return name;
    }
}
