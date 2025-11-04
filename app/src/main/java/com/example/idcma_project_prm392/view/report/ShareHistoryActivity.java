package com.example.idcma_project_prm392.view.report;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;

/**
 * Activity để xem lịch sử chia sẻ chứng chỉ
 * 
 * TODO: Implement các tính năng sau:
 * 1. Hiển thị danh sách ShareRecord của user hoặc certificate cụ thể
 * 2. RecyclerView với ShareHistoryAdapter
 * 3. Mỗi item hiển thị:
 *    - Certificate name
 *    - Recipient email (nếu có)
 *    - Share date
 *    - Link status (Active, Expired, Revoked)
 *    - Expiration date
 * 4. Filter options:
 *    - Show all
 *    - Show by certificate
 *    - Show by status
 * 5. Option để revoke shared link
 * 6. Option để copy link
 * 7. Hiển thị empty state nếu không có sharing history
 */
public class ShareHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewShareHistory;
    private String certificateId; // Optional: filter by specific certificate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_history);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.shareHistoryToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Lịch sử chia sẻ");
        }

        // Get optional certificate ID from Intent
        certificateId = getIntent().getStringExtra("CERTIFICATE_ID");

        // TODO: Initialize views
        // TODO: Load ShareRecord từ database
        // TODO: Setup RecyclerView với ShareHistoryAdapter
        // TODO: Implement filter logic
        // TODO: Implement revoke/copy link functionality
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

