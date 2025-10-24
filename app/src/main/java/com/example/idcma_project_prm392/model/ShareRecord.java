package com.example.idcma_project_prm392.model;

public class ShareRecord {
    private String certificateId;
    private String userId;
    private String recipientEmail;
    private String shareDate;
    private String link;
    private boolean expired;

    public ShareRecord() {}

    public ShareRecord(String certificateId, String userId, String recipientEmail,
                       String shareDate, String link, boolean expired) {
        this.certificateId = certificateId;
        this.userId = userId;
        this.recipientEmail = recipientEmail;
        this.shareDate = shareDate;
        this.link = link;
        this.expired = expired;
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

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getShareDate() {
        return shareDate;
    }

    public void setShareDate(String shareDate) {
        this.shareDate = shareDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
