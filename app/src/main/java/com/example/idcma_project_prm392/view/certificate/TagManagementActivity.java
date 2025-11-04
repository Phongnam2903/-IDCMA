package com.example.idcma_project_prm392.view.certificate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;
import com.google.android.material.chip.ChipGroup;

/**
 * Activity để quản lý tags/categories cho chứng chỉ
 * 
 * TODO: Implement các tính năng sau:
 * 1. Hiển thị tags hiện tại của certificate trong ChipGroup
 * 2. Button "Add Tag" mở Dialog với:
 *    - EditText để nhập tag mới
 *    - Hoặc list các tags có sẵn để chọn
 * 3. Có thể xóa tag bằng cách click vào Chip (với confirm dialog)
 * 4. Lưu tags vào Certificate trong Room Database
 * 5. Update certificate với tags mới
 * 6. Hiển thị suggestions tags phổ biến (IT, Business, Language, Design, etc.)
 * 7. Auto-complete khi nhập tag
 */
public class TagManagementActivity extends AppCompatActivity {

    private ChipGroup chipGroupTags;
    private Button btnAddTag;
    private String certificateId; // ID của certificate (từ Intent)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_management); // TODO: Create layout file

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.tagToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý Tags");
        }

        // Get certificate ID from Intent
        certificateId = getIntent().getStringExtra("CERTIFICATE_ID");
        if (certificateId == null || certificateId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID chứng chỉ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // TODO: Initialize views
        // TODO: Load current tags của certificate
        // TODO: Display tags in ChipGroup
        // TODO: Setup button listeners
        // TODO: Implement add/remove tag logic
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

