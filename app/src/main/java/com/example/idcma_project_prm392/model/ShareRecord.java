package com.example.idcma_project_prm392.model;

public class ShareRecord {
    private String id;
    private String certificateId;
    private String userId;          // owner who shared
    private String recipientEmail;  // optional
    private String shareDate;       // dd/MM/yyyy HH:mm
    private String link;            // secure link
    private boolean expired;
    private String status;          // e.g. "Active", "Expired", "Revoked"

    public ShareRecord() {}

    public ShareRecord(String certificateId, String userId, String recipientEmail,
                       String shareDate, String link, boolean expired, String status) {
        this.certificateId = certificateId;
        this.userId = userId;
        this.recipientEmail = recipientEmail;
        this.shareDate = shareDate;
        this.link = link;
        this.expired = expired;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getShareDate() { return shareDate; }
    public void setShareDate(String shareDate) { this.shareDate = shareDate; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "ShareRecord{id='" + id + "', certificateId='" + certificateId + "', recipient='" + recipientEmail + "', status='" + status + "'}";
    }
}
