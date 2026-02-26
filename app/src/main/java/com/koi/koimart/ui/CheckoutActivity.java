package com.koi.koimart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.koi.koimart.R;
import com.koi.koimart.db.DbHelper;
import com.koi.koimart.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    EditText edtName, edtAddress, edtPhone, edtEmail;
    Button btnPlaceOrder;

    DbHelper db;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        db = new DbHelper(this);
        session = new SessionManager(this);

        // Safety: if not logged in, go to login
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarCheckout);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtName = findViewById(R.id.edtShipName);
        edtAddress = findViewById(R.id.edtShipAddress);
        edtPhone = findViewById(R.id.edtShipPhone);
        edtEmail = findViewById(R.id.edtShipEmail);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        // ✅ Autofill from session
        edtName.setText(session.getUserName());
        edtEmail.setText(session.getUserEmail());

        btnPlaceOrder.setOnClickListener(v -> {
            String n = edtName.getText().toString().trim();
            String a = edtAddress.getText().toString().trim();
            String p = edtPhone.getText().toString().trim();
            String e = edtEmail.getText().toString().trim();

            if (TextUtils.isEmpty(n)) { toast("Enter full name"); return; }
            if (TextUtils.isEmpty(a)) { toast("Enter address"); return; }
            if (TextUtils.isEmpty(p)) { toast("Enter phone"); return; }
            if (TextUtils.isEmpty(e) || !Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                toast("Enter valid email");
                return;
            }

            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            long orderId = db.placeOrder(session.getUserId(), date, n, a, p, e);

            if (orderId != -1) {
                toast("Order placed successfully. Order #" + orderId);
                startActivity(new Intent(this, PurchaseHistoryActivity.class));
                finishAffinity();
            } else {
                toast("Failed to place order");
            }
        });
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
