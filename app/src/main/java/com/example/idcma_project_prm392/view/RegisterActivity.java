package com.example.idcma_project_prm392.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.utils.FirebaseUtils;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText etFullName = findViewById(R.id.etFullName);
        EditText etEmail = findViewById(R.id.etRegEmail);
        EditText etPassword = findViewById(R.id.etRegPassword);
        EditText etConfirm = findViewById(R.id.etRegConfirm);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);

        btnRegister.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirm = etConfirm.getText().toString();
            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseUtils.register(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Optionally save displayName to profile
                    if (FirebaseUtils.currentUser() != null) {
                        FirebaseUtils.currentUser().updateProfile(
                                new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullName)
                                        .build()
                        );
                    }
                    startActivity(new Intent(this, DashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvLoginLink.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }
}
