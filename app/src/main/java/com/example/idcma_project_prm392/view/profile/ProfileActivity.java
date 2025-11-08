package com.example.idcma_project_prm392.view.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.database.AppDatabase;
import com.example.idcma_project_prm392.database.dao.UserDao;
import com.example.idcma_project_prm392.database.entity.UserEntity;
import com.example.idcma_project_prm392.model.User;
import com.example.idcma_project_prm392.utils.SessionManager;
import com.example.idcma_project_prm392.view.auth.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText etFullName, etEmail;
    private Switch switch2FA;
    private Button btnUpdate, btnLogout;

    private SessionManager sessionManager;
    private UserDao userDAO;
    private UserEntity currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // --- Toolbar ---
        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // --- UI binding ---
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        switch2FA = findViewById(R.id.switch2FA);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnLogout = findViewById(R.id.btnLogout);

        sessionManager = new SessionManager(this);
        userDAO = AppDatabase.getInstance(this).userDao(); // lấy DAO từ Room

        loadUserInfo();

        // --- Lưu thay đổi tên/email ---
        btnUpdate.setOnClickListener(v -> updateProfile());

        // --- Logout ---
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finishAffinity();
        });

        // --- Thay đổi 2FA ngay lập tức ---
        switch2FA.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentUser != null) {
                currentUser.setTwoFactorEnabled(isChecked);
                new Thread(() -> userDAO.updateTwoFactor(currentUser.getId(), isChecked)).start();
            }
        });
    }

    private void loadUserInfo() {
        String userId = sessionManager.getUserId();
        if (userId == null) return;

        new Thread(() -> {
            try {
                long id = Long.parseLong(userId);
                currentUser = userDAO.getUserById(id);

                runOnUiThread(() -> {
                    if (currentUser != null) {
                        etFullName.setText(currentUser.getFullName());
                        etEmail.setText(currentUser.getEmail());
                        switch2FA.setChecked(currentUser.getTwoFactorEnabled());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateProfile() {
        if (currentUser == null) return;

        String newName = etFullName.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Tên và email không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setFullName(newName);
        currentUser.setEmail(newEmail);

        new Thread(() -> {
            userDAO.updateUser(currentUser);
            runOnUiThread(() ->
                    Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show());
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
