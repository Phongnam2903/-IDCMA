package com.example.idcma_project_prm392.view.certificate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Activity để chỉnh sửa thông tin chứng chỉ đã tồn tại
 * 
 * TODO: Implement các tính năng sau:
 * 1. Load thông tin chứng chỉ hiện tại từ database (dựa vào certificateId từ Intent)
 * 2. Pre-populate các EditText fields với dữ liệu hiện tại
 * 3. Cho phép user chỉnh sửa: name, issuer, credentialId, issueDate, expiryDate
 * 4. Cho phép upload file mới (nếu muốn thay đổi)
 * 5. Validate dữ liệu trước khi lưu
 * 6. Update certificate trong Room Database
 * 7. Update file trong local storage nếu có thay đổi
 * 8. Hiển thị thông báo thành công/thất bại
 */
public class EditCertificateActivity extends AppCompatActivity {

    private TextInputEditText edtName, edtIssuer, edtIssueDate, edtExpiryDate, edtCredentialId;
    private Button btnUploadFile, btnSave;
    
    private String certificateId; // ID của certificate cần edit (từ Intent)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_certificate); // Có thể dùng chung layout với AddCertificateActivity

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.addCertToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chỉnh sửa chứng chỉ");
        }

        // Get certificate ID from Intent
        certificateId = getIntent().getStringExtra("CERTIFICATE_ID");
        if (certificateId == null || certificateId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID chứng chỉ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // TODO: Initialize views
        // TODO: Load certificate data from database
        // TODO: Pre-populate form fields
        // TODO: Setup button listeners
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

