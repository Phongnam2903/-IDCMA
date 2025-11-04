package com.example.idcma_project_prm392.view.report;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;

/**
 * Activity để export summary report của tất cả certificates
 * 
 * TODO: Implement các tính năng sau:
 * 1. RadioGroup để chọn format:
 *    - PDF
 *    - CSV
 * 2. CheckBox options:
 *    - Include archived certificates
 *    - Include expired certificates
 *    - Include certificate images/files
 * 3. Button "Generate Report"
 * 4. Progress dialog khi đang generate
 * 5. Generate report file:
 *    - PDF: sử dụng library như iText hoặc Android PDF
 *    - CSV: format data thành CSV format
 * 6. Save file to Downloads folder hoặc app storage
 * 7. Save ReportRequest vào Room Database
 * 8. Option để share/download generated file
 * 9. Hiển thị list các reports đã generate (nếu có)
 */
public class ExportReportActivity extends AppCompatActivity {

    private RadioGroup radioGroupFormat;
    private Button btnGenerateReport;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_report);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.exportToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Xuất báo cáo");
        }

        // TODO: Initialize views
        // TODO: Setup button listeners
        // TODO: Implement report generation logic
        // TODO: Implement PDF/CSV generation
        // TODO: Implement file saving và sharing
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

