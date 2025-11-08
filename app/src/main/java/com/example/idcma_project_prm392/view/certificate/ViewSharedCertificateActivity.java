package com.example.idcma_project_prm392.view.certificate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.database.entity.ShareRecordEntity;
import com.example.idcma_project_prm392.model.Certificate;
import com.example.idcma_project_prm392.repository.CertificateRepository;
import com.example.idcma_project_prm392.repository.ShareRecordRepository;
import com.example.idcma_project_prm392.utils.DateUtils;
import com.example.idcma_project_prm392.utils.LocalStorageHelper;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity để xem chứng chỉ từ share link (read-only)
 */
public class ViewSharedCertificateActivity extends AppCompatActivity {

    private TextView tvCertName, tvIssuer, tvCredentialId, tvIssueDate, tvExpiryDate;
    private TextView tvStatus, tvFileType;
    private ImageView imgCertificate;
    private MaterialCardView cardFile, tvExpiredWarning;
    private ProgressBar progressBar;
    private View expiryWarningBanner;

    private CertificateRepository certificateRepository;
    private ShareRecordRepository shareRecordRepository;
    private String shareToken;
    private ShareRecordEntity shareRecord;
    private Certificate certificate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shared_certificate);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.sharedToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chứng chỉ được chia sẻ");
        }

        certificateRepository = new CertificateRepository(this);
        shareRecordRepository = new ShareRecordRepository(this);

        initViews();

        // Get token from Intent
        shareToken = getTokenFromIntent();
        if (shareToken == null || shareToken.isEmpty()) {
            Toast.makeText(this, "Link không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadSharedCertificate();
    }

    private void initViews() {
        tvCertName = findViewById(R.id.tvCertName);
        tvIssuer = findViewById(R.id.tvIssuer);
        tvCredentialId = findViewById(R.id.tvCredentialId);
        tvIssueDate = findViewById(R.id.tvIssueDate);
        tvExpiryDate = findViewById(R.id.tvExpiryDate);
        tvStatus = findViewById(R.id.tvStatus);
        tvFileType = findViewById(R.id.tvFileType);
        tvExpiredWarning = findViewById(R.id.tvExpiredWarning);
        imgCertificate = findViewById(R.id.imgCertificate);
        cardFile = findViewById(R.id.cardFile);
        progressBar = findViewById(R.id.progressBar);
        expiryWarningBanner = findViewById(R.id.expiryWarningBanner);
        
        // Set click listener for image to view full file
        imgCertificate.setOnClickListener(v -> viewFullFile());
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

    private String getTokenFromIntent() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        
        if (data != null) {
            // Handle deep link: idcma://certificate/view?token=TOKEN
            String token = data.getQueryParameter("token");
            if (token != null) return token;
            
            // Handle URL: https://idcma.app/certificate/TOKEN
            String path = data.getPath();
            if (path != null && path.startsWith("/certificate/")) {
                return path.substring("/certificate/".length());
            }
        }
        
        // Fallback: get from Intent extra
        return intent.getStringExtra("SHARE_TOKEN");
    }

    private void loadSharedCertificate() {
        progressBar.setVisibility(View.VISIBLE);
        
        new Thread(() -> {
            try {
                // 1. Get ShareRecord from database
                shareRecord = shareRecordRepository.getShareRecordByToken(shareToken);
                
                if (shareRecord == null) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Link không tồn tại hoặc đã bị xóa", Toast.LENGTH_LONG).show();
                        finish();
                    });
                    return;
                }

                // 2. Check if expired
                if (isLinkExpired(shareRecord)) {
                    // Mark as expired in database
                    shareRecord.setExpired(true);
                    shareRecord.setStatus("Expired");
                    shareRecordRepository.updateShareRecord(shareRecord);
                    
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        if (tvExpiredWarning != null) {
                            tvExpiredWarning.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(this, "Link đã hết hạn", Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                // 3. Check status
                if (!"Active".equals(shareRecord.getStatus())) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Link không còn hoạt động", Toast.LENGTH_LONG).show();
                        finish();
                    });
                    return;
                }

                // 4. Get Certificate from database
                long certId = Long.parseLong(shareRecord.getCertificateId());
                certificate = certificateRepository.getCertificateById(certId);

                if (certificate == null) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Chứng chỉ không tồn tại", Toast.LENGTH_LONG).show();
                        finish();
                    });
                    return;
                }

                // 5. Display certificate (read-only)
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    displayCertificate();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        }).start();
    }

    private boolean isLinkExpired(ShareRecordEntity shareRecord) {
        if (shareRecord.isExpired()) return true;
        
        String expirationDateStr = shareRecord.getExpirationDate();
        if (expirationDateStr == null || expirationDateStr.isEmpty()) return false;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date expirationDate = sdf.parse(expirationDateStr);
            Date now = new Date();
            return now.after(expirationDate);
        } catch (ParseException e) {
            return false;
        }
    }

    private void displayCertificate() {
        if (certificate == null) return;

        // Basic info
        tvCertName.setText(certificate.getName() != null ? certificate.getName() : "N/A");
        tvIssuer.setText(certificate.getIssuer() != null ? certificate.getIssuer() : "N/A");
        tvCredentialId.setText(certificate.getCredentialId() != null ? certificate.getCredentialId() : "Không có");
        tvIssueDate.setText(certificate.getIssueDate() != null ? certificate.getIssueDate() : "N/A");

        // Status - Always show as shared
        tvStatus.setText("🔗 Được chia sẻ");
        tvStatus.setBackgroundColor(0xFF2196F3);

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
        } else {
            cardFile.setVisibility(View.GONE);
        }

        // Show share info
        if (shareRecord != null) {
            String shareInfo = "Chia sẻ bởi: " + shareRecord.getUserId() + "\n" +
                    "Ngày chia sẻ: " + shareRecord.getShareDate() + "\n" +
                    "Hết hạn: " + shareRecord.getExpirationDate();
            // You can add a TextView to show this info if needed
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
}

