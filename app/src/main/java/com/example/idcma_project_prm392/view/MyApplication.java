package com.example.idcma_project_prm392.view;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApplication extends Application {

    public static final String REMINDER_CHANNEL_ID = "EXPIRATION_REMINDER_CHANNEL";

    @Override
    public void onCreate() {
        super.onCreate();

        // Gọi hàm này khi ứng dụng vừa khởi động
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nhắc nhở hết hạn chứng chỉ";
            String description = "Kênh thông báo cho các chứng chỉ sắp hết hạn";
            int importance = NotificationManager.IMPORTANCE_HIGH; // Đặt ưu tiên cao

            NotificationChannel channel = new NotificationChannel(REMINDER_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}