package com.koi.koimart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koi.koimart.R;
import com.koi.koimart.adapter.OrderAdapter;
import com.koi.koimart.db.DbHelper;
import com.koi.koimart.model.OrderItem;
import com.koi.koimart.util.SessionManager;

import java.util.List;

public class PurchaseHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private TextView txtEmpty;
    private Button btnGoHome;

    private DbHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        db = new DbHelper(this);
        session = new SessionManager(this);

        // Must be logged in
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarHistory);
        setSupportActionBar(toolbar);

        // Show back arrow in toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Toolbar back arrow click -> go home
        toolbar.setNavigationOnClickListener(v -> goHome());

        rvOrders = findViewById(R.id.rvOrders);
        txtEmpty = findViewById(R.id.txtHistoryEmpty);
        btnGoHome = findViewById(R.id.btnGoHome);

        // Home button click -> go home
        btnGoHome.setOnClickListener(v -> goHome());

        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        loadOrders();
    }

    private void loadOrders() {
        int userId = session.getUserId();
        List<OrderItem> orders = db.getOrdersLast6Months(userId);

        if (orders == null || orders.isEmpty()) {
            txtEmpty.setText("No orders in the last 6 months.");
            txtEmpty.setVisibility(View.VISIBLE);
            rvOrders.setAdapter(null);
        } else {
            txtEmpty.setVisibility(View.GONE);
            rvOrders.setAdapter(new OrderAdapter(this, orders));
        }
    }

    // Hardware back button -> go home (prevents app closing)
    @Override
    public void onBackPressed() {
        goHome();
    }

    private void goHome() {
        // CHANGE THIS if your home screen activity is not MainActivity
        Intent intent = new Intent(PurchaseHistoryActivity.this, HomeActivity.class);

        // Clears stack so you don't return to purchase history again
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
            db = null;
        }
    }
}
