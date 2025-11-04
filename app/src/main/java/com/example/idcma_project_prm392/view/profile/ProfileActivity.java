package com.example.idcma_project_prm392.view.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.model.User;
import com.example.idcma_project_prm392.repository.UserRepository;
import com.example.idcma_project_prm392.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvEmail;
    private Button btnLogout;
    private SessionManager sessionManager;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);

        sessionManager = new SessionManager(this);
        userRepository = new UserRepository(this);

        // Load user info from session
        String email = sessionManager.getUserEmail();
        String fullName = sessionManager.getUserName();
        String userId = sessionManager.getUserId();

        if (email != null && !email.isEmpty()) {
            tvEmail.setText(email);
        }

        if (fullName != null && !fullName.isEmpty()) {
            tvFullName.setText("Xin chào, " + fullName);
        } else if (email != null && !email.isEmpty()) {
            tvFullName.setText("Xin chào, " + email.split("@")[0]);
        } else {
            tvFullName.setText("Xin chào");
        }

        // Load full user info from database if needed
        if (userId != null && !userId.isEmpty()) {
            new Thread(() -> {
                try {
                    long id = Long.parseLong(userId);
                    User user = userRepository.getUserById(id);
                    
                    runOnUiThread(() -> {
                        if (user != null && user.getFullName() != null && !user.getFullName().isEmpty()) {
                            tvFullName.setText("Xin chào, " + user.getFullName());
                        }
                    });
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }).start();
        }

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finishAffinity();
        });
    }
}
