package com.koi.koimart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koi.koimart.R;
import com.koi.koimart.adapter.CartAdapter;
import com.koi.koimart.db.DbHelper;
import com.koi.koimart.model.CartItem;
import com.koi.koimart.util.SessionManager;

import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangedListener {

    RecyclerView rvCart;
    TextView txtTotal;
    Button btnCheckout;

    DbHelper db;
    SessionManager session;
    List<CartItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        db = new DbHelper(this);
        session = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbarCart);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvCart = findViewById(R.id.rvCart);
        txtTotal = findViewById(R.id.txtCartTotal);
        btnCheckout = findViewById(R.id.btnCheckout);

        rvCart.setLayoutManager(new LinearLayoutManager(this));

        loadCart();

        btnCheckout.setOnClickListener(v -> {
            if (items == null || items.isEmpty()) return;
            startActivity(new Intent(this, CheckoutActivity.class));
        });
    }

    private void loadCart() {
        items = db.getCartItems(session.getUserId());
        rvCart.setAdapter(new CartAdapter(this, items, db, this));
        updateTotal();
        btnCheckout.setEnabled(items != null && !items.isEmpty());
    }

    private void updateTotal() {
        double total = db.getCartTotal(session.getUserId());
        txtTotal.setText("Total: $" + String.format("%.2f", total));
    }

    @Override
    public void onCartChanged() {
        updateTotal();
        btnCheckout.setEnabled(items != null && !items.isEmpty());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
