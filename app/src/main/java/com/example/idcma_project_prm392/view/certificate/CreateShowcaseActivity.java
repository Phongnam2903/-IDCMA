package com.example.idcma_project_prm392.view.certificate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.adapter.SelectableCertificateAdapter;
import com.example.idcma_project_prm392.model.Certificate;
import com.example.idcma_project_prm392.repository.CertificateRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CreateShowcaseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SelectableCertificateAdapter adapter;
    private CertificateRepository certificateRepository;
    private List<Certificate> allCertificates = new ArrayList<>();

    private EditText etReportTitle;
    private Button btnGenerate;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_showcase);

        // Khởi tạo Repository (để đọc từ Room)
        certificateRepository = new CertificateRepository(this);

        // Ánh xạ UI
        etReportTitle = findViewById(R.id.edit_text_report_title);
        btnGenerate = findViewById(R.id.button_generate_report);
        progressBar = findViewById(R.id.progressBar_showcase);
        recyclerView = findViewById(R.id.recycler_view_selectable_certs);

        // Cài đặt RecyclerView
        setupRecyclerView();

        // Tải dữ liệu từ Room (offline)
        loadCertificatesFromRoom();

        // Gán sự kiện cho nút
        btnGenerate.setOnClickListener(v -> generateOfflineShowcase());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SelectableCertificateAdapter(allCertificates);
        recyclerView.setAdapter(adapter);
    }

    // Liên kết với Chức năng 4 (Dashboard): Tải tất cả chứng chỉ
    private void loadCertificatesFromRoom() {
        progressBar.setVisibility(View.VISIBLE);
        // Chạy trên luồng nền (giống code của bạn trong CertificateDetailActivity)
        new Thread(() -> {
            // Lấy tất cả chứng chỉ từ Room
            List<Certificate> certs = certificateRepository.getAllCertificates();

            // Cập nhật UI trên luồng chính
            runOnUiThread(() -> {
                allCertificates.clear();
                allCertificates.addAll(certs);
                adapter.notifyDataSetChanged(); // Báo cho adapter biết có dữ liệu mới
                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }

    /**
     * Hàm này sẽ tạo báo cáo text và mở ShareSheet
     */
    private void generateOfflineShowcase() {
        // 1. Lấy tiêu đề
        String title = etReportTitle.getText().toString().trim();
        if (title.isEmpty()) {
            title = "Hồ sơ Chứng chỉ của tôi"; // Tiêu đề mặc định
        }

        // 2. Lấy danh sách ID đã chọn từ Adapter
        Set<Long> selectedIds = adapter.getSelectedIds();
        if (selectedIds.isEmpty()) {
            Toast.makeText(this, "Bạn chưa chọn chứng chỉ nào", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // 3. Xây dựng chuỗi Text (Phải chạy trên luồng nền
        //    vì nó gọi hàm 'getCertificateById' nhiều lần)
        String finalTitle = title;
        new Thread(() -> {
            StringBuilder report = new StringBuilder();
            report.append("--- ").append(finalTitle).append(" ---\n\n");

            int count = 1;
            for (Long id : selectedIds) {
                // Liên kết với Chức năng 6 (View Detail):
                // Tái sử dụng logic lấy 1 cert
                Certificate cert = certificateRepository.getCertificateById(id);
                if (cert != null) {
                    report.append(count).append(". ").append(cert.getName()).append("\n");
                    report.append("   Tổ chức: ").append(cert.getIssuer()).append("\n");
                    report.append("   Ngày cấp: ").append(cert.getIssueDate()).append("\n");
                    if (cert.getExpiryDate() != null && !cert.getExpiryDate().isEmpty()) {
                        report.append("   Hết hạn: ").append(cert.getExpiryDate()).append("\n");
                    }
                    report.append("\n"); // Thêm một dòng trống
                    count++;
                }
            }

            // 4. Gửi chuỗi text qua ShareSheet (trên luồng chính)
            //    Liên kết với Chức năng 7 (Share): Tái sử dụng Intent
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, finalTitle); // Tiêu đề (cho email)
                shareIntent.putExtra(Intent.EXTRA_TEXT, report.toString()); // Nội dung text

                startActivity(Intent.createChooser(shareIntent, "Chia sẻ hồ sơ qua"));
            });
        }).start();
    }
}