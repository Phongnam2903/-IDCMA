package com.example.idcma_project_prm392.view.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.idcma_project_prm392.MainActivity;
import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.model.User;
import com.example.idcma_project_prm392.repository.UserRepository;
import com.example.idcma_project_prm392.utils.SessionManager;
import com.example.idcma_project_prm392.view.certificate.DashboardActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private ProgressDialog progress;

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

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Đang đăng nhập...");

        btnLogin.setOnClickListener(v -> loginUser());
        tvGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.show();

        // Login từ Room Database
        new Thread(() -> {
            User user = userRepository.login(email, password);
            
            runOnUiThread(() -> {
                progress.dismiss();
                
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
