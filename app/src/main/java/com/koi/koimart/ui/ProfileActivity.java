package com.koi.koimart.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.koi.koimart.R;
import com.koi.koimart.util.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbarProfile);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        session = new SessionManager(this);

        TextView txtName = findViewById(R.id.txtProfileName);
        TextView txtEmail = findViewById(R.id.txtProfileEmail);

        txtName.setText(session.getUserName());
        txtEmail.setText(session.getUserEmail());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
