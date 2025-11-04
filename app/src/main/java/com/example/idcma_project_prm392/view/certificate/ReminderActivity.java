package com.example.idcma_project_prm392.view.certificate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.idcma_project_prm392.R;

/**
 * Activity để thiết lập nhắc nhở hết hạn cho chứng chỉ
 * 
 * TODO: Implement các tính năng sau:
 * 1. Hiển thị thông tin chứng chỉ (name, expiry date)
 * 2. Switch/Toggle để enable/disable reminder
 * 3. Spinner/Dialog để chọn thời gian nhắc nhở:
 *    - 1 month before
 *    - 2 weeks before
 *    - 1 week before
 *    - 3 days before
 *    - 1 day before
 * 4. Lưu Reminder vào Room Database
 * 5. Schedule local notification using AlarmManager hoặc WorkManager
 * 6. Hiển thị danh sách reminders đã set (nếu có)
 * 7. Cho phép xóa/edit reminder
 */
public class ReminderActivity extends AppCompatActivity {

    private Switch switchEnableReminder;
    private Spinner spinnerReminderTime;
    private Button btnSaveReminder;
    private String certificateId; // ID của certificate (từ Intent)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder); // TODO: Create layout file

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.reminderToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Nhắc nhở hết hạn");
        }

        // Get certificate ID from Intent
        certificateId = getIntent().getStringExtra("CERTIFICATE_ID");
        if (certificateId == null || certificateId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID chứng chỉ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // TODO: Initialize views
        // TODO: Load existing reminder (nếu có)
        // TODO: Setup button listeners
        // TODO: Implement reminder scheduling logic
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

