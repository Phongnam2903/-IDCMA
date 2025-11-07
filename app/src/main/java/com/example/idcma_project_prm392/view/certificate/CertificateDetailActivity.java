package com.example.idcma_project_prm392.view.certificate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.model.Certificate;
import com.example.idcma_project_prm392.repository.CertificateRepository;
import com.example.idcma_project_prm392.utils.DateUtils;
import com.example.idcma_project_prm392.utils.LocalStorageHelper;
import com.example.idcma_project_prm392.worker.ReminderWorker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

    private Spinner reminderSpinner;
    private SwitchCompat reminderSwitch;

    private static final String REMINDER_PREF_KEY_PREFIX = "reminder_pref_";
    private static final String TAG = "CertDetailActivity";

    private WorkManager workManager;
    private SharedPreferences sharedPreferences;

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

        workManager = WorkManager.getInstance(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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
        reminderSpinner = findViewById(R.id.spinner_reminder_time);
        reminderSwitch = findViewById(R.id.switch_reminder);
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
                        setupReminderControls();
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


    /**
     * Cài đặt logic cho Switch và Spinner nhắc nhở
     */
    private void setupReminderControls() {
        // 1. Cài đặt Spinner Adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderSpinner.setAdapter(adapter);

        // 2. Kiểm tra SharedPreferences xem đã lưu cài đặt cho cert này chưa
        // (Chúng ta dùng -1 nếu chưa cài)
        String prefKey = REMINDER_PREF_KEY_PREFIX + certificateId;
        int savedSelection = sharedPreferences.getInt(prefKey, -1);

        if (savedSelection != -1) {
            // Đã cài -> Bật Switch và chọn đúng Spinner
            reminderSwitch.setChecked(true);
            reminderSpinner.setVisibility(View.VISIBLE);
            reminderSpinner.setSelection(savedSelection);
        } else {
            // Chưa cài -> Tắt Switch
            reminderSwitch.setChecked(false);
            reminderSpinner.setVisibility(View.GONE);
        }

        // 3. Xử lý sự kiện khi Bật/Tắt Switch
        reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Khi bật, hiển thị Spinner và đặt lịch
                reminderSpinner.setVisibility(View.VISIBLE);
                scheduleReminder();
            } else {
                // Khi tắt, ẩn Spinner và hủy lịch
                reminderSpinner.setVisibility(View.GONE);
                cancelReminder();
            }
        });

        // 4. Xử lý sự kiện khi chọn Spinner (để đặt lại lịch)
        reminderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (reminderSwitch.isChecked()) {
                    scheduleReminder();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Lập lịch một tác vụ nhắc nhở bằng WorkManager
     */
    private void scheduleReminder() {
        // 1. Lấy ngày hết hạn từ chứng chỉ
        String expiryDateStr = certificate.getExpiryDate();
        if (expiryDateStr == null || expiryDateStr.isEmpty() || expiryDateStr.equals("Không giới hạn")) {
            Toast.makeText(this, "Không thể đặt lịch: không có ngày hết hạn", Toast.LENGTH_SHORT).show();
            reminderSwitch.setChecked(false); // Tắt switch đi
            return;
        }

        // 2. Chuyển đổi String (dd/MM/yyyy) sang Date
        // QUAN TRỌNG: Đảm bảo format "dd/MM/yyyy" khớp với dữ liệu của bạn
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date expirationDate;
        try {
            expirationDate = sdf.parse(expiryDateStr);
        } catch (ParseException e) {
            Log.e(TAG, "Lỗi parse ngày: " + expiryDateStr, e);
            Toast.makeText(this, "Lỗi định dạng ngày", Toast.LENGTH_SHORT).show();
            reminderSwitch.setChecked(false);
            return;
        }

        // 3. Lấy lựa chọn từ Spinner
        int selectionPosition = reminderSpinner.getSelectedItemPosition();
        long remindBeforeDays;
        switch (selectionPosition) {
            case 0: remindBeforeDays = 7; break; // 1 tuần
            case 1: remindBeforeDays = 30; break; // 1 tháng
            case 2: remindBeforeDays = 90; break; // 3 tháng
            default: remindBeforeDays = 7;
        }

        // 4. Tính toán thời gian delay
        long expirationTimeMs = expirationDate.getTime();
        long reminderTimeMs = expirationTimeMs - TimeUnit.DAYS.toMillis(remindBeforeDays);
        long delayMs = reminderTimeMs - System.currentTimeMillis();

        if (delayMs <= 0) {
            Toast.makeText(this, "Ngày nhắc nhở đã ở trong quá khứ.", Toast.LENGTH_SHORT).show();
            reminderSwitch.setChecked(false);
            return; // Không đặt lịch
        }

        // 5. Chuẩn bị dữ liệu cho Worker
        Data inputData = new Data.Builder()
                .putString(ReminderWorker.KEY_CERT_ID, certificateId)
                .putString(ReminderWorker.KEY_CERT_NAME, certificate.getName())
                .build();

        // 6. Tạo WorkRequest
        OneTimeWorkRequest reminderWork = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(certificateId) // Dùng ID chứng chỉ làm Tag
                .build();

        // 7. Hủy lịch cũ (nếu có) và Lên lịch mới
        workManager.cancelAllWorkByTag(certificateId); // Hủy lịch cũ
        workManager.enqueue(reminderWork); // Đặt lịch mới

        // 8. Lưu lựa chọn vào SharedPreferences
        String prefKey = REMINDER_PREF_KEY_PREFIX + certificateId;
        sharedPreferences.edit().putInt(prefKey, selectionPosition).apply();

        Log.d(TAG, "Đã đặt lịch nhắc cho '" + certificate.getName() + "' sau " + (delayMs / 1000 / 60) + " phút.");
        Toast.makeText(this, "Đã đặt lịch nhắc!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Hủy lịch nhắc nhở cho chứng chỉ này
     */
    private void cancelReminder() {
        // Hủy tất cả tác vụ có Tag là ID của chứng chỉ
        workManager.cancelAllWorkByTag(certificateId);

        // Xóa cài đặt đã lưu
        String prefKey = REMINDER_PREF_KEY_PREFIX + certificateId;
        sharedPreferences.edit().remove(prefKey).apply();

        Toast.makeText(this, "Đã hủy lịch nhắc", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Đã hủy lịch nhắc cho: " + certificateId);
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