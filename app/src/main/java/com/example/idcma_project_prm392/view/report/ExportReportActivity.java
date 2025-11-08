package com.example.idcma_project_prm392.view.report;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.room.Room;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.webkit.MimeTypeMap;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.database.AppDatabase;
import com.example.idcma_project_prm392.database.dao.CertificateDao;
import com.example.idcma_project_prm392.database.entity.CertificateEntity;
import com.example.idcma_project_prm392.utils.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExportReportActivity extends AppCompatActivity {

    private RadioGroup rgFormat;
    private CheckBox cbIncludeArchived, cbIncludeExpired;
    private Button btnGenerate;
    private ProgressDialog progressDialog;
    private AppDatabase db;
    private SessionManager sessionManager;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_report);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.exportToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rgFormat = findViewById(R.id.rgFormat);
        cbIncludeArchived = findViewById(R.id.cbIncludeArchived);
        cbIncludeExpired = findViewById(R.id.cbIncludeExpired);
        btnGenerate = findViewById(R.id.btnGenerate);

        db = AppDatabase.getInstance(this);

        sessionManager = new SessionManager(this);

        btnGenerate.setOnClickListener(v -> {
            String userId = sessionManager.getUserId();
            if (userId == null) {
                Toast.makeText(this, "Vui lòng đăng nhập trước khi xuất báo cáo!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean includeArchived = cbIncludeArchived.isChecked();
            boolean includeExpired = cbIncludeExpired.isChecked();
            int selectedFormat = rgFormat.getCheckedRadioButtonId();
            boolean isPdf = selectedFormat == R.id.rbPdf;

            progressDialog = ProgressDialog.show(this,
                    "Đang tạo báo cáo", "Vui lòng chờ...", true);

            executor.execute(() -> {
                try {
                    CertificateDao dao = db.certificateDao();
                    List<CertificateEntity> certs = dao.getCertificatesByUserId(userId);
                    File file = isPdf
                            ? generatePdfReport(certs)
                            : generateCsvReport(certs);

                    mainHandler.post(() -> {
                        progressDialog.dismiss();
                        if (file != null && file.exists() && file.length() > 0) {
                            Toast.makeText(this,
                                    "Báo cáo đã lưu: " + file.getAbsolutePath(),
                                    Toast.LENGTH_LONG).show();
                            openFile(file);
                        } else {
                            Toast.makeText(this, "Không thể tạo báo cáo.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    mainHandler.post(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }

    private File generatePdfReport(List<CertificateEntity> certs) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) dir.mkdirs();

            File pdfFile = new File(dir, "certificate_report.pdf");

            PdfDocument pdfDocument = new PdfDocument();
            Paint paint = new Paint();
            paint.setTextSize(12);
            int pageWidth = 595;
            int pageHeight = 842;
            int y = 40;

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            canvas.drawText("Certificate Summary Report ", 40, y, paint);
            y += 30;

            for (CertificateEntity c : certs) {
                if (y > pageHeight - 60) {
                    pdfDocument.finishPage(page);
                    pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.getPages().size() + 1).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    y = 40;
                }

                canvas.drawText("Name: " + c.getName(), 40, y, paint); y += 20;
                canvas.drawText("Issuer: " + c.getIssuer(), 40, y, paint); y += 20;
                canvas.drawText("Credential ID: " + c.getCredentialId(), 40, y, paint); y += 20;
                canvas.drawText("Issue Date: " + c.getIssueDate(), 40, y, paint); y += 20;
                canvas.drawText("Expiry Date: " + c.getExpiryDate(), 40, y, paint); y += 30;
                canvas.drawLine(40, y, pageWidth - 40, y, paint);
                y += 20;
            }

            pdfDocument.finishPage(page);
            FileOutputStream fos = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(fos);
            fos.flush();
            fos.close();
            pdfDocument.close();

            return pdfFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private File generateCsvReport(List<CertificateEntity> certs) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) dir.mkdirs();

            File csvFile = new File(dir, "certificate_report.csv");
            FileOutputStream fos = new FileOutputStream(csvFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);

            writer.write("Name,Issuer,Credential ID,Issue Date,Expiry Date,Archived\n");
            for (CertificateEntity c : certs) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s\n",
                        c.getName(),
                        c.getIssuer(),
                        c.getCredentialId(),
                        c.getIssueDate(),
                        c.getExpiryDate(),
                        c.isArchived()));
            }

            writer.flush();
            writer.close();
            fos.close();
            return csvFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void openFile(File file) {
        try {
            Uri uri = FileProvider.getUriForFile(this,
                    getPackageName() + ".provider", file);
            String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mime);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Mở báo cáo với..."));
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
}
