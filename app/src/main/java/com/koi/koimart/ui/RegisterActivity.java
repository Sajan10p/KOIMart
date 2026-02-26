package com.koi.koimart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.koi.koimart.R;
import com.koi.koimart.db.DbHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText edtName, edtEmail, edtPhone, edtPassword;
    Button btnRegister;
    TextView txtGoLogin;

    DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DbHelper(this);

        edtName = findViewById(R.id.edtRegName);
        edtEmail = findViewById(R.id.edtRegEmail);
        edtPhone = findViewById(R.id.edtRegPhone);
        edtPassword = findViewById(R.id.edtRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtGoLogin = findViewById(R.id.txtGoLogin);

        btnRegister.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String pass = edtPassword.getText().toString();

            if (name.isEmpty()) { toast("Enter name"); return; }
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) { toast("Enter valid email"); return; }
            if (pass.length() < 4) { toast("Password must be 4+ characters"); return; }

            boolean ok = db.registerUser(name, email, pass, phone);
            if (ok) {
                toast("Registered successfully. Please login.");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                toast("Email already exists. Try another.");
            }
        });

        txtGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
