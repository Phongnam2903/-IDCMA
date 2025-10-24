package com.example.idcma_project_prm392.model;

import java.util.List;

public class ExportRequest {
    private ExportFormat format;
    private List<String> certificateIds; // null or empty means export all

    public ExportRequest() {
    }

    public ExportFormat getFormat() {
        return format;
    }

    public void setFormat(ExportFormat format) {
        this.format = format;
    }

    public List<String> getCertificateIds() {
        return certificateIds;
    }

    public void setCertificateIds(List<String> certificateIds) {
        this.certificateIds = certificateIds;
    }
}
