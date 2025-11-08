package com.example.idcma_project_prm392.view.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.model.User;
import com.example.idcma_project_prm392.repository.UserRepository;
import com.example.idcma_project_prm392.utils.SessionManager;
import com.example.idcma_project_prm392.view.certificate.DashboardActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText loginEmail, loginPassword;
    private MaterialButton btnLogin;
    private TextView tvGoToRegister;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRepository = new UserRepository(this);
        sessionManager = new SessionManager(this);

        // Nếu đã đăng nhập thì bỏ qua màn hình này
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
            return;
        }

        // Ánh xạ view
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> loginUser());
        tvGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void loginUser() {
        String email = loginEmail.getText() != null ? loginEmail.getText().toString().trim() : "";
        String password = loginPassword.getText() != null ? loginPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Login từ Room Database
        new Thread(() -> {
            User user = userRepository.login(email, password);
            
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                
                if (user != null) {
                    // Tạo session
                    sessionManager.createSession(user.getId(), user.getEmail(), user.getFullName());
                    
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }
}
