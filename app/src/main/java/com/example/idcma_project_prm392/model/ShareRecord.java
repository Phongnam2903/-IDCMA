package com.example.idcma_project_prm392.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ShareRecord {
    @DocumentId
    private String id;
    private String certificateId;
    private String userId;      // who shared
    private String createdAt;
    private String expiresAt;
    private String secureUrl;
    private String status;      // Active, Expired, Revoked

    public ShareRecord() {}

    public ShareRecord(String certificateId, String userId, String createdAt, String expiresAt, String secureUrl, String status) {
        this.certificateId = certificateId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.secureUrl = secureUrl;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }

    public String getSecureUrl() { return secureUrl; }
    public void setSecureUrl(String secureUrl) { this.secureUrl = secureUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
