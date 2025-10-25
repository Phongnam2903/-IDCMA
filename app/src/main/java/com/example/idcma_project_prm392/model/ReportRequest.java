package com.example.idcma_project_prm392.model;

import com.google.firebase.firestore.DocumentId;

public class ReportRequest {
    @DocumentId
    private String id;
    private String userId;
    private String format;    // "PDF" or "CSV"
    private String downloadUrl;
    private String requestDate; // dd/MM/yyyy HH:mm
    private String status;      // "Pending", "Ready", "Failed"

    public ReportRequest() {}

    public ReportRequest(String userId, String format, String downloadUrl, String requestDate, String status) {
        this.userId = userId;
        this.format = format;
        this.downloadUrl = downloadUrl;
        this.requestDate = requestDate;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "ReportRequest{id='" + id + "', userId='" + userId + "', format='" + format + "', status='" + status + "'}";
    }
}
