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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CertificateDetailActivity extends AppCompatActivity {

    private TextView tvCertName, tvIssuer, tvCredentialId, tvIssueDate, tvExpiryDate;
    private TextView tvStatus, tvFileType;
    private ImageView imgCertificate;
    private MaterialCardView cardFile;
    private Button btnShare, btnEdit, btnDelete, btnViewFile, btnArchive;
    private ProgressBar progressBar;
    private View expiryWarningBanner;

    // Tags
    private ChipGroup chipGroupTags;
    private MaterialButton btnAddTag;

    private CertificateRepository certificateRepository;
    private String certificateId;
    private Certificate certificate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_detail);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết chứng chỉ");
        }

        certificateRepository = new CertificateRepository(this);
        initViews();

        certificateId = getIntent().getStringExtra("CERTIFICATE_ID");
        if (certificateId == null || certificateId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID chứng chỉ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadCertificateDetails();
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
        btnArchive = findViewById(R.id.btnArchive);
        progressBar = findViewById(R.id.progressBar);
        expiryWarningBanner = findViewById(R.id.expiryWarningBanner);

        chipGroupTags = findViewById(R.id.chipGroupTags);
        btnAddTag = findViewById(R.id.btnAddTag);
    }

    private void setupButtonListeners() {
        btnShare.setOnClickListener(v -> shareCertificate());
        btnEdit.setOnClickListener(v -> editCertificate());
        btnDelete.setOnClickListener(v -> confirmDeleteCertificate());
        btnViewFile.setOnClickListener(v -> viewFullFile());
        imgCertificate.setOnClickListener(v -> viewFullFile());
        if (btnAddTag != null) btnAddTag.setOnClickListener(v -> showAddTagDialog());
        if (btnArchive != null) btnArchive.setOnClickListener(v -> toggleArchive());
    }

    private void loadCertificateDetails() {
        progressBar.setVisibility(View.VISIBLE);
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
        // Basic
        tvCertName.setText(certificate.getName() != null ? certificate.getName() : "N/A");
        tvIssuer.setText(certificate.getIssuer() != null ? certificate.getIssuer() : "N/A");
        tvCredentialId.setText(certificate.getCredentialId() != null ? certificate.getCredentialId() : "Không có");
        tvIssueDate.setText(certificate.getIssueDate() != null ? certificate.getIssueDate() : "N/A");

        // Status + nút Archive
        if (certificate.isArchived()) {
            tvStatus.setText("📦 Đã lưu trữ");
            tvStatus.setBackgroundColor(0xFF9E9E9E);
            if (btnArchive != null) btnArchive.setText("Unarchive");
        } else {
            tvStatus.setText("✅ Đang hoạt động");
            tvStatus.setBackgroundColor(0xFF4CAF50);
            if (btnArchive != null) btnArchive.setText("Archive");
        }

        // Expiry
        String expiryDate = certificate.getExpiryDate();
        if (expiryDate != null && !expiryDate.isEmpty()) {
            tvExpiryDate.setText(expiryDate);
            if (DateUtils.isExpiringSoon(expiryDate)) {
                expiryWarningBanner.setVisibility(View.VISIBLE);
                tvExpiryDate.setTextColor(0xFFFF0000);
            } else {
                expiryWarningBanner.setVisibility(View.GONE);
                tvExpiryDate.setTextColor(0xFF666666);
            }
        } else {
            tvExpiryDate.setText("Không giới hạn");
            expiryWarningBanner.setVisibility(View.GONE);
        }

        // Tags
        renderTags(certificate.getTags());

        // File
        String filePath = certificate.getFileUrl();
        if (filePath != null && !filePath.isEmpty() && LocalStorageHelper.fileExists(filePath)) {
            cardFile.setVisibility(View.VISIBLE);

            String lower = filePath.toLowerCase();
            if (lower.endsWith(".pdf")) {
                tvFileType.setText("📄 PDF Document");
                imgCertificate.setImageResource(android.R.drawable.ic_menu_gallery);
                imgCertificate.setScaleType(ImageView.ScaleType.CENTER);
            } else {
                tvFileType.setText("🖼️ Image File");
                imgCertificate.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Uri fileUri = LocalStorageHelper.getUriFromPath(this, filePath);
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

    // ====== Tags ======

    private void renderTags(List<String> tags) {
        if (chipGroupTags == null) return;
        chipGroupTags.removeAllViews();
        if (tags == null || tags.isEmpty()) return;

        for (String t : tags) {
            Chip chip = new Chip(this);
            chip.setText(t);
            chip.setCloseIconVisible(true);
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
                // remove case-insensitively (không dùng removeIf để tránh yêu cầu API)
                for (int i = list.size() - 1; i >= 0; i--) {
                    String s = list.get(i);
                    if (s != null && s.equalsIgnoreCase(tag)) list.remove(i);
                }
                certificate.setTags(list);
                certificateRepository.updateCertificate(certificate);
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
                                if (s != null && s.equalsIgnoreCase(tag)) { exists = true; break; }
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

    // ====== Archive / Unarchive ======

    private void toggleArchive() {
        if (certificate == null) return;

        final boolean target = !certificate.isArchived();
        String title = target ? "Lưu trữ chứng chỉ" : "Khôi phục chứng chỉ";
        String msg   = target ? "Chuyển chứng chỉ vào mục lưu trữ?" : "Khôi phục chứng chỉ vào danh sách chính?";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Xác nhận", (d, w) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    new Thread(() -> {
                        try {
                            certificate.setArchived(target);
                            certificateRepository.updateCertificate(certificate);
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(this, target ? "Đã lưu trữ" : "Đã khôi phục", Toast.LENGTH_SHORT).show();
                                displayCertificateDetails();
                                setResult(RESULT_OK);
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ====== Share / Edit / Delete / View file ======

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
        new Thread(() -> {
            try {
                long id = Long.parseLong(certificateId);

                if (certificate != null && certificate.getFileUrl() != null && !certificate.getFileUrl().isEmpty()) {
                    try { LocalStorageHelper.deleteCertificateFile(this, certificate.getFileUrl()); } catch (Exception ignore) {}
                }

                certificateRepository.deleteCertificateById(id);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "✅ Đã xóa chứng chỉ thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
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
        if (!LocalStorageHelper.fileExists(filePath)) {
            Toast.makeText(this, "File không tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri fileUri = LocalStorageHelper.getUriFromPath(this, filePath);
        if (fileUri == null) {
            Toast.makeText(this, "Không thể mở file", Toast.LENGTH_SHORT).show();
            return;
        }

        String lower = filePath.toLowerCase();
        String mime = getContentResolver().getType(fileUri);
        if (mime == null) {
            if (lower.endsWith(".pdf")) mime = "application/pdf";
            else mime = "image/*";
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // ====== OS lifecycle ======

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (certificateId != null) loadCertificateDetails();
    }
}