package com.example.idcma_project_prm392.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "user_setting")
public class SettingsEntity {
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "userId")
    private String userId;

    @ColumnInfo(name = "two_factor_enabled")
    private boolean twoFactorEnabled;

    @ColumnInfo(name = "last_password_change")
    private String lastPasswordChange;   // dd/MM/yyyy

    @ColumnInfo(name = "notification_preference")
    private String notificationPreference; // e.g. "FCM", "Local", "None"

    public SettingsEntity() {}

    public SettingsEntity(String userId, boolean twoFactorEnabled, String lastPasswordChange, String notificationPreference) {
        this.userId = userId;
        this.twoFactorEnabled = twoFactorEnabled;
        this.lastPasswordChange = lastPasswordChange;
        this.notificationPreference = notificationPreference;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public boolean isTwoFactorEnabled() { return twoFactorEnabled; }
    public void setTwoFactorEnabled(boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }

    public String getLastPasswordChange() { return lastPasswordChange; }
    public void setLastPasswordChange(String lastPasswordChange) { this.lastPasswordChange = lastPasswordChange; }

    public String getNotificationPreference() { return notificationPreference; }
    public void setNotificationPreference(String notificationPreference) { this.notificationPreference = notificationPreference; }

    @Override
    public String toString() {
        return "Settings{id='" + id + "', userId='" + userId + "', 2FA=" + twoFactorEnabled + "}";
    }
}
