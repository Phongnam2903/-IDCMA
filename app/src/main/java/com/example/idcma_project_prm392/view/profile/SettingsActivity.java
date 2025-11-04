package com.example.idcma_project_prm392.view.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.view.auth.LoginActivity;

/**
 * Activity để quản lý profile và security settings
 * 
 * TODO: Implement các tính năng sau:
 * 1. Hiển thị thông tin user hiện tại:
 *    - Profile picture (ImageView, có thể click để change)
 *    - Full name (EditText)
 *    - Email (read-only hoặc EditText)
 * 2. Buttons/Links:
 *    - "Change Password" -> mở ChangePasswordActivity
 *    - "Enable Two-Factor Authentication" (Switch)
 * 3. Lưu Settings vào Room Database:
 *    - twoFactorEnabled
 *    - lastPasswordChange
 *    - notificationPreference
 * 4. Update User profile trong database
 * 5. Upload profile picture to local storage
 * 6. Hiển thị thông báo thành công/thất bại
 * 7. Option để logout
 */
public class SettingsActivity extends AppCompatActivity {

    private ImageView imgProfilePicture;
    private EditText edtFullName, edtEmail;
    private Switch switchTwoFactor;
    private Button btnChangePassword, btnSave, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Cài đặt");
        }

        // TODO: Initialize views
        // TODO: Load user data và settings từ database
        // TODO: Pre-populate form fields
        // TODO: Setup button listeners
        // TODO: Implement save logic
        // TODO: Implement change password flow
        // TODO: Implement logout functionality
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

