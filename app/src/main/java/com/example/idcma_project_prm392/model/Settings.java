package com.example.idcma_project_prm392.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Settings {
    @DocumentId
    private String id;
    private String userId;
    private boolean pushEnabled;
    private boolean localNotificationsEnabled;
    private String reminderLeadTime; // e.g., P30D, P7D

    public Settings() {}

    public Settings(String userId, boolean pushEnabled, boolean localNotificationsEnabled, String reminderLeadTime) {
        this.userId = userId;
        this.pushEnabled = pushEnabled;
        this.localNotificationsEnabled = localNotificationsEnabled;
        this.reminderLeadTime = reminderLeadTime;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public boolean isPushEnabled() { return pushEnabled; }
    public void setPushEnabled(boolean pushEnabled) { this.pushEnabled = pushEnabled; }

    public boolean isLocalNotificationsEnabled() { return localNotificationsEnabled; }
    public void setLocalNotificationsEnabled(boolean localNotificationsEnabled) { this.localNotificationsEnabled = localNotificationsEnabled; }

    public String getReminderLeadTime() { return reminderLeadTime; }
    public void setReminderLeadTime(String reminderLeadTime) { this.reminderLeadTime = reminderLeadTime; }
}
