package com.example.idcma_project_prm392.view.report;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View; // Import View
import android.widget.TextView; // Import TextView
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager; // Import LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView; // Import RecyclerView

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.adapter.ShareRecordAdapter; // Import Adapter mới
import com.example.idcma_project_prm392.database.AppDatabase;
import com.example.idcma_project_prm392.database.entity.ShareRecordEntity;
import com.example.idcma_project_prm392.utils.SessionManager;

import java.util.ArrayList; // Thêm ArrayList
import java.util.List;

public class ShareRecordActivity extends AppCompatActivity {

    private AppDatabase db;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private ShareRecordAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_record);

        Toolbar toolbar = findViewById(R.id.shareRecordToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Danh sách chứng chỉ đã chia sẻ");
        }

        sessionManager = new SessionManager(this);
        recyclerView = findViewById(R.id.recyclerViewShareRecord);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        db = AppDatabase.getInstance(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ShareRecordAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem các bản ghi chia sẻ", Toast.LENGTH_LONG).show();
            recyclerView.setVisibility(View.GONE);
            tvEmptyState.setText("Vui lòng đăng nhập");
            tvEmptyState.setVisibility(View.VISIBLE);
            return;
        }

        String userId = sessionManager.getUserId();

        new Thread(() -> {
            // Xóa dữ liệu cũ (nếu muốn)
            db.shareRecordDao().clearAll();

            // Thêm dữ liệu demo
            db.shareRecordDao().insert(new ShareRecordEntity(
                    1,          // certificateId
                    Long.parseLong(userId),       // userId
                    "alice@example.com",   // recipientEmail
                    "07/11/2025 10:00",    // shareDate
                    "https://example.com/share/1", // link
                    false,      // expired
                    "Active"    // status
            ));

            db.shareRecordDao().insert(new ShareRecordEntity(
                    2,
                    Long.parseLong(userId),
                    "bob@example.com",
                    "06/11/2025 09:30",
                    "https://example.com/share/2",
                    false,
                    "Active"
            ));

            db.shareRecordDao().insert(new ShareRecordEntity(
                    3,
                    Long.parseLong(userId),
                    "charlie@example.com",
                    "05/11/2025 08:45",
                    "https://example.com/share/3",
                    true,
                    "Expired"
            ));
            List<ShareRecordEntity> records = db.shareRecordDao().getAllByUser(userId);

            runOnUiThread(() -> {
                if (records == null || records.isEmpty()) {
                    // Hiển thị trạng thái trống
                    recyclerView.setVisibility(View.GONE);
                    tvEmptyState.setText("Chưa có lịch sử chia sẻ");
                    tvEmptyState.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Không có bản ghi chia sẻ nào được tìm thấy", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.updateList(records);
                    recyclerView.setVisibility(View.VISIBLE);
                    tvEmptyState.setVisibility(View.GONE);
                    Toast.makeText(this, "Đã tải " + records.size() + " bản ghi chia sẻ.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
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