package com.example.idcma_project_prm392.view.certificate;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CertificateAdapter adapter;
    private final ArrayList<Certificate> certList = new ArrayList<>();
    private CertificateRepository certificateRepository;
    private SessionManager sessionManager;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.dashboardToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Dashboard - Tổng quan chứng chỉ");
        }

        // Views
        recyclerView = findViewById(R.id.recyclerCertifications);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        certificateRepository = new CertificateRepository(this);
        sessionManager = new SessionManager(this);

        // Adapter
        adapter = new CertificateAdapter(certList);
        recyclerView.setAdapter(adapter);

        loadCertifications();
    }

    private void loadCertifications() {
        progressBar.setVisibility(View.VISIBLE);

        String currentUserId = sessionManager.getUserId();
        if (currentUserId == null || !sessionManager.isLoggedIn()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Vui lòng đăng nhập để xem chứng chỉ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy chứng chỉ của user hiện tại, sau đó LỌC bỏ những cái đã archive
        new Thread(() -> {
            List<Certificate> all = certificateRepository.getCertificatesByUserId(currentUserId);

            // Lọc active (isArchived == false)
            List<Certificate> active = new ArrayList<>();
            if (all != null) {
                for (Certificate c : all) {
                    if (c != null && !c.isArchived()) {
                        active.add(c);
                    }
                }
            }

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);

                certList.clear();
                certList.addAll(active);

                // Empty state
                if (certList.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload lại để phản ánh thay đổi (Archive/Unarchive/Delete) từ màn chi tiết
        loadCertifications();
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
