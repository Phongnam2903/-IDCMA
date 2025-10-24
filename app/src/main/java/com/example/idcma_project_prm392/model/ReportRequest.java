package com.example.idcma_project_prm392.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ReportRequest {
    @DocumentId
    private String id;
    private String userId;
    private String requestedAt; // ISO 8601
    private String format;      // pdf, csv
    private String status;      // pending, processing, ready, failed
    private String downloadUrl; // when ready

    public ReportRequest() {}

    public ReportRequest(String userId, String requestedAt, String format, String status, String downloadUrl) {
        this.userId = userId;
        this.requestedAt = requestedAt;
        this.format = format;
        this.status = status;
        this.downloadUrl = downloadUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRequestedAt() { return requestedAt; }
    public void setRequestedAt(String requestedAt) { this.requestedAt = requestedAt; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
}
