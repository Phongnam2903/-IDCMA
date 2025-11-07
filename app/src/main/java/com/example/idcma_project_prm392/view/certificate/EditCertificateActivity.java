package com.example.idcma_project_prm392.view.certificate;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.model.Certificate;
import com.example.idcma_project_prm392.repository.CertificateRepository;
import com.example.idcma_project_prm392.utils.DateUtils;
import com.example.idcma_project_prm392.utils.LocalStorageHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;

public class EditCertificateActivity extends AppCompatActivity {

    private TextInputEditText edtName, edtIssuer, edtIssueDate, edtExpiryDate, edtCredentialId;
    private Button btnUploadFile, btnSave;

    private String certificateId;
    private CertificateRepository certificateRepository;
    private Certificate certificate;

    // đường dẫn file mới sau khi user chọn (đã copy vào bộ nhớ app)
    private String newFileAbsolutePath = null;

    private final ActivityResultLauncher<String[]> pickDocLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    // Gợi ý: copy file được chọn về bộ nhớ app để thống nhất với fileUrl (đang là local path)
                    String copied = copyPickedToAppStorage(uri);
                    if (copied != null) {
                        newFileAbsolutePath = copied;
                        Toast.makeText(this, "Đã chọn file: " + new File(copied).getName(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Không thể nhập file", Toast.LENGTH_LONG).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_certificate); // dùng chung layout

        Toolbar toolbar = findViewById(R.id.addCertToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chỉnh sửa chứng chỉ");
        }

        certificateRepository = new CertificateRepository(this);

        certificateId = getIntent().getStringExtra("CERTIFICATE_ID");
        if (certificateId == null || certificateId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID chứng chỉ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindViews();
        loadData();

        btnUploadFile.setOnClickListener(v -> pickDocLauncher.launch(new String[]{"application/pdf", "image/*"}));
        edtIssueDate.setOnClickListener(v -> showDatePicker(edtIssueDate));
        edtExpiryDate.setOnClickListener(v -> showDatePicker(edtExpiryDate));
        btnSave.setOnClickListener(v -> onSave());
    }

    private void bindViews() {
        edtName = findViewById(R.id.edtName);
        edtIssuer = findViewById(R.id.edtIssuer);
        edtIssueDate = findViewById(R.id.edtIssueDate);
        edtExpiryDate = findViewById(R.id.edtExpiryDate);
        edtCredentialId = findViewById(R.id.edtCredentialId);
        btnUploadFile = findViewById(R.id.btnUploadFile);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setText("Update");
    }

    private void loadData() {
        new Thread(() -> {
            try {
                long id = Long.parseLong(certificateId);
                certificate = certificateRepository.getCertificateById(id);
                runOnUiThread(() -> {
                    if (certificate == null) {
                        Toast.makeText(this, "Không tìm thấy chứng chỉ", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    prefill();
                });
            } catch (NumberFormatException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "ID không hợp lệ", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    private void prefill() {
        edtName.setText(s(certificate.getName()));
        edtIssuer.setText(s(certificate.getIssuer()));
        edtCredentialId.setText(s(certificate.getCredentialId()));
        edtIssueDate.setText(s(certificate.getIssueDate()));   // format dd/MM/yyyy
        edtExpiryDate.setText(s(certificate.getExpiryDate()));
    }

    private void showDatePicker(TextInputEditText target) {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (v, y, m, d) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(y, m, d, 0, 0, 0);
            target.setText(DateUtils.formatDate(cal.getTime())); // dd/MM/yyyy
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void onSave() {
        String name = t(edtName), issuer = t(edtIssuer), cred = t(edtCredentialId);
        String issue = t(edtIssueDate), expiry = t(edtExpiryDate);

        // Validate
        if (name.isEmpty()) { edtName.setError("Bắt buộc"); return; }
        if (issuer.isEmpty()) { edtIssuer.setError("Bắt buộc"); return; }
        if (DateUtils.tryParse(issue) == null) { edtIssueDate.setError("Ngày không hợp lệ"); return; }
        if (!expiry.isEmpty() && DateUtils.tryParse(expiry) == null) { edtExpiryDate.setError("Ngày không hợp lệ"); return; }

        new Thread(() -> {
            try {
                // cập nhật field
                certificate.setName(name);
                certificate.setIssuer(issuer);
                certificate.setCredentialId(cred);
                certificate.setIssueDate(issue);
                certificate.setExpiryDate(expiry);

                // nếu user chọn file mới → cập nhật đường dẫn
                if (newFileAbsolutePath != null) {
                    // (tuỳ chọn) dọn file cũ
                    try {
                        String old = certificate.getFileUrl();
                        if (old != null && !old.isEmpty()) {
                            LocalStorageHelper.deleteCertificateFile(this, old);
                        }
                    } catch (Exception ignore) {}
                    certificate.setFileUrl(newFileAbsolutePath);
                }

                certificateRepository.updateCertificate(certificate);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Lỗi lưu: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    // copy file từ SAF (content://) về thư mục files của app và trả về absolute path (String)
    private String copyPickedToAppStorage(Uri uri) {
        try {
            ContentResolver cr = getContentResolver();
            String ext = guessExtension(uri);
            File out = new File(getFilesDir(), "cert_" + System.currentTimeMillis() + (ext != null ? ("." + ext) : ""));
            try (InputStream in = cr.openInputStream(uri);
                 FileOutputStream os = new FileOutputStream(out)) {
                if (in == null) return null;
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) os.write(buf, 0, n);
                os.flush();
            }
            return out.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    private String guessExtension(Uri uri) {
        try {
            String mime = getContentResolver().getType(uri);
            if (mime == null) return null;
            String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime);
            return (ext == null || ext.isEmpty()) ? null : ext;
        } catch (Exception e) {
            return null;
        }
    }

    private String t(TextInputEditText e){ return e.getText()==null? "": e.getText().toString().trim(); }
    private String s(String x){ return x==null? "": x; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
