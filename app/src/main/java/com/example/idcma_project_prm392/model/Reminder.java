package com.example.idcma_project_prm392.model;

import com.google.firebase.firestore.DocumentId;

public class Reminder {
    @DocumentId
    private String id;
    private String certificateId;
    private String userId;
    private String remindDate;   // dd/MM/yyyy or ISO string
    private boolean enabled;
    private int remindBeforeDays; // egz: 30, 7, 1

    public Reminder() {}

    public Reminder(String certificateId, String userId, String remindDate, boolean enabled, int remindBeforeDays) {
        this.certificateId = certificateId;
        this.userId = userId;
        this.remindDate = remindDate;
        this.enabled = enabled;
        this.remindBeforeDays = remindBeforeDays;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRemindDate() { return remindDate; }
    public void setRemindDate(String remindDate) { this.remindDate = remindDate; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public int getRemindBeforeDays() { return remindBeforeDays; }
    public void setRemindBeforeDays(int remindBeforeDays) { this.remindBeforeDays = remindBeforeDays; }

    @Override
    public String toString() {
        return "Reminder{id='" + id + "', certificateId='" + certificateId + "', remindDate='" + remindDate + "', enabled=" + enabled + "}";
    }
}
