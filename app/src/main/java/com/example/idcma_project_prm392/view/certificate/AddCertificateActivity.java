package com.example.idcma_project_prm392.view.certificate;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.model.Certificate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class AddCertificateActivity extends AppCompatActivity {

    private EditText edtName, edtIssuer, edtIssueDate, edtExpiryDate, edtCredentialId;
    private Button btnUploadFile, btnSave;
    private TextView tvFileName;

    private Uri fileUri;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private final ActivityResultLauncher<String> filePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    fileUri = uri;
                    tvFileName.setText(uri.getLastPathSegment());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_certificate);

        // Ánh xạ view
        edtName = findViewById(R.id.edtName);
        edtIssuer = findViewById(R.id.edtIssuer);
        edtIssueDate = findViewById(R.id.edtIssueDate);
        edtExpiryDate = findViewById(R.id.edtExpiryDate);
        edtCredentialId = findViewById(R.id.edtCredentialId);
        btnUploadFile = findViewById(R.id.btnUploadFile);
        btnSave = findViewById(R.id.btnSave);
        tvFileName = findViewById(R.id.tvFileName);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        btnUploadFile.setOnClickListener(v -> filePicker.launch("*/*"));

        edtIssueDate.setOnClickListener(v -> showDatePicker(edtIssueDate));
        edtExpiryDate.setOnClickListener(v -> showDatePicker(edtExpiryDate));

        btnSave.setOnClickListener(v -> saveCertificate());
    }

    private void showDatePicker(EditText target) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) ->
                        target.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1)),
                year, month, day);
        picker.show();
    }

    private void saveCertificate() {
        String name = edtName.getText().toString().trim();
        String issuer = edtIssuer.getText().toString().trim();
        String issueDate = edtIssueDate.getText().toString().trim();
        String expiryDate = edtExpiryDate.getText().toString().trim();
        String credentialId = edtCredentialId.getText().toString().trim();

        if (name.isEmpty() || issuer.isEmpty() || issueDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Đang lưu chứng chỉ...");
        progress.show();

        if (fileUri != null) {
            StorageReference fileRef = storage.getReference()
                    .child("certificates/" + System.currentTimeMillis() + "_" + name + ".pdf");

            fileRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveToFirestore(name, issuer, issueDate, expiryDate, credentialId, uri.toString(), progress);
                    }))
                    .addOnFailureListener(e -> {
                        progress.dismiss();
                        Toast.makeText(this, "Lỗi upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            saveToFirestore(name, issuer, issueDate, expiryDate, credentialId, null, progress);
        }
    }

    private void saveToFirestore(String name, String issuer, String issueDate, String expiryDate,
                                 String credentialId, String fileUrl, ProgressDialog progress) {

        Certificate cert = new Certificate(name, issuer, issueDate, expiryDate, fileUrl, credentialId);

        db.collection("certificates")
                .add(cert)
                .addOnSuccessListener(documentReference -> {
                    progress.dismiss();
                    Toast.makeText(this, "Thêm chứng chỉ thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progress.dismiss();
                    Toast.makeText(this, "Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
