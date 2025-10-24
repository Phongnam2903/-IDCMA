package com.example.idcma_project_prm392.model;

public class ReportRequest {
    private String userId;
    private String format; // "PDF" hoặc "CSV"
    private String downloadUrl;
    private String requestDate;

    public ReportRequest() {}

    public ReportRequest(String userId, String format, String downloadUrl, String requestDate) {
        this.userId = userId;
        this.format = format;
        this.downloadUrl = downloadUrl;
        this.requestDate = requestDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }
}
