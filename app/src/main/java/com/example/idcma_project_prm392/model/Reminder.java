package com.example.idcma_project_prm392.model;

public class Reminder {
    private String certificateId;
    private String userId;
    private String remindDate;   // dd/MM/yyyy
    private boolean enabled;

    public Reminder() {}

    public Reminder(String certificateId, String userId, String remindDate, boolean enabled) {
        this.certificateId = certificateId;
        this.userId = userId;
        this.remindDate = remindDate;
        this.enabled = enabled;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(String remindDate) {
        this.remindDate = remindDate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
