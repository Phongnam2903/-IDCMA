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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ view
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tạo tài khoản...");
        progressDialog.setCancelable(false);

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
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

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

        progressDialog.show();

        // 🔍 Kiểm tra email có tồn tại trước khi tạo tài khoản
        auth.fetchSignInMethodsForEmail(email)
                .addOnSuccessListener(result -> {
                    if (result.getSignInMethods() != null && !result.getSignInMethods().isEmpty()) {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Email này đã được đăng ký. Vui lòng dùng email khác.", Toast.LENGTH_LONG).show();
                    } else {
                        // ✅ Nếu email chưa tồn tại → tạo tài khoản mới
                        createAccount(fullName, email, password);
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Lỗi khi kiểm tra email: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void createAccount(String fullName, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = auth.getCurrentUser().getUid();
                    User user = new User(uid, fullName, email);

                    // Lưu thông tin người dùng vào Firestore
                    db.collection("users").document(uid).set(user)
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Đăng ký thành công! Chào mừng bạn, " + fullName + "!", Toast.LENGTH_SHORT).show();

                                // Chuyển sang MainActivity
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Không thể lưu thông tin người dùng: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    String message = e.getMessage();
                    if (message != null && message.contains("email address is already in use")) {
                        Toast.makeText(this, "Email này đã được sử dụng. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Đăng ký thất bại: " + message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
