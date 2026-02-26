package com.koi.koimart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.koi.koimart.R;
import com.koi.koimart.model.Product;
import com.koi.koimart.util.ProductStore;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    AutoCompleteTextView actSearch;
    Button btnSearch;

    List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        products = ProductStore.getProducts();

        actSearch = findViewById(R.id.actSearch);
        btnSearch = findViewById(R.id.btnDoSearch);

        ArrayList<String> names = new ArrayList<>();
        for (Product p : products) names.add(p.name);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, names);
        actSearch.setAdapter(adapter);
        actSearch.setThreshold(1);

        actSearch.setOnItemClickListener((parent, view, position, id) -> openSelected(actSearch.getText().toString()));

        btnSearch.setOnClickListener(v -> {
            String q = actSearch.getText().toString().trim();
            if (q.isEmpty()) {
                Toast.makeText(this, "Type a product name", Toast.LENGTH_SHORT).show();
                return;
            }
            openSelected(q);
        });
    }

    private void openSelected(String name) {
        Product found = null;
        for (Product p : products) {
            if (p.name.equalsIgnoreCase(name)) {
                found = p;
                break;
            }
        }
        if (found == null) {
            Toast.makeText(this, "No exact match. Choose from suggestions.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(this, ProductDetailActivity.class);
        i.putExtra("product_id", found.id);
        startActivity(i);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
