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
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CertificateDetailActivity extends AppCompatActivity {

    private TextView tvCertName, tvIssuer, tvCredentialId, tvIssueDate, tvExpiryDate;
    private TextView tvStatus, tvTags, tvFileType;
    private ImageView imgCertificate;
    private MaterialCardView cardFile;
    private Button btnShare, btnEdit, btnDelete, btnViewFile;
    private ProgressBar progressBar;
    private View expiryWarningBanner;

    private WorkManager workManager;
    private SharedPreferences sharedPreferences;

    private SwitchCompat reminderSwitch;
    private Spinner reminderSpinner;

    private static final String REMINDER_PREF_KEY_PREFIX = "reminder_pref_";
    private static final String TAG = "CertDetailActivity";

    private CertificateRepository certificateRepository;

    private String certificateId;
    private Certificate certificate;

    //them comment
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

        workManager = WorkManager.getInstance(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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
        tvTags = findViewById(R.id.tvTags);
        tvFileType = findViewById(R.id.tvFileType);
        imgCertificate = findViewById(R.id.imgCertificate);
        cardFile = findViewById(R.id.cardFile);
        btnShare = findViewById(R.id.btnShare);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnViewFile = findViewById(R.id.btnViewFile);
        progressBar = findViewById(R.id.progressBar);
        expiryWarningBanner = findViewById(R.id.expiryWarningBanner);

        reminderSwitch = findViewById(R.id.switch_reminder);
        reminderSpinner = findViewById(R.id.spinner_reminder_time);
    }

    private void setupButtonListeners() {
        btnShare.setOnClickListener(v -> shareCertificate());
        btnEdit.setOnClickListener(v -> editCertificate());
        btnDelete.setOnClickListener(v -> confirmDeleteCertificate());
        btnViewFile.setOnClickListener(v -> viewFullFile());
        imgCertificate.setOnClickListener(v -> viewFullFile());
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

        // Tags
        if (certificate.getTags() != null && !certificate.getTags().isEmpty()) {
            StringBuilder tagsBuilder = new StringBuilder();
            for (String tag : certificate.getTags()) {
                tagsBuilder.append("#").append(tag).append("  ");
            }
            tvTags.setText(tagsBuilder.toString().trim());
            tvTags.setVisibility(View.VISIBLE);
        } else {
            tvTags.setVisibility(View.GONE);
        }

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

        if (certificate != null) {
            setupReminderControls();
        }
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
            return; // Không đặt lịch
        }

        // 5. Chuẩn bị dữ liệu cho Worker
        Data inputData = new Data.Builder()
                .putString(ReminderWorker.KEY_CERT_ID, certificateId)
                .putString(ReminderWorker.KEY_CERT_NAME, certificate.getName())
                .build();

        // 6. Tạo WorkRequest
        OneTimeWorkRequest reminderWork = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(15, TimeUnit.MILLISECONDS)
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

    private void shareCertificate() {
        if (certificate == null) return;

        // 1. Chuẩn bị nội dung Text (Giống như code cũ của bạn)
        StringBuilder shareText = new StringBuilder();
        shareText.append("📜 Chứng chỉ: ").append(certificate.getName()).append("\n");
        shareText.append("🏢 Tổ chức cấp: ").append(certificate.getIssuer()).append("\n");
        shareText.append("📅 Ngày cấp: ").append(certificate.getIssueDate());
        // ... (Thêm các trường khác nếu bạn muốn)

        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        // 2. Lấy đường dẫn file và kiểm tra
        String filePath = certificate.getFileUrl();

        // 3. KIỂM TRA: Nếu có file, chúng ta sẽ đính kèm file
        if (filePath != null && !filePath.isEmpty() && LocalStorageHelper.fileExists(filePath)) {

            // 4. LẤY URI AN TOÀN (Giống hệt hàm viewFullFile của bạn)
            // Chúng ta giả định LocalStorageHelper.getUriFromPath đã dùng FileProvider
            // Nếu hàm viewFullFile của bạn chạy được, thì hàm này cũng sẽ chạy được.
            Uri fileUri = LocalStorageHelper.getUriFromPath(this, filePath);

            if (fileUri != null) {
                // Lấy loại file (MIME type)
                String mimeType = getContentResolver().getType(fileUri);

                // 5. ĐẶT URI VÀO INTENT
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.setType(mimeType); // Ví dụ: "image/jpeg" hoặc "application/pdf"

                // 6. CẤP QUYỀN ĐỌC
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                // Thêm nội dung text vào
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ chứng chỉ: " + certificate.getName());

            } else {
                // Nếu có lỗi khi lấy URI, chỉ chia sẻ text
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ chứng chỉ: " + certificate.getName());
            }

        } else {
            // 7. NẾU KHÔNG CÓ FILE: Chỉ chia sẻ text (Giống code cũ)
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ chứng chỉ: " + certificate.getName());
        }

        // 8. Khởi chạy ShareSheet
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
    }

    private void editCertificate() {
        // TODO: Implement edit functionality
        // Intent intent = new Intent(this, EditCertificateActivity.class);
        // intent.putExtra("CERTIFICATE_ID", certificateId);
        // startActivity(intent);
        
        Toast.makeText(this, "Tính năng chỉnh sửa đang được phát triển", Toast.LENGTH_SHORT).show();
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
        Uri fileUri = LocalStorageHelper.getUriFromPath(this, filePath);
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
