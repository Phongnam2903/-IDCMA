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

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo Repository và SessionManager
        userRepository = new UserRepository(this);
        sessionManager = new SessionManager(this);

        // Ánh xạ view
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);

        // Sự kiện nút đăng ký
        btnRegister.setOnClickListener(v -> registerUser());

        // Chuyển sang màn hình đăng nhập
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        // Kiểm tra dữ liệu đầu vào
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // Kiểm tra email có tồn tại trước khi tạo tài khoản
        new Thread(() -> {
            boolean emailExists = userRepository.emailExists(email);
            
            runOnUiThread(() -> {
                if (emailExists) {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    Toast.makeText(this, "Email này đã được đăng ký. Vui lòng dùng email khác.", Toast.LENGTH_LONG).show();
                } else {
                    // Nếu email chưa tồn tại → tạo tài khoản mới
                    createAccount(fullName, email, password);
                }
            });
        }).start();
    }

    private void createAccount(String fullName, String email, String password) {
        new Thread(() -> {
            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(password); // Password sẽ được hash trong repository
            user.setRole("user"); // Default role
            
            long userId = userRepository.register(user);
            
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                
                if (userId > 0) {
                    // Tạo session
                    sessionManager.createSession(String.valueOf(userId), email, fullName);
                    
                    Toast.makeText(this, "Đăng ký thành công! Chào mừng bạn, " + fullName + "!", Toast.LENGTH_SHORT).show();
                    
                    // Chuyển sang DashboardActivity
                    Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Đăng ký thất bại! Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }
}
