package com.example.idcma_project_prm392.model;

import com.google.firebase.firestore.DocumentId;

/**
 * Represents a certificate record stored in Firestore.
 */
public class Certificate {

    @DocumentId
    private String id;           // ID tự động sinh trong Firestore
    private String name;         // Tên chứng chỉ
    private String issuer;       // Tổ chức cấp
    private String issueDate;    // Ngày cấp (String format dd/MM/yyyy)
    private String expiryDate;   // Ngày hết hạn
    private String fileUrl;      // URL ảnh hoặc file chứng chỉ
    private String credentialId; // Mã định danh (nếu có)

    // Bắt buộc cần constructor trống để Firestore mapping được
    public Certificate() {}

    public Certificate(String name, String issuer, String issueDate, String expiryDate, String fileUrl, String credentialId) {
        this.name = name;
        this.issuer = issuer;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.fileUrl = fileUrl;
        this.credentialId = credentialId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getCredentialId() { return credentialId; }
    public void setCredentialId(String credentialId) { this.credentialId = credentialId; }
}
