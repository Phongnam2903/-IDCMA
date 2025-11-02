package com.example.idcma_project_prm392.view.certificate;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.model.Certificate;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AddCertificateActivity extends AppCompatActivity {

    private TextInputEditText edtName, edtIssuer, edtIssueDate, edtExpiryDate, edtCredentialId;
    private Button btnUploadFile, btnSave;
    private TextView tvFileName, tvProgressMessage;
    private ImageView imgFilePreview;
    private FrameLayout progressOverlay;

    private Uri fileUri;
    private String fileType = "";
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    // File picker launcher for images and PDFs
    private final ActivityResultLauncher<String> filePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    fileUri = uri;
                    handleFileSelection(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_certificate);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.addCertToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thêm chứng chỉ mới");
        }

        // Initialize views
        edtName = findViewById(R.id.edtName);
        edtIssuer = findViewById(R.id.edtIssuer);
        edtIssueDate = findViewById(R.id.edtIssueDate);
        edtExpiryDate = findViewById(R.id.edtExpiryDate);
        edtCredentialId = findViewById(R.id.edtCredentialId);
        btnUploadFile = findViewById(R.id.btnUploadFile);
        btnSave = findViewById(R.id.btnSave);
        tvFileName = findViewById(R.id.tvFileName);
        imgFilePreview = findViewById(R.id.imgFilePreview);
        progressOverlay = findViewById(R.id.progressOverlay);
        tvProgressMessage = findViewById(R.id.tvProgressMessage);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        // Set click listeners
        btnUploadFile.setOnClickListener(v -> openFilePicker());
        edtIssueDate.setOnClickListener(v -> showDatePicker(edtIssueDate, "Chọn ngày cấp"));
        edtExpiryDate.setOnClickListener(v -> showDatePicker(edtExpiryDate, "Chọn ngày hết hạn"));
        btnSave.setOnClickListener(v -> saveCertificate());
    }

    private void openFilePicker() {
        // Accept images and PDFs
        filePicker.launch("*/*");
    }

    private void handleFileSelection(Uri uri) {
        try {
            // Get file name
            String fileName = getFileName(uri);
            tvFileName.setText(fileName != null ? fileName : "File đã chọn");

            // Get MIME type
            ContentResolver contentResolver = getContentResolver();
            String mimeType = contentResolver.getType(uri);
            
            if (mimeType != null) {
                if (mimeType.startsWith("image/")) {
                    // Show image preview
                    fileType = "image";
                    imgFilePreview.setVisibility(View.VISIBLE);
                    Picasso.get()
                            .load(uri)
                            .resize(300, 300)
                            .centerCrop()
                            .into(imgFilePreview);
                } else if (mimeType.equals("application/pdf")) {
                    // PDF file
                    fileType = "pdf";
                    imgFilePreview.setVisibility(View.GONE);
                    tvFileName.setText("📄 " + fileName);
                } else {
                    // Unsupported file type
                    Toast.makeText(this, "Chỉ hỗ trợ PDF và ảnh (JPG, PNG)", Toast.LENGTH_SHORT).show();
                    fileUri = null;
                    imgFilePreview.setVisibility(View.GONE);
                    tvFileName.setText("Chưa chọn file");
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi đọc file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void showDatePicker(TextInputEditText target, String title) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String dateStr = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1);
                    target.setText(dateStr);
                },
                year, month, day
        );
        picker.setTitle(title);
        picker.show();
    }

    private void saveCertificate() {
        // Get input values
        String name = edtName.getText() != null ? edtName.getText().toString().trim() : "";
        String issuer = edtIssuer.getText() != null ? edtIssuer.getText().toString().trim() : "";
        String issueDate = edtIssueDate.getText() != null ? edtIssueDate.getText().toString().trim() : "";
        String expiryDate = edtExpiryDate.getText() != null ? edtExpiryDate.getText().toString().trim() : "";
        String credentialId = edtCredentialId.getText() != null ? edtCredentialId.getText().toString().trim() : "";

        // Validation
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Vui lòng nhập tên chứng chỉ", Toast.LENGTH_SHORT).show();
            edtName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(issuer)) {
            Toast.makeText(this, "Vui lòng nhập tổ chức cấp", Toast.LENGTH_SHORT).show();
            edtIssuer.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(issueDate)) {
            Toast.makeText(this, "Vui lòng chọn ngày cấp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress
        showProgress("Đang lưu chứng chỉ...");

        // Check if user is logged in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            hideProgress();
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Upload file if selected
        if (fileUri != null) {
            uploadFileAndSave(name, issuer, issueDate, expiryDate, credentialId, currentUser.getUid());
        } else {
            // Save without file
            saveToFirestore(name, issuer, issueDate, expiryDate, credentialId, currentUser.getUid(), null);
        }
    }

    private void uploadFileAndSave(String name, String issuer, String issueDate, 
                                   String expiryDate, String credentialId, String userId) {
        tvProgressMessage.setText("Đang tải file lên...");

        // Create unique filename
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileExtension = getFileExtension(fileUri);
        String fileName = "cert_" + timestamp + "." + fileExtension;

        StorageReference fileRef = storage.getReference()
                .child("certificates/" + userId + "/" + fileName);

        fileRef.putFile(fileUri)
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    tvProgressMessage.setText(String.format("Đang tải lên... %.0f%%", progress));
                })
                .addOnSuccessListener(taskSnapshot -> {
                    // Get download URL
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        tvProgressMessage.setText("Đang lưu thông tin...");
                        saveToFirestore(name, issuer, issueDate, expiryDate, credentialId, userId, uri.toString());
                    }).addOnFailureListener(e -> {
                        hideProgress();
                        Toast.makeText(this, "Lỗi khi lấy URL: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                })
                .addOnFailureListener(e -> {
                    hideProgress();
                    Toast.makeText(this, "Lỗi upload file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void saveToFirestore(String name, String issuer, String issueDate, 
                                String expiryDate, String credentialId, String userId, String fileUrl) {

        Certificate cert = new Certificate(
                userId,
                name,
                issuer,
                credentialId,
                issueDate,
                expiryDate,
                fileUrl,
                false,              // isArchived = false
                new ArrayList<>()   // tags = empty
        );

        db.collection("certificates")
                .add(cert)
                .addOnSuccessListener(documentReference -> {
                    hideProgress();
                    Toast.makeText(this, "✅ Thêm chứng chỉ thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    hideProgress();
                    Toast.makeText(this, "❌ Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension != null ? extension : "dat";
    }

    private void showProgress(String message) {
        tvProgressMessage.setText(message);
        progressOverlay.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressOverlay.setVisibility(View.GONE);
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
