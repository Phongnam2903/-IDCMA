package com.example.idcma_project_prm392.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.idcma_project_prm392.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class CertificateDetailActivity extends AppCompatActivity {

    private Button shareButton;
    private FirebaseFunctions mFunctions;
    // Giả sử bạn có đường dẫn file trên Storage, không phải ID từ Firestore
    private String certificateStoragePath; // Ví dụ: "uploads/userId123/my_cert.pdf"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_detail);

        // Khởi tạo Firebase Functions
        mFunctions = FirebaseFunctions.getInstance();

        // (Lấy certificateStoragePath từ Intent hoặc Firestore)
        // certificateStoragePath = ...

        shareButton = findViewById(R.id.button_share);
        shareButton.setOnClickListener(v -> {
            if (certificateStoragePath != null && !certificateStoragePath.isEmpty()) {
                generateAndShareLink(certificateStoragePath);
            } else {
                Toast.makeText(this, "Không tìm thấy file chứng chỉ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateAndShareLink(String filePath) {
        // (Hiển thị ProgressBar)

        // 1. Chuẩn bị dữ liệu gửi lên Cloud Function
        Map<String, Object> data = new HashMap<>();
        data.put("filePath", filePath);

        // 2. Gọi function tên là "getSecureShareableUrl"
        mFunctions
                .getHttpsCallable("getSecureShareableUrl")
                .call(data)
                .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        // (Ẩn ProgressBar)
                        if (task.isSuccessful()) {
                            // 3. Nhận kết quả
                            Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                            String secureUrl = (String) result.get("shareableLink");

                            // 4. Khởi chạy ShareSheet
                            launchShareSheet(secureUrl);
                        } else {
                            // Lỗi (ví dụ: chưa đăng nhập, file không tồn tại)
                            Log.w("ShareError", "Lỗi gọi function: ", task.getException());
                            Toast.makeText(CertificateDetailActivity.this, "Lỗi tạo link chia sẻ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void launchShareSheet(String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ chứng chỉ");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Bạn có thể xem chứng chỉ của tôi (link hết hạn sau 24h): " + url);
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
    }

    // ... (Code của Chức năng 8 và 9 sẽ ở dưới)
}
