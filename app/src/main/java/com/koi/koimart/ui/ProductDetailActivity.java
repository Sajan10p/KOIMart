package com.koi.koimart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.koi.koimart.R;
import com.koi.koimart.db.DbHelper;
import com.koi.koimart.model.Product;
import com.koi.koimart.util.ProductStore;
import com.koi.koimart.util.SessionManager;

public class ProductDetailActivity extends AppCompatActivity {

    ImageView img;
    TextView txtName, txtDesc, txtPrice, txtQty;
    Button btnMinus, btnPlus, btnAddToCart, btnGoCart;

    int qty = 1;
    Product product;

    DbHelper db;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        db = new DbHelper(this);
        session = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int pid = getIntent().getIntExtra("product_id", -1);
        product = ProductStore.findById(pid);

        if (product == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        img = findViewById(R.id.imgDetail);
        txtName = findViewById(R.id.txtDetailName);
        txtDesc = findViewById(R.id.txtDetailDesc);
        txtPrice = findViewById(R.id.txtDetailPrice);
        txtQty = findViewById(R.id.txtDetailQty);
        btnMinus = findViewById(R.id.btnQtyMinus);
        btnPlus = findViewById(R.id.btnQtyPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnGoCart = findViewById(R.id.btnGoCart);

        img.setImageResource(product.imageResId);
        txtName.setText(product.name);
        txtDesc.setText(product.description);
        txtPrice.setText("$" + String.format("%.2f", product.price));
        txtQty.setText(String.valueOf(qty));

        btnMinus.setOnClickListener(v -> {
            qty--;
            if (qty < 1) qty = 1;
            txtQty.setText(String.valueOf(qty));
        });

        btnPlus.setOnClickListener(v -> {
            qty++;
            txtQty.setText(String.valueOf(qty));
        });

        btnAddToCart.setOnClickListener(v -> {
            if (!session.isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }
            db.addToCart(session.getUserId(), product.id, product.name, product.price, qty);
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        });

        btnGoCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
