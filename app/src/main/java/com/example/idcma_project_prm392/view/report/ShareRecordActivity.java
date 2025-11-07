package com.example.idcma_project_prm392.view.report;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.database.AppDatabase;
import com.example.idcma_project_prm392.database.entity.ShareRecordEntity;
import com.example.idcma_project_prm392.utils.SessionManager;

import java.util.List;

public class ShareRecordActivity extends AppCompatActivity {

    private AppDatabase db;
    private ListView listView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_record);

        Toolbar toolbar = findViewById(R.id.shareRecordToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Danh sách chứng chỉ đã chia sẻ");
        }

        sessionManager = new SessionManager(this);
        listView = findViewById(R.id.listViewShare);
        db = AppDatabase.getInstance(this);

        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem các bản ghi chia sẻ", Toast.LENGTH_LONG).show();
            return;
        }

        String userId = sessionManager.getUserId();

        new Thread(() -> {
            List<ShareRecordEntity> records = db.shareRecordDao().getAllByUser(String.valueOf(userId));

            runOnUiThread(() -> {
                if (records == null || records.isEmpty()) {
                    Toast.makeText(this, "Không có bản ghi chia sẻ nào được tìm thấy", Toast.LENGTH_SHORT).show();
                    listView.setAdapter(null);
                } else {
                    ArrayAdapter<ShareRecordEntity> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_list_item_1,
                            records
                    );
                    listView.setAdapter(adapter);
                    Toast.makeText(this, "Đã tải " + records.size() + " bản ghi chia sẻ.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
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
