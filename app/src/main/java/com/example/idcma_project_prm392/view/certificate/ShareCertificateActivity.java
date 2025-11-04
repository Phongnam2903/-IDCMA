package com.example.idcma_project_prm392.view.certificate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;

/**
 * Activity để chia sẻ chứng chỉ một cách bảo mật
 * 
 * TODO: Implement các tính năng sau:
 * 1. Hiển thị thông tin chứng chỉ sẽ được chia sẻ
 * 2. Options để chia sẻ:
 *    - Generate secure link (unique, time-limited URL)
 *    - Send via Email
 *    - Share via Android ShareSheet
 * 3. Tạo ShareRecord trong database
 * 4. Generate secure link (có thể tạo unique token/ID)
 * 5. Lưu sharing history vào database
 * 6. Hiển thị link/share options cho user
 * 7. Copy link to clipboard
 * 8. Set expiration date cho shared link (optional)
 */
public class ShareCertificateActivity extends AppCompatActivity {

    private Button btnGenerateLink, btnShareEmail, btnShareOther;
    private String certificateId; // ID của certificate cần share (từ Intent)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_certificate);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.shareToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chia sẻ chứng chỉ");
        }

        // Get certificate ID from Intent
        certificateId = getIntent().getStringExtra("CERTIFICATE_ID");
        if (certificateId == null || certificateId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID chứng chỉ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // TODO: Initialize views
        // TODO: Load certificate data
        // TODO: Setup button listeners
        // TODO: Implement share logic
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

