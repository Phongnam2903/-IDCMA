package com.example.idcma_project_prm392.view.report;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.database.AppDatabase;
import com.example.idcma_project_prm392.database.dao.CertificateDao;
import com.example.idcma_project_prm392.database.entity.CertificateEntity;
import com.example.idcma_project_prm392.utils.SessionManager;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExportReportActivity extends AppCompatActivity {

    private RadioGroup rgFormat;
    private Button btnGenerate;
    private ProgressDialog progressDialog;
    private AppDatabase db;
    private SessionManager sessionManager;
    private boolean isPdfFormat = true;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ActivityResultLauncher<Intent> createFileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri fileUri = result.getData().getData();
                    if (fileUri != null) {
                        generateReport(fileUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_report);

        Toolbar toolbar = findViewById(R.id.exportToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rgFormat = findViewById(R.id.rgFormat);
        btnGenerate = findViewById(R.id.btnGenerate);

        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        btnGenerate.setOnClickListener(v -> {
            String userId = sessionManager.getUserId();
            if (userId == null) {
                Toast.makeText(this, "Vui lòng đăng nhập trước khi xuất báo cáo!", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedFormat = rgFormat.getCheckedRadioButtonId();
            isPdfFormat = selectedFormat == R.id.rbPdf;

            String mimeType = isPdfFormat ? "application/pdf" : "text/csv";
            String fileName = isPdfFormat ? "certificate_report.pdf" : "certificate_report.csv";

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_TITLE, fileName);

            createFileLauncher.launch(intent);
        });
    }

    private void generateReport(Uri fileUri) {
        progressDialog = ProgressDialog.show(this,
                "Đang tạo báo cáo", "Vui lòng chờ...", true);

        executor.execute(() -> {
            try {
                CertificateDao dao = db.certificateDao();
                String userId = sessionManager.getUserId();
                List<CertificateEntity> certs = dao.getCertificatesByUserId(userId);

                if (certs == null || certs.isEmpty()) {
                    mainHandler.post(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Không có chứng chỉ nào để xuất báo cáo.", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                if (isPdfFormat) {
                    generatePdfReport(fileUri, certs);
                } else {
                    generateCsvReport(fileUri, certs);
                }

                mainHandler.post(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Báo cáo đã lưu thành công.", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Lỗi khi tạo báo cáo: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void generatePdfReport(Uri pdfUri, List<CertificateEntity> certs) throws Exception {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        paint.setTextSize(12);
        int pageWidth = 595;
        int pageHeight = 842;
        int y = 40;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint titlePaint = new Paint();
        titlePaint.setTextSize(18);
        titlePaint.setFakeBoldText(true);
        canvas.drawText("Certificate Summary Report", 40, y, titlePaint);
        y += 40;

        paint.setTextSize(12);

        for (CertificateEntity c : certs) {
            if (y > pageHeight - 100) {
                pdfDocument.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight,
                        pdfDocument.getPages().size() + 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 40;
            }

            canvas.drawText("Name: " + safe(c.getName()), 40, y, paint);
            y += 20;
            canvas.drawText("Issuer: " + safe(c.getIssuer()), 40, y, paint);
            y += 20;
            canvas.drawText("Credential ID: " + safe(c.getCredentialId()), 40, y, paint);
            y += 20;
            canvas.drawText("Issue Date: " + safe(c.getIssueDate()), 40, y, paint);
            y += 20;
            canvas.drawText("Expiry Date: " + safe(c.getExpiryDate()), 40, y, paint);
            y += 20;

            canvas.drawLine(40, y, pageWidth - 40, y, paint);
            y += 20;
        }

        pdfDocument.finishPage(page);

        try (OutputStream os = getContentResolver().openOutputStream(pdfUri)) {
            if (os == null) throw new Exception("Không thể mở OutputStream cho tệp.");
            pdfDocument.writeTo(os);
        } finally {
            pdfDocument.close();
        }
    }

    private void generateCsvReport(Uri csvUri, List<CertificateEntity> certs) throws Exception {
        try (OutputStream os = getContentResolver().openOutputStream(csvUri)) {
            if (os == null) throw new Exception("Không thể mở OutputStream cho tệp.");

            try (OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                writer.write("Name\tIssuer\tCredential ID\tIssue Date\tExpiry Date\n");

                for (CertificateEntity c : certs) {
                    writer.write(String.format("%s\t%s\t%s\t%s\t%s\n",
                            safe(c.getName()),
                            safe(c.getIssuer()),
                            safe(c.getCredentialId()),
                            safe(c.getIssueDate()),
                            safe(c.getExpiryDate())
                    ));
                }
                writer.flush();
            }
        }
    }

    private String safe(Object obj) {
        if (obj == null) return "";
        return obj.toString()
                .replace("\t", " ")
                .replace("\n", " ")
                .replace("\r", " ")
                .trim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}