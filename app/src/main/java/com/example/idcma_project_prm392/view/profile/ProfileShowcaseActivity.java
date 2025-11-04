package com.example.idcma_project_prm392.view.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;

/**
 * Activity để tạo public showcase profile - portfolio công khai các chứng chỉ
 * 
 * TODO: Implement các tính năng sau:
 * 1. Hiển thị danh sách tất cả certificates của user
 * 2. CheckBox trên mỗi certificate item để chọn certificates muốn showcase
 * 3. EditText để nhập custom URL slug (optional)
 * 4. Button "Generate Public Link"
 * 5. Lưu Profile entity vào Room Database với:
 *    - userId
 *    - slug (custom hoặc auto-generate)
 *    - certificateIds (list các ID đã chọn)
 *    - isPublic = true
 *    - publicUrl (generated URL)
 * 6. Hiển thị public URL sau khi tạo
 * 7. Copy URL to clipboard
 * 8. Option để preview public profile
 */
public class ProfileShowcaseActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCertificates;
    private EditText edtCustomSlug;
    private Button btnGenerateLink, btnPreview;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_showcase);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.showcaseToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tạo Public Profile");
        }

        // TODO: Initialize views
        // TODO: Load all user certificates
        // TODO: Setup RecyclerView with CheckBox adapter
        // TODO: Setup button listeners
        // TODO: Implement profile generation logic
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

