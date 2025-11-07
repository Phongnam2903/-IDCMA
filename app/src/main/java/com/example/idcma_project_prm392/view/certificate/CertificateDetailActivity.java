package com.example.idcma_project_prm392.view.certificate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.model.Certificate;
import com.example.idcma_project_prm392.repository.CertificateRepository;
import com.example.idcma_project_prm392.utils.DateUtils;
import com.example.idcma_project_prm392.utils.LocalStorageHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CertificateDetailActivity extends AppCompatActivity {

    private TextView tvCertName, tvIssuer, tvCredentialId, tvIssueDate, tvExpiryDate;
    private TextView tvStatus, tvFileType;
    private ImageView imgCertificate;
    private MaterialCardView cardFile;
    private Button btnShare, btnEdit, btnDelete, btnViewFile;
    private ProgressBar progressBar;
    private View expiryWarningBanner;

    // TAG UI (NEW)
    private ChipGroup chipGroupTags;
    private MaterialButton btnAddTag;

    private CertificateRepository certificateRepository;

    private String certificateId;
    private Certificate certificate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_detail);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết chứng chỉ");
        }

        // Initialize Repository
        certificateRepository = new CertificateRepository(this);

        // Initialize views
        initViews();

        // Get certificate ID from intent
        certificateId = getIntent().getStringExtra("CERTIFICATE_ID");
        if (certificateId == null || certificateId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID chứng chỉ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load certificate data
        loadCertificateDetails();

        // Setup button listeners
        setupButtonListeners();
    }

    private void initViews() {
        tvCertName = findViewById(R.id.tvCertName);
        tvIssuer = findViewById(R.id.tvIssuer);
        tvCredentialId = findViewById(R.id.tvCredentialId);
        tvIssueDate = findViewById(R.id.tvIssueDate);
        tvExpiryDate = findViewById(R.id.tvExpiryDate);
        tvStatus = findViewById(R.id.tvStatus);
        tvFileType = findViewById(R.id.tvFileType);
        imgCertificate = findViewById(R.id.imgCertificate);
        cardFile = findViewById(R.id.cardFile);
        btnShare = findViewById(R.id.btnShare);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnViewFile = findViewById(R.id.btnViewFile);
        progressBar = findViewById(R.id.progressBar);
        expiryWarningBanner = findViewById(R.id.expiryWarningBanner);

        // NEW: tag views
        chipGroupTags = findViewById(R.id.chipGroupTags);
        btnAddTag = findViewById(R.id.btnAddTag);
    }

    private void setupButtonListeners() {
        btnShare.setOnClickListener(v -> shareCertificate());
        btnEdit.setOnClickListener(v -> editCertificate());
        btnDelete.setOnClickListener(v -> confirmDeleteCertificate());
        btnViewFile.setOnClickListener(v -> viewFullFile());
        imgCertificate.setOnClickListener(v -> viewFullFile());

        // NEW: add tag
        if (btnAddTag != null) {
            btnAddTag.setOnClickListener(v -> showAddTagDialog());
        }
    }

    private void loadCertificateDetails() {
        progressBar.setVisibility(View.VISIBLE);

        // Load từ Room Database
        new Thread(() -> {
            try {
                long id = Long.parseLong(certificateId);
                certificate = certificateRepository.getCertificateById(id);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    if (certificate != null) {
                        displayCertificateDetails();
                    } else {
                        Toast.makeText(this, "Không tìm thấy chứng chỉ", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } catch (NumberFormatException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "ID chứng chỉ không hợp lệ", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    private void displayCertificateDetails() {
        // Basic Information
        tvCertName.setText(certificate.getName() != null ? certificate.getName() : "N/A");
        tvIssuer.setText(certificate.getIssuer() != null ? certificate.getIssuer() : "N/A");
        tvCredentialId.setText(certificate.getCredentialId() != null ? certificate.getCredentialId() : "Không có");

        // Dates
        tvIssueDate.setText(certificate.getIssueDate() != null ? certificate.getIssueDate() : "N/A");

        String expiryDate = certificate.getExpiryDate();
        if (expiryDate != null && !expiryDate.isEmpty()) {
            tvExpiryDate.setText(expiryDate);

            // Check if expiring soon
            if (DateUtils.isExpiringSoon(expiryDate)) {
                expiryWarningBanner.setVisibility(View.VISIBLE);
                tvExpiryDate.setTextColor(0xFFFF0000); // Red
            } else {
                expiryWarningBanner.setVisibility(View.GONE);
                tvExpiryDate.setTextColor(0xFF666666); // Gray
            }
        } else {
            tvExpiryDate.setText("Không giới hạn");
            expiryWarningBanner.setVisibility(View.GONE);
        }

        // Status
        if (certificate.isArchived()) {
            tvStatus.setText("📦 Đã lưu trữ");
            tvStatus.setBackgroundColor(0xFF9E9E9E);
        } else {
            tvStatus.setText("✅ Đang hoạt động");
            tvStatus.setBackgroundColor(0xFF4CAF50);
        }

        // NEW: Render tags with ChipGroup
        renderTags(certificate.getTags());

        // File Display
        String filePath = certificate.getFileUrl(); // fileUrl is actually filePath now
        if (filePath != null && !filePath.isEmpty() && LocalStorageHelper.fileExists(filePath)) {
            cardFile.setVisibility(View.VISIBLE);

            // Determine file type
            if (filePath.contains(".pdf")) {
                tvFileType.setText("📄 PDF Document");
                imgCertificate.setImageResource(android.R.drawable.ic_menu_gallery);
                imgCertificate.setScaleType(ImageView.ScaleType.CENTER);
            } else {
                // Image file
                tvFileType.setText("🖼️ Image File");
                imgCertificate.setScaleType(ImageView.ScaleType.CENTER_CROP);

                // Load image from local file path with Picasso
                Uri fileUri = LocalStorageHelper.getUriFromPath(filePath);
                if (fileUri != null) {
                    Picasso.get()
                            .load(fileUri)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_report_image)
                            .into(imgCertificate);
                } else {
                    imgCertificate.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }

            btnViewFile.setVisibility(View.VISIBLE);
        } else {
            cardFile.setVisibility(View.GONE);
            btnViewFile.setVisibility(View.GONE);
        }
    }

    // ========== TAGS: render / add / remove ==========

    private void renderTags(List<String> tags) {
        if (chipGroupTags == null) return;
        chipGroupTags.removeAllViews();

        if (tags == null || tags.isEmpty()) return;

        for (String t : tags) {
            Chip chip = new Chip(this);
            chip.setText(t);
            chip.setCloseIconVisible(true);   // show X to remove
            chip.setCheckable(false);
            chip.setOnCloseIconClickListener(v -> removeTag(t));
            chipGroupTags.addView(chip);
        }
    }

    private void removeTag(String tag) {
        if (certificate == null) return;

        new Thread(() -> {
            try {
                List<String> list = certificate.getTags() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(certificate.getTags());
                // remove case-insensitively
                list.removeIf(s -> s.equalsIgnoreCase(tag));

                certificate.setTags(list);
                certificateRepository.updateCertificate(certificate); // persist

                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã xoá tag", Toast.LENGTH_SHORT).show();
                    renderTags(list);
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Lỗi xoá tag: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void showAddTagDialog() {
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("VD: Cloud, Java, Security");

        new MaterialAlertDialogBuilder(this)
                .setTitle("Add Tag")
                .setView(input)
                .setPositiveButton("Add", (d, w) -> {
                    String raw = input.getText() == null ? "" : input.getText().toString();
                    String tag = raw.trim().replaceAll("\\s+", " ");
                    if (tag.isEmpty()) return;

                    new Thread(() -> {
                        try {
                            List<String> list = certificate.getTags() == null
                                    ? new ArrayList<>()
                                    : new ArrayList<>(certificate.getTags());

                            boolean exists = false;
                            for (String s : list) {
                                if (s.equalsIgnoreCase(tag)) { exists = true; break; }
                            }
                            if (!exists) list.add(tag);

                            certificate.setTags(list);
                            certificateRepository.updateCertificate(certificate);

                            final boolean existed = exists;
                            runOnUiThread(() -> {
                                Toast.makeText(this,
                                        existed ? "Tag đã tồn tại" : "Đã thêm tag",
                                        Toast.LENGTH_SHORT).show();
                                renderTags(list);
                            });
                        } catch (Exception e) {
                            runOnUiThread(() ->
                                    Toast.makeText(this, "Lỗi thêm tag: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // =================================================

    private void shareCertificate() {
        if (certificate == null) return;

        StringBuilder shareText = new StringBuilder();
        shareText.append("📜 Chứng chỉ: ").append(certificate.getName()).append("\n");
        shareText.append("🏢 Tổ chức cấp: ").append(certificate.getIssuer()).append("\n");
        shareText.append("📅 Ngày cấp: ").append(certificate.getIssueDate()).append("\n");

        if (certificate.getExpiryDate() != null && !certificate.getExpiryDate().isEmpty()) {
            shareText.append("⏰ Hết hạn: ").append(certificate.getExpiryDate()).append("\n");
        }

        if (certificate.getCredentialId() != null && !certificate.getCredentialId().isEmpty()) {
            shareText.append("🆔 Mã: ").append(certificate.getCredentialId()).append("\n");
        }

        if (certificate.getFileUrl() != null && !certificate.getFileUrl().isEmpty()) {
            shareText.append("\n🔗 Link: ").append(certificate.getFileUrl());
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ chứng chỉ: " + certificate.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());

        startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
    }

    private void editCertificate() {
        Intent intent = new Intent(this, EditCertificateActivity.class);
        intent.putExtra("CERTIFICATE_ID", certificateId);
        startActivity(intent);
    }


    private void confirmDeleteCertificate() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa chứng chỉ")
                .setMessage("Bạn có chắc chắn muốn xóa chứng chỉ này? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCertificate())
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteCertificate() {
        progressBar.setVisibility(View.VISIBLE);

        // Delete from Room Database
        new Thread(() -> {
            try {
                long id = Long.parseLong(certificateId);

                // Delete file from local storage if exists
                if (certificate != null && certificate.getFileUrl() != null && !certificate.getFileUrl().isEmpty()) {
                    LocalStorageHelper.deleteCertificateFile(this, certificate.getFileUrl());
                }

                // Delete certificate from database
                certificateRepository.deleteCertificateById(id);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "✅ Đã xóa chứng chỉ thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Notify calling activity
                    finish();
                });
            } catch (NumberFormatException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi xóa: ID không hợp lệ", Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void viewFullFile() {
        if (certificate == null || certificate.getFileUrl() == null || certificate.getFileUrl().isEmpty()) {
            Toast.makeText(this, "Không có file đính kèm", Toast.LENGTH_SHORT).show();
            return;
        }

        String filePath = certificate.getFileUrl();

        // Check if file exists
        if (!LocalStorageHelper.fileExists(filePath)) {
            Toast.makeText(this, "File không tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get URI from file path
        Uri fileUri = LocalStorageHelper.getUriFromPath(filePath);
        if (fileUri == null) {
            Toast.makeText(this, "Không thể mở file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Open file with external viewer
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, getContentResolver().getType(fileUri));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when returning from edit
        if (certificateId != null) {
            loadCertificateDetails();
        }
    }
}
