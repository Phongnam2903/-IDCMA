package com.example.idcma_project_prm392.view.certificate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.adapter.CertificateAdapter;
import com.example.idcma_project_prm392.model.Certificate;
import com.example.idcma_project_prm392.repository.CertificateRepository;
import com.example.idcma_project_prm392.utils.SessionManager;
import com.example.idcma_project_prm392.view.auth.ProfileActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CertificateAdapter adapter;
    private ArrayList<Certificate> certList = new ArrayList<>();
    private CertificateRepository certificateRepository;
    private SessionManager sessionManager;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Khởi tạo Toolbar
        Toolbar toolbar = findViewById(R.id.dashboardToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Dashboard - Tổng quan chứng chỉ");
        }

        // Ánh xạ view
        recyclerView = findViewById(R.id.recyclerCertifications);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        certificateRepository = new CertificateRepository(this);
        sessionManager = new SessionManager(this);

        // Khởi tạo adapter với danh sách trống
        adapter = new CertificateAdapter(certList);
        recyclerView.setAdapter(adapter);

        loadCertifications();

        // FloatingActionButton để thêm chứng chỉ
        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddCertificateActivity.class);
            startActivity(intent);
        });

        Button btnGoToShowcase = findViewById(R.id.btn_go_to_showcase);
        btnGoToShowcase.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CreateShowcaseActivity.class);
            startActivity(intent);
        });
    }

    private void loadCertifications() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        
        String currentUserId = sessionManager.getUserId();
        
        if (currentUserId == null || !sessionManager.isLoggedIn()) {
            progressBar.setVisibility(ProgressBar.GONE);
            Toast.makeText(this, "Vui lòng đăng nhập để xem chứng chỉ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy chứng chỉ của user hiện tại từ Room Database
        new Thread(() -> {
            List<Certificate> certificates = certificateRepository.getCertificatesByUserId(currentUserId);
            
            runOnUiThread(() -> {
                progressBar.setVisibility(ProgressBar.GONE);
                
                certList.clear();
                if (certificates != null) {
                    certList.addAll(certificates);
                }
                
                // Hiển thị empty state nếu không có chứng chỉ
                if (certList.isEmpty()) {
                    tvEmptyState.setVisibility(TextView.VISIBLE);
                    recyclerView.setVisibility(RecyclerView.GONE);
                } else {
                    tvEmptyState.setVisibility(TextView.GONE);
                    recyclerView.setVisibility(RecyclerView.VISIBLE);
                }
                
                adapter.notifyDataSetChanged();
            });
        }).start();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload certificates khi quay lại activity
        loadCertifications();
    }

    // --- Thêm menu Profile & Search ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Ẩn menu Dashboard vì đã ở Dashboard rồi
        MenuItem dashboardItem = menu.findItem(R.id.action_dashboard);
        if (dashboardItem != null) {
            dashboardItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.action_profile) {
            startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
            return true;
        } else if (itemId == R.id.action_search) {
            startActivity(new Intent(DashboardActivity.this, SearchFilterActivity.class));
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}
