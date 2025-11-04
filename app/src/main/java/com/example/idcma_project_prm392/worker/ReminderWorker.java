package com.example.idcma_project_prm392.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.view.MyApplication;

public class ReminderWorker extends Worker {

    public static final String KEY_CERT_ID = "CERTIFICATE_ID";
    public static final String KEY_CERT_NAME = "CERTIFICATE_NAME";
    private static final String TAG = "ReminderWorker";

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "WorkManager: Đang thực thi tác vụ nhắc nhở...");

        // Lấy dữ liệu đã được gửi đến
        String certName = getInputData().getString(KEY_CERT_NAME);
        String certId = getInputData().getString(KEY_CERT_ID);

        if (certName == null || certId == null) {
            Log.e(TAG, "WorkManager: Không nhận được Tên hoặc ID");
            return Result.failure();
        }

        // Gọi hàm để hiển thị thông báo
        showNotification(certName, certId);

        Log.d(TAG, "WorkManager: Đã hiển thị thông báo cho: " + certName);
        return Result.success();
    }

    private void showNotification(String certName, String certId) {
        Context context = getApplicationContext();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MyApplication.REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_dialog_alert)
                .setContentTitle("🔔 Chứng chỉ sắp hết hạn!")
                .setContentText("Chứng chỉ '" + certName + "' của bạn sắp hết hạn. Hãy kiểm tra ngay.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        int notificationId = certId.hashCode();

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "WorkManager: Thiếu quyền POST_NOTIFICATIONS. " + e.getMessage());
        }
    }
}