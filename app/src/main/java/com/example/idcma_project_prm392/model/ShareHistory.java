package com.example.idcma_project_prm392.model;

public class ShareHistory {
    private String id;
    private String certificateId;
    private ShareMethod method; // how it was shared
    private String recipient;   // email or target label
    private long sharedAtEpochMs;
    private String shareLinkId; // reference to ShareLink if applicable
    private ShareStatus status; // status at the time of viewing

    public ShareHistory() {
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

    public ShareMethod getMethod() {
        return method;
    }

    public void setMethod(ShareMethod method) {
        this.method = method;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public long getSharedAtEpochMs() {
        return sharedAtEpochMs;
    }

    public void setSharedAtEpochMs(long sharedAtEpochMs) {
        this.sharedAtEpochMs = sharedAtEpochMs;
    }

    public String getShareLinkId() {
        return shareLinkId;
    }

    public void setShareLinkId(String shareLinkId) {
        this.shareLinkId = shareLinkId;
    }

    public ShareStatus getStatus() {
        return status;
    }

    public void setStatus(ShareStatus status) {
        this.status = status;
    }
}
