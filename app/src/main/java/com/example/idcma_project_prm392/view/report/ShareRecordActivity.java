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
import com.example.idcma_project_prm392.dto.CertificateReportDTO;
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
            List<CertificateReportDTO> records = db.shareRecordDao().getAllByUser(String.valueOf(userId));

            runOnUiThread(() -> {
                if (records == null || records.isEmpty()) {
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