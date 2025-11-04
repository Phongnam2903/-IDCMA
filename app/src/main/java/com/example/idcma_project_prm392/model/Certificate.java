package com.example.idcma_project_prm392.model;

import java.util.List;

public class Certificate {
    private String id;
    private String userId;           // owner uid
    private String name;
    private String issuer;
    private String credentialId;
    private String issueDate;        // format dd/MM/yyyy
    private String expiryDate;       // format dd/MM/yyyy
    private String fileUrl;          // Local file path (thay vì Firebase Storage URL)
    private boolean isArchived;
    private List<String> tags;

    public Certificate() {}

    public Certificate(String userId, String name, String issuer, String credentialId,
                       String issueDate, String expiryDate, String fileUrl,
                       boolean isArchived, List<String> tags) {
        this.userId = userId;
        this.name = name;
        this.issuer = issuer;
        this.credentialId = credentialId;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.fileUrl = fileUrl;
        this.isArchived = isArchived;
        this.tags = tags;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    public String getCredentialId() { return credentialId; }
    public void setCredentialId(String credentialId) { this.credentialId = credentialId; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public boolean isArchived() { return isArchived; }
    public void setArchived(boolean archived) { isArchived = archived; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    @Override
    public String toString() {
        return "Certificate{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", issuer='" + issuer + '\'' +
                ", credentialId='" + credentialId + '\'' +
                ", issueDate='" + issueDate + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", isArchived=" + isArchived +
                ", tags=" + tags +
                '}';
    }
}
