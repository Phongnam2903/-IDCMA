package com.example.idcma_project_prm392.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ShareHistory {
    @DocumentId
    private String id;
    private String certificateId;
    private String recipientEmail;
    private String sharedAt;       // ISO 8601 string
    private String expiryAt;       // ISO 8601 string
    private String status;         // Active, Expired, Revoked
    private String shareUrl;

    public ShareHistory() {}

    public ShareHistory(String certificateId, String recipientEmail, String sharedAt,
                        String expiryAt, String status, String shareUrl) {
        this.certificateId = certificateId;
        this.recipientEmail = recipientEmail;
        this.sharedAt = sharedAt;
        this.expiryAt = expiryAt;
        this.status = status;
        this.shareUrl = shareUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(String sharedAt) {
        this.sharedAt = sharedAt;
    }

    public String getExpiryAt() {
        return expiryAt;
    }

    public void setExpiryAt(String expiryAt) {
        this.expiryAt = expiryAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
