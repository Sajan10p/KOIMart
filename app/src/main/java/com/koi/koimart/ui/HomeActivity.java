package com.koi.koimart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koi.koimart.R;
import com.koi.koimart.adapter.ProductAdapter;
import com.koi.koimart.model.Product;
import com.koi.koimart.util.ProductStore;
import com.koi.koimart.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    SessionManager session;

    RecyclerView rv;
    EditText edtHomeSearch;
    TextView txtFooter;

    List<Product> allProducts = new ArrayList<>();
    List<Product> filteredProducts = new ArrayList<>();
    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarHome);
        if (toolbar != null) setSupportActionBar(toolbar);

        rv = findViewById(R.id.rvProducts);
        edtHomeSearch = findViewById(R.id.edtHomeSearch);
        txtFooter = findViewById(R.id.txtFooter);
        txtFooter.setText("Developed by DevLovers");
        TextView txtWelcome = findViewById(R.id.txtWelcome);
        txtWelcome.setText("Welcome, " + session.getUserName());


        rv.setLayoutManager(new LinearLayoutManager(this));

        allProducts = ProductStore.getProducts();
        filteredProducts = new ArrayList<>(allProducts);

        adapter = new ProductAdapter(this, filteredProducts);
        rv.setAdapter(adapter);

        edtHomeSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }
        });
    }

    private void filterProducts(String query) {
        String q = query.trim().toLowerCase();
        filteredProducts.clear();

        if (q.isEmpty()) {
            filteredProducts.addAll(allProducts);
        } else {
            for (Product p : allProducts) {
                if (p.name.toLowerCase().contains(q) ||
                        p.description.toLowerCase().contains(q)) {
                    filteredProducts.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }

        if (id == R.id.menu_history) {
            startActivity(new Intent(this, PurchaseHistoryActivity.class));
            return true;
        }

        if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }

        if (id == R.id.menu_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Do you want to logout?")
                    .setPositiveButton("Yes", (d, w) -> {
                        session.logout();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
