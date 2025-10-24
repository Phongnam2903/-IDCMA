package com.example.idcma_project_prm392.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Reminder {
    @DocumentId
    private String id;
    private String certificateId;
    private String userId;
    private String remindAt;   // ISO 8601 trigger time
    private String channel;    // local, push
    private boolean enabled;

    public Reminder() {}

    public Reminder(String certificateId, String userId, String remindAt, String channel, boolean enabled) {
        this.certificateId = certificateId;
        this.userId = userId;
        this.remindAt = remindAt;
        this.channel = channel;
        this.enabled = enabled;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRemindAt() { return remindAt; }
    public void setRemindAt(String remindAt) { this.remindAt = remindAt; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
