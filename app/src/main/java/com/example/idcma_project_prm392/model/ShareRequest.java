package com.example.idcma_project_prm392.model;

public class ShareRequest {
    private String certificateId;
    private ShareMethod method; // GENERATE_LINK or EMAIL
    private long ttlMillis;     // time to live for link
    private String recipientEmail; // used if method is EMAIL

    public ShareRequest() {
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public ShareMethod getMethod() {
        return method;
    }

    public void setMethod(ShareMethod method) {
        this.method = method;
    }

    public long getTtlMillis() {
        return ttlMillis;
    }

    public void setTtlMillis(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
}
