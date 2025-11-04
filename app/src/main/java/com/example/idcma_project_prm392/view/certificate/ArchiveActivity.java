package com.example.idcma_project_prm392.view.certificate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;

/**
 * Activity để xem và quản lý các chứng chỉ đã archive/expired
 * 
 * TODO: Implement các tính năng sau:
 * 1. Hiển thị danh sách certificates đã archive (isArchived = true)
 * 2. RecyclerView với CertificateAdapter
 * 3. Filter options:
 *    - Show only archived
 *    - Show only expired
 *    - Show both
 * 4. Option để unarchive certificate
 * 5. Option để permanently delete certificate
 * 6. Confirm dialog trước khi delete
 * 7. Hiển thị empty state nếu không có certificate nào
 */
public class ArchiveActivity extends AppCompatActivity {

    private RecyclerView recyclerViewArchived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.archiveToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chứng chỉ đã lưu trữ");
        }

        // TODO: Initialize views
        // TODO: Load archived certificates từ database
        // TODO: Setup RecyclerView
        // TODO: Implement filter logic
        // TODO: Implement unarchive/delete functionality
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

