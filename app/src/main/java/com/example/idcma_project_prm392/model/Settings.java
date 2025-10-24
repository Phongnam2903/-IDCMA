package com.example.idcma_project_prm392.model;

public class Settings {
    private String userId;
    private boolean twoFactorEnabled;
    private String lastPasswordChange;
    private String notificationPreference;

    public Settings() {}

    public Settings(String userId, boolean twoFactorEnabled, String lastPasswordChange, String notificationPreference) {
        this.userId = userId;
        this.twoFactorEnabled = twoFactorEnabled;
        this.lastPasswordChange = lastPasswordChange;
        this.notificationPreference = notificationPreference;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public String getLastPasswordChange() {
        return lastPasswordChange;
    }

    public void setLastPasswordChange(String lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }

    public String getNotificationPreference() {
        return notificationPreference;
    }

    public void setNotificationPreference(String notificationPreference) {
        this.notificationPreference = notificationPreference;
    }
}
