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
import com.koi.koimart.util.SessionManager;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView txtGoRegister;

    DbHelper db;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DbHelper(this);
        session = new SessionManager(this);

        if (session.isLoggedIn()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        edtEmail = findViewById(R.id.edtLoginEmail);
        edtPassword = findViewById(R.id.edtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtGoRegister = findViewById(R.id.txtGoRegister);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String pass = edtPassword.getText().toString();

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                toast("Enter a valid email");
                return;
            }
            if (pass.isEmpty()) {
                toast("Enter password");
                return;
            }

            int userId = db.loginUser(email, pass);
            if (userId != -1) {
                String userName = db.getUserNameById(userId);   // ✅ get name from DB
                session.login(userId, userName, email);         // ✅ store name + email in session

                toast("Login success");
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                toast("Invalid email or password");
            }

        });

        txtGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
