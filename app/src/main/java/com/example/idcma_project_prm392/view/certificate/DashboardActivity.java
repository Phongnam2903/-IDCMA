package com.example.idcma_project_prm392.view.certificate;

import android.os.Bundle;
import android.view.MenuItem;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CertificateAdapter adapter;
    private ArrayList<Certificate> certList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Khởi tạo Toolbar
        Toolbar toolbar = findViewById(R.id.dashboardToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Dashboard - Tổng quan chứng chỉ");
        }

        // Ánh xạ view
        recyclerView = findViewById(R.id.recyclerCertifications);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Khởi tạo adapter với danh sách trống
        adapter = new CertificateAdapter(certList);
        recyclerView.setAdapter(adapter);

        loadCertifications();
    }

    private void loadCertifications() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        
        String currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        
        if (currentUserId == null) {
            progressBar.setVisibility(ProgressBar.GONE);
            Toast.makeText(this, "Vui lòng đăng nhập để xem chứng chỉ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy chứng chỉ của user hiện tại, real-time update
        db.collection("certificates")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    
                    if (error != null) {
                        Toast.makeText(this, "Lỗi khi tải dữ liệu: " + error.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        certList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Certificate cert = doc.toObject(Certificate.class);
                            certList.add(cert);
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
                    }
                });
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
